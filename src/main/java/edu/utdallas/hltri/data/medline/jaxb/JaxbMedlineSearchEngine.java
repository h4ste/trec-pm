package edu.utdallas.hltri.data.medline.jaxb;

import edu.utdallas.hltri.data.medline.MedlineSearchEngine;
import edu.utdallas.hltri.data.medline.MedlineSettings;
import edu.utdallas.hltri.framework.ProgressLogger;
import edu.utdallas.hltri.inquire.lucene.DocumentFactory;
import edu.utdallas.hltri.inquire.lucene.LuceneUtils;
import edu.utdallas.hltri.logging.Logger;
import edu.utdallas.hltri.util.Unsafe;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

public class JaxbMedlineSearchEngine extends MedlineSearchEngine<JaxbMedlineArticle> {
  private static final Logger log = Logger.get(JaxbMedlineSearchEngine.class);

  private final String ABSTRACT_FIELD = "abstract";

  public JaxbMedlineSearchEngine(
      String medlineIndexPath,
      String defaultFieldName,
      DocumentFactory<? extends JaxbMedlineArticle> factory) {
    super(getIndexDirectory(medlineIndexPath), JaxbMedlineIndexer.getAnalyzer(),
        defaultFieldName, factory);
  }

  private static Directory getIndexDirectory(String path) {
    try {
      return MMapDirectory.open(Paths.get(path));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<JaxbMedlineArticle> loadArticlesByPmids(Collection<String> pmids) {
    // Temporarily increase the max clause count to accomodate all NCT IDs
    final int prevMaxClauseCount = BooleanQuery.getMaxClauseCount();
    BooleanQuery.setMaxClauseCount(pmids.size());

    log.debug("Generating disjunction query of {} PMIDs", pmids.size());
    // Build a disjunction of all NCT IDs
    final BooleanQuery query = pmids.stream()
        .map(nctId -> new TermQuery(new Term(PMID_FIELD, nctId)))
        .collect(LuceneUtils.toBooleanQuery(Occur.SHOULD));

    return withSearcher(searcher -> {
      try {
        // Do the actual retrieval
        final TopDocs results = searcher.search(query, pmids.size());
        // Reset max clause count to previous value
        BooleanQuery.setMaxClauseCount(prevMaxClauseCount);


        final ScoreDoc[] docs = results.scoreDocs;
        // Sort docs in increasing luceneId for faster reading!
        Arrays.sort(docs, Comparator.comparingInt(sd -> sd.doc));

        log.debug("Retrieved {} trials", results.totalHits);
        assert results.totalHits <= pmids.size();
        if (docs.length < pmids.size()) {
          log.error("Failed to find {} articles", pmids.size() - docs.length);
        }

        final IndexReader reader = searcher.getIndexReader();
        final List<JaxbMedlineArticle> articles = new ArrayList<>();

        for (ScoreDoc doc : docs) {
          final JaxbMedlineArticle article = factory.build(reader, doc.doc);
          articles.add(article);
        }
        return articles;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }
}
