package edu.utdallas.hltri.trec.pm.ui;

import java.util.List;

import edu.utdallas.hltri.inquire.lucene.LuceneSearchEngine;
import edu.utdallas.hltri.scribe.text.Identifiable;
import edu.utdallas.hltri.trec.pm.AnalyzedTopic;
import edu.utdallas.hltri.trec.pm.search.Displayable;
import edu.utdallas.hltri.trec.pm.search.FusedSearchManager;
import edu.utdallas.hltri.trec.pm.search.JointSearchManager;
import edu.utdallas.hltri.trec.pm.search.SearchManager;
import edu.utdallas.hltri.trec.pm.search.fusion.AspectBatchRanker;
import edu.utdallas.hltri.trec.pm.search.fusion.AspectFusedSimilarityBatchRanker;
import edu.utdallas.hltri.trec.pm.search.fusion.MeanReciprocalRankFusor;
import edu.utdallas.hltri.trec.pm.search.fusion.ReciprocalRankFusor;
import edu.utdallas.hltri.trec.pm.search.fusion.SimilarityBatchRanker;
import edu.utdallas.hltri.trec.pm.search.query.LuceneSingleQueryFactory;
import edu.utdallas.hltri.trec.pm.search.query.LuceneTopicQueryFactory;

public enum SearchMethod {
  JOINT {
    @Override public <K extends Identifiable & Displayable,
        T extends LuceneSearchEngine<K>,
        Q extends LuceneTopicQueryFactory,
        S extends LuceneSingleQueryFactory,
        U extends LuceneSingleQueryFactory> SearchManager<K, T> getSearchManager(
        List<AnalyzedTopic> topics,
        TaskConfiguration<K, T, Q, S, U> task) {
      return new JointSearchManager<>(
          topics,
          task.getSearchEngineSupplier(),
          task.getTopicFactoryCreator(),
          task.getHitTemplate());
    }
  },

  ASPECT_FUSION {
    @Override public <K extends Identifiable & Displayable,
        T extends LuceneSearchEngine<K>,
        Q extends LuceneTopicQueryFactory,
        S extends LuceneSingleQueryFactory,
        U extends LuceneSingleQueryFactory> SearchManager<K, T> getSearchManager(
        List<AnalyzedTopic> topics,
        TaskConfiguration<K, T, Q, S, U> task) {
      return new FusedSearchManager<>(
          topics,
          task.getSearchEngineSupplier(),
          task.getTopicFactoryCreator(),
          task.getHitTemplate(),
          AspectBatchRanker::new,
          MeanReciprocalRankFusor.getDefault());
    }
  },

  SIMILARITY_FUSION {
    @Override public <K extends Identifiable & Displayable,
        T extends LuceneSearchEngine<K>,
        Q extends LuceneTopicQueryFactory,
        S extends LuceneSingleQueryFactory,
        U extends LuceneSingleQueryFactory> SearchManager<K, T> getSearchManager(
        List<AnalyzedTopic> topics,
        TaskConfiguration<K, T, Q, S, U> task) {
      return new FusedSearchManager<>(
          topics,
          task.getSearchEngineSupplier(),
          task.getTopicFactoryCreator(),
          task.getHitTemplate(),
          SimilarityBatchRanker::new,
          ReciprocalRankFusor.getDefault());
    }
  },

  FUSION_FUSION {
    @Override public <K extends Identifiable & Displayable,
        T extends LuceneSearchEngine<K>,
        Q extends LuceneTopicQueryFactory,
        S extends LuceneSingleQueryFactory,
        U extends LuceneSingleQueryFactory> SearchManager<K, T> getSearchManager(
        List<AnalyzedTopic> topics,
        TaskConfiguration<K, T, Q, S, U> task) {
      return new FusedSearchManager<>(
          topics,
          task.getSearchEngineSupplier(),
          task.getTopicFactoryCreator(),
          task.getHitTemplate(),
          AspectFusedSimilarityBatchRanker::new,
          ReciprocalRankFusor.getDefault());
    }
  },

  SIMPLE {
    @Override public <K extends Identifiable & Displayable,
        T extends LuceneSearchEngine<K>,
        Q extends LuceneTopicQueryFactory,
        S extends LuceneSingleQueryFactory,
        U extends LuceneSingleQueryFactory> SearchManager<K, T> getSearchManager(
        List<AnalyzedTopic> topics,
        TaskConfiguration<K, T, Q, S, U> task) {
      return new JointSearchManager<>(
          topics,
          task.getSearchEngineSupplier(),
          task.getSimpleFactoryCreator(),
          task.getHitTemplate());
    }
  },

  STUPID {
    @Override public <K extends Identifiable & Displayable,
        T extends LuceneSearchEngine<K>,
        Q extends LuceneTopicQueryFactory,
        S extends LuceneSingleQueryFactory,
        U extends LuceneSingleQueryFactory> SearchManager<K, T> getSearchManager(
        List<AnalyzedTopic> topics,
        TaskConfiguration<K, T, Q, S, U> task) {
      return new JointSearchManager<>(
          topics,
          task.getSearchEngineSupplier(),
          task.getStupidFactoryCreator(),
          task.getHitTemplate());
    }
  };

  public abstract <K extends Identifiable & Displayable,
      T extends LuceneSearchEngine<K>,
      Q extends LuceneTopicQueryFactory,
      S extends LuceneSingleQueryFactory,
      U extends LuceneSingleQueryFactory> SearchManager<K, T> getSearchManager(
      List<AnalyzedTopic> topics,
      TaskConfiguration<K, T, Q, S, U> task);
}
