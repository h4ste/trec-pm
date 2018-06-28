package edu.utdallas.hltri.data.clinical_trials;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import edu.utdallas.hltri.framework.ProgressLogger;
import edu.utdallas.hltri.inquire.lucene.DocumentFactory;
import edu.utdallas.hltri.inquire.lucene.LuceneSearchEngine;
import edu.utdallas.hltri.inquire.lucene.LuceneUtils;
import edu.utdallas.hltri.logging.Logger;

/**
 * Created by travis on 6/19/17.
 */
public class ClinicalTrialSearchEngine<T extends ClinicalTrial> extends LuceneSearchEngine<T> {
  private static final Logger log = Logger.get(ClinicalTrialSearchEngine.class);

  @SuppressWarnings("WeakerAccess")
  public static final String NCTID_FIELD = "nctid";

  /**
   * Create a new LuceneSearchEngine
   *
   * @param indexPath (file) path to Lucene index
   * @param defaultFieldName default search field (e.g. TEXT)
   */
  @SuppressWarnings("WeakerAccess")
  public ClinicalTrialSearchEngine(String indexPath, String defaultFieldName,
                                   DocumentFactory<T> factory) {
    super(indexPath,
        new EnglishAnalyzer(),
        defaultFieldName,
        factory);
  }

  public static ClinicalTrialSearchEngine<ClinicalTrial> getDefault() {
    return new ClinicalTrialSearchEngine<>(
        ClinicalTrialSettings.EAGER.indexPath,
        ClinicalTrialSettings.EAGER.defaultField,
        DocumentFactory.eager(SimpleClinicalTrial::fromLucene));
  }

  public static ClinicalTrialSearchEngine<JaxbClinicalTrial> getLazy() {
    return new ClinicalTrialSearchEngine<>(
        ClinicalTrialSettings.LAZY.indexPath,
        ClinicalTrialSettings.LAZY.defaultField,
        JaxbClinicalTrial::new);
  }

  /**
   * Produces a list of ClinicalTrials associated with the given NCT IDs by
   * forming a large disjunction query of NCT IDs
   * @param nctIds list of clinical trial NCT IDs
   * @return a list of ClinicalTrials associated with the given NCT IDs
   */
  public List<T> getNctsByIds(final Collection<String> nctIds) {
    // Temporarily increase the max clause count to accomodate all NCT IDs
    final int prevMaxClauseCount = BooleanQuery.getMaxClauseCount();
    BooleanQuery.setMaxClauseCount(nctIds.size());

    log.debug("Generating disjunction query of {} NCT IDs", nctIds.size());
    // Build a disjunction of all NCT IDs
    final BooleanQuery query = nctIds.stream()
        .map(nctId -> new TermQuery(new Term(NCTID_FIELD, nctId)))
        .collect(LuceneUtils.toBooleanQuery(Occur.SHOULD));

    return withSearcher(searcher -> {
      try {
        // Do the actual retrieval
        final ScoreDoc[] docs = searcher.search(query, Integer.MAX_VALUE).scoreDocs;
        log.debug("Retrieved {} trials", docs.length);

        // Reset max clause count to previous value
        BooleanQuery.setMaxClauseCount(prevMaxClauseCount);
        assert docs.length <= nctIds.size();
        if (docs.length < nctIds.size()) {
          log.error("Failed to find {} trials", nctIds.size() - docs.length);
        }

        // Convert all of Lucene's ScoreDoc entries to ClinicalTrial objects
        final List<T> trials = new ArrayList<>();
        try (ProgressLogger plog = ProgressLogger.fixedSize("loading trials",
            docs.length,
            1, TimeUnit.MINUTES)) {
          for (ScoreDoc doc : docs) {
            final T trial = factory.build(searcher.getIndexReader(), doc.doc);
            trials.add(trial);
            plog.update("loaded {}", trial.getNctId());
          }
        }
        return trials;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  /**
   * Get the ClinicalTrial associated with the given NCT ID in the index
   * @param nctId an NCT ID
   * @return the associated ClinicalTrial
   */
  public @Nullable ClinicalTrial getByNctId(final String nctId) {
    return withSearcher(searcher -> {
      try {
        ScoreDoc doc = searcher
            .search(new TermQuery(new Term(NCTID_FIELD, nctId)), 1).scoreDocs[0];
        return factory.build(searcher.getIndexReader(), doc.doc);
      } catch (ArrayIndexOutOfBoundsException e) {
        log.error("Failed to find trial with NCT ID |{}|", nctId);
        return null;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }
}
