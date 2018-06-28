package edu.utdallas.hltri.trec.pm.search.fusion;

import org.apache.lucene.search.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.utdallas.hltri.inquire.lucene.LuceneResult;
import edu.utdallas.hltri.inquire.lucene.LuceneSearchEngine;
import edu.utdallas.hltri.inquire.lucene.similarity.Similarities;
import edu.utdallas.hltri.trec.pm.AnalyzedTopic;
import edu.utdallas.hltri.trec.pm.search.query.LuceneSingleQueryFactory;

public class SimilarityBatchRanker<K> implements BatchRanker<K> {
  private final LuceneSearchEngine<K>    engine;
  private final LuceneSingleQueryFactory queryFactory;

  public SimilarityBatchRanker(LuceneSearchEngine<K> engine,
                               LuceneSingleQueryFactory queryFactory) {
    this.engine = engine;
    this.queryFactory = queryFactory;
  }

  @Override
  public List<List<K>> getBatchRankings(AnalyzedTopic topic, int limit) {
    final Query query = queryFactory.getQuery(topic);

    final List<List<K>> rankings = new ArrayList<>();
    for (Similarities similarities : Similarities.values()) {
      final List<K> ranking = engine.search(similarities.similarity, query, limit * 2)
          .getResults()
          .stream()
          .map(LuceneResult::getValue)
          .collect(Collectors.toList());

      rankings.add(ranking);
    }

    return rankings;
  }
}
