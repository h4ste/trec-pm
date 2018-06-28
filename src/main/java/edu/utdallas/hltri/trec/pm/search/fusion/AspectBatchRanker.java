package edu.utdallas.hltri.trec.pm.search.fusion;

import org.apache.lucene.search.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.utdallas.hltri.inquire.lucene.LuceneResult;
import edu.utdallas.hltri.inquire.lucene.LuceneSearchEngine;
import edu.utdallas.hltri.inquire.lucene.LuceneSearchResultsList;
import edu.utdallas.hltri.trec.pm.AnalyzedTopic;
import edu.utdallas.hltri.trec.pm.search.query.LuceneAspectQueryFactory;

public class AspectBatchRanker<K> implements BatchRanker<K> {
  protected final LuceneSearchEngine<K>    engine;
  private final   LuceneAspectQueryFactory queryFactory;

  public AspectBatchRanker(LuceneSearchEngine<K> engine, LuceneAspectQueryFactory queryFactory) {
    this.engine = engine;
    this.queryFactory = queryFactory;
  }

  protected LuceneSearchResultsList<K> search(Query query, int limit) {
    return engine.search(query, limit * 2);
  }

  @Override
  public List<List<K>> getBatchRankings(AnalyzedTopic topic, int limit) {
    final List<Query> subQueries = queryFactory.getQueries(topic);
    final List<List<K>> rankings = new ArrayList<>();
    for (final Query query : subQueries) {
      final List<K> subQueryResults = search(query, limit)
          .getResults()
          .stream()
          .map(LuceneResult::getValue)
          .collect(Collectors.toList());
      rankings.add(subQueryResults);
    }
    return rankings;
  }
}
