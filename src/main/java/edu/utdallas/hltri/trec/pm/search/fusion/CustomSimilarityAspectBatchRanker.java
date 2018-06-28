package edu.utdallas.hltri.trec.pm.search.fusion;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.Similarity;

import edu.utdallas.hltri.inquire.lucene.LuceneSearchEngine;
import edu.utdallas.hltri.inquire.lucene.LuceneSearchResultsList;
import edu.utdallas.hltri.trec.pm.search.query.LuceneAspectQueryFactory;

public class CustomSimilarityAspectBatchRanker<K> extends AspectBatchRanker<K> {
  private final Similarity similarity;

  @SuppressWarnings("WeakerAccess")
  public CustomSimilarityAspectBatchRanker(LuceneSearchEngine<K> engine,
                                           LuceneAspectQueryFactory queryFactory,
                                           Similarity similarity) {
    super(engine, queryFactory);
    this.similarity = similarity;
  }

  @Override
  protected LuceneSearchResultsList<K> search(Query query, int limit) {
    return engine.search(similarity, query, limit * 2);
  }
}
