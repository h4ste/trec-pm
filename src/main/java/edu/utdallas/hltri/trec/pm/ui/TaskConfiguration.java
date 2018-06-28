package edu.utdallas.hltri.trec.pm.ui;

import edu.utdallas.hltri.data.clinical_trials.ClinicalTrial;
import edu.utdallas.hltri.data.clinical_trials.ClinicalTrialSearchEngine;
import edu.utdallas.hltri.data.medline.MedlineArticle;
import edu.utdallas.hltri.data.medline.MedlineSearchEngine;
import edu.utdallas.hltri.inquire.lucene.LuceneSearchEngine;
import edu.utdallas.hltri.trec.pm.search.query.LuceneQueryFactory;
import edu.utdallas.hltri.trec.pm.search.query.LuceneTopicQueryFactory;
import edu.utdallas.hltri.trec.pm.search.query.clinical_trials.ClinicalTrialQueryFactory;
import edu.utdallas.hltri.trec.pm.search.query.clinical_trials.SimpleClinicalTrialQueryFactory;
import edu.utdallas.hltri.trec.pm.search.query.clinical_trials.StupidClinicalTrialsQueryFactory;
import edu.utdallas.hltri.trec.pm.search.query.medline.MedlineQueryFactory;
import edu.utdallas.hltri.trec.pm.search.query.medline.SimpleMedlineQueryFactory;
import edu.utdallas.hltri.trec.pm.search.query.medline.StupidMedlineQueryFactory;
import java.util.function.Function;
import java.util.function.Supplier;

class TaskConfiguration<K,
    T extends LuceneSearchEngine<K>,
    Q extends LuceneTopicQueryFactory,
    S extends LuceneQueryFactory,
    U extends LuceneQueryFactory> {

  private final Supplier<T>                      searchEngineSupplier;
  private final Function<? super T, ? extends Q> topicFactoryCreator;
  private final Function<? super T, ? extends S> simpleFactoryCreator;
  private final Function<? super T, ? extends U> stupidFactoryCreator;
  private final String hitTemplate;

  private TaskConfiguration(
      Supplier<T> searchEngineSupplier,
      Function<? super T, ? extends Q> topicFactoryCreator,
      Function<? super T, ? extends S> simpleFactoryCreator,
      Function<? super T, ? extends U> stupidFactoryCreator, String hitTemplate) {
    this.searchEngineSupplier = searchEngineSupplier;
    this.topicFactoryCreator = topicFactoryCreator;
    this.simpleFactoryCreator = simpleFactoryCreator;
    this.stupidFactoryCreator = stupidFactoryCreator;
    this.hitTemplate = hitTemplate;
  }

  Supplier<T> getSearchEngineSupplier() {
    return searchEngineSupplier;
  }

  Function<? super T, ? extends Q> getTopicFactoryCreator() {
    return topicFactoryCreator;
  }

  Function<? super T, ? extends S> getSimpleFactoryCreator() {
    return simpleFactoryCreator;
  }

  Function<? super T, ? extends U> getStupidFactoryCreator() {
    return stupidFactoryCreator;
  }

  String getHitTemplate() {
    return hitTemplate;
  }

  /*
    TaskConfiguration<K,
      T extends LuceneSearchEngine<K>,
      Q extends LuceneQueryFactory,
      S extends LuceneQueryFactory,
      U extends LuceneQueryFactory>
     */
  static final TaskConfiguration<MedlineArticle, MedlineSearchEngine<MedlineArticle>, MedlineQueryFactory,
      SimpleMedlineQueryFactory, StupidMedlineQueryFactory>
      MEDLINE  = new TaskConfiguration<MedlineArticle, MedlineSearchEngine<MedlineArticle>, MedlineQueryFactory,
      SimpleMedlineQueryFactory, StupidMedlineQueryFactory>(
        MedlineSearchEngine::getDefault,
        MedlineQueryFactory::new,
        SimpleMedlineQueryFactory::new,
        StupidMedlineQueryFactory::new,
        "medline_hit.vm");

  static final TaskConfiguration<ClinicalTrial, ClinicalTrialSearchEngine<ClinicalTrial>,
      ClinicalTrialQueryFactory, SimpleClinicalTrialQueryFactory, StupidClinicalTrialsQueryFactory>
      CLINICAL_TRIALS = new TaskConfiguration<ClinicalTrial, ClinicalTrialSearchEngine<ClinicalTrial>,
      ClinicalTrialQueryFactory, SimpleClinicalTrialQueryFactory, StupidClinicalTrialsQueryFactory>(
        ClinicalTrialSearchEngine::getDefault,
        ClinicalTrialQueryFactory::new,
        SimpleClinicalTrialQueryFactory::new,
        StupidClinicalTrialsQueryFactory::new,
        "clinical_trial_hit.vm");
}
