package edu.utdallas.hltri.trec.pm.search.fusion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.utdallas.hltri.inquire.SearchResult;
import edu.utdallas.hltri.inquire.lucene.LuceneSearchEngine;
import edu.utdallas.hltri.inquire.lucene.similarity.Similarities;
import edu.utdallas.hltri.trec.pm.AnalyzedTopic;
import edu.utdallas.hltri.trec.pm.TrecSettings;
import edu.utdallas.hltri.trec.pm.search.query.LuceneAspectQueryFactory;

public class AspectFusedSimilarityBatchRanker<K> implements BatchRanker<K> {
  private final RankFusor subRanker;
  private final List<CustomSimilarityAspectBatchRanker<K>> similarityAspectBatchers;


  public AspectFusedSimilarityBatchRanker(LuceneSearchEngine<K> engine,
                                          LuceneAspectQueryFactory queryFactory) {
    this.subRanker = new MeanReciprocalRankFusor(TrecSettings.INSTANCE.getRrfConstant);
    this.similarityAspectBatchers = new ArrayList<>();
    for (Similarities similarities : Similarities.values()) {
      this.similarityAspectBatchers.add(new CustomSimilarityAspectBatchRanker<>(
          engine,
          queryFactory,
          similarities.similarity));
    }
  }

  @Override
  public List<List<K>> getBatchRankings(AnalyzedTopic topic, int limit) {
    final List<List<K>> similarityRankings = new ArrayList<>();
    for (CustomSimilarityAspectBatchRanker<K> aspectBatchRanker : similarityAspectBatchers) {
      final List<List<K>> aspectRankings = aspectBatchRanker.getBatchRankings(topic, limit);
      final List<SearchResult<K>> aspectFusedSearchResults =
          subRanker.fuseRankings(aspectRankings).getResults();
      final List<K> aspectFusedDocuments = aspectFusedSearchResults.stream()
          .map(SearchResult::getValue)
          .collect(Collectors.toList());
      similarityRankings.add(aspectFusedDocuments);
    }
    return similarityRankings;
  }
}