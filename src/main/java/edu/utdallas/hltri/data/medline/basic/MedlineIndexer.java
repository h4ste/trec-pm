package edu.utdallas.hltri.data.medline.basic;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

import com.fasterxml.aalto.sax.SAXParserFactoryImpl;

import edu.utdallas.hltri.data.medline.MedlineArticle;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.SortedSetDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.BytesRef;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import edu.utdallas.hltri.concurrent.BoundedThreadPool;
import edu.utdallas.hltri.framework.ProgressLogger;
import edu.utdallas.hltri.io.AC;
import edu.utdallas.hltri.logging.Logger;

import static edu.utdallas.hltri.inquire.lucene.LuceneSearchEngine.TEXT_FIELD_TYPE;

/**
 * Asynchronous Lucene indexer relying on SAX Parsing;
 * can be invoked to do the indexing process on the command line via its main method
 * Assumes gzipped XML articles.
 * Created by travis on 6/6/2017.
 */
@SuppressWarnings("WeakerAccess")
public class MedlineIndexer implements AC {
  private static final Logger log = Logger.get(MedlineIndexer.class);

  private final ExecutorService   indexingService;
  private final MedlineXmlHandler handler;

  private final SAXParserFactory parserFactory = new SAXParserFactoryImpl();

  private final IndexWriter indexWriter;
  private final ProgressLogger plog;

  private final boolean update;

  public MedlineIndexer(final Path indexPath, final ProgressLogger plog, final boolean update) {
    this.plog = plog;

    try {
      final Directory indexDir = new NIOFSDirectory(indexPath);
      final Analyzer analyzer = new EnglishAnalyzer();
      final IndexWriterConfig config = new IndexWriterConfig(analyzer)
          .setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND)
          .setSimilarity(new BM25Similarity());
      this.indexWriter = new IndexWriter(indexDir, config);
      this.update = update;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    this.indexingService = BoundedThreadPool.create(512, "indexing");
    this.handler = new MedlineXmlHandler(t -> indexingService.execute(() -> this.indexDocument(t)));
  }


  public void indexDocument(final MedlineArticle article) {
    assert !Strings.isNullOrEmpty(article.getPubmedId()) : "blank PMID";

    if (article.getAbstractText().isEmpty()) {
      return;
    }

    log.trace("Indexing article {}", article.getPubmedId());
    final Document luceneDoc = new Document();

    // Raw (non-processed) textual fields
    luceneDoc.add(new Field("pmid", article.getPubmedId(), StringField.TYPE_STORED));

    if (article.getJournalTitle() != null && !article.getJournalTitle().isEmpty()) {
      luceneDoc.add(new SortedSetDocValuesField("journal_title_str",
         new BytesRef(article.getJournalTitle())));
      luceneDoc.add(new Field("journal_title", article.getJournalTitle(), TEXT_FIELD_TYPE));
    }

    if (article.getMeshTerms() != null && !article.getMeshTerms().isEmpty()) {
      for (String term : article.getMeshTerms()) {
        luceneDoc.add(new SortedSetDocValuesField("mesh_terms_str", new BytesRef(term)));
        luceneDoc.add(new Field("mesh_terms", term, TEXT_FIELD_TYPE));
      }
    }

    for (String publicationType : article.getPublicationTypes()) {
      luceneDoc.add(new SortedSetDocValuesField("article_types_str",
          new BytesRef(publicationType)));
      luceneDoc.add(new StoredField("article_types", publicationType));
    }

    if (article.getChemicals() != null && !article.getChemicals().isEmpty()) {
      for (String chemical : article.getChemicals()) {
        luceneDoc.add(new SortedSetDocValuesField("chemicals_str", new BytesRef(chemical)));
        luceneDoc.add(new Field("chemicals", chemical, TEXT_FIELD_TYPE));
      }
    }

    if (article.getArticleTitle() != null) {
      luceneDoc.add(new Field("article_title", article.getArticleTitle(), TEXT_FIELD_TYPE));
    }

    if (article.getCreationDate() != null) {
      luceneDoc.add(new LongPoint("creation_date_p", article.getCreationDate().toEpochDay()));
      luceneDoc.add(new StoredField("creation_date", article.getCreationDate().toEpochDay()));
    }

    if (article.getCompletionDate() != null ) {
      luceneDoc.add(new LongPoint("completion_date_p", article.getCompletionDate().toEpochDay()));
      luceneDoc.add(new StoredField("completion_date", article.getCompletionDate().toEpochDay()));
    }
    if (article.getRevisionDate() != null ) {
      luceneDoc.add(new LongPoint("revision_date_p", article.getRevisionDate().toEpochDay()));
      luceneDoc.add(new StoredField("revision_date", article.getRevisionDate().toEpochDay()));
    }

    luceneDoc.add(new Field("text", article.getAbstractText(), TEXT_FIELD_TYPE));
    for (Map.Entry<String, String> section : article.getAbstractTexts().entrySet()) {
      luceneDoc.add(new Field("text_" + section.getKey(),
          // Replace any whitespace with a single '_'
          CharMatcher.whitespace().trimAndCollapseFrom(section.getValue().toLowerCase(),
              '_'),
          TEXT_FIELD_TYPE));
    }

    try {
      if (update) {
        indexWriter.updateDocument(new Term("pmid", article.getPubmedId()), luceneDoc);
      } else {
        indexWriter.addDocument(luceneDoc);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    plog.update("{}", luceneDoc.get("article_title"));
  }

  private void parseCompressedXml(Path path) {
    try (final InputStream is = Files.newInputStream(path);
         final BufferedInputStream bis = new BufferedInputStream(is);
         final GZIPInputStream gzis = new GZIPInputStream(bis)) {
      final SAXParser saxParser = parserFactory.newSAXParser();
      saxParser.parse(gzis, handler);
    } catch (IOException | SAXException | ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  public void indexDirectory(Path path) {
    try {
      Files.walk(path)
          .filter(p -> p.toString().endsWith(".xml.gz"))
          .forEach(this::parseCompressedXml);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  @Override
  public void close() {
    // Tell the executor we're done
    this.indexingService.shutdown();

    // Wait for everything to finish indexing
    try {
      this.indexingService.awaitTermination(1, TimeUnit.HOURS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    // Close the index
    try {
      this.indexWriter.commit();
      this.indexWriter.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String... args) throws IOException {
    final Path indexDir = Paths.get(args[0]);
    final boolean replaceIndex = args[1].equals("--replace");

    if (replaceIndex && Files.isDirectory(indexDir)) {
      log.info("Deleting existing index at {}", indexDir);
      //noinspection ResultOfMethodCallIgnored
      Files.walk(indexDir, FileVisitOption.FOLLOW_LINKS)
          .sorted(Comparator.reverseOrder())
          .map(Path::toFile)
          .peek(file -> log.debug("deleting {}", file))
          .forEach(File::delete);
    }

    Arrays.stream(args)
        .skip(replaceIndex ? 2 : 1)
        .map(Paths::get)
        .forEachOrdered( dir -> {
          try (ProgressLogger plog = ProgressLogger.indeterminateSize(
              "indexing " + dir.getFileName(), 1, TimeUnit.MINUTES);
               MedlineIndexer indexer = new MedlineIndexer(indexDir, plog, !replaceIndex)) {
            indexer.indexDirectory(dir);
          }
        });
  }
}
