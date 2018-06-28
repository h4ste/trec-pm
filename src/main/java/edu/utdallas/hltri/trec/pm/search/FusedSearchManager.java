package edu.utdallas.hltri.trec.pm.search;

import edu.utdallas.hltri.trec.pm.search.query.LuceneSingleQueryFactory;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import edu.utdallas.hltri.inquire.SearchResult;
import edu.utdallas.hltri.inquire.engines.SearchResultsList;
import edu.utdallas.hltri.inquire.lucene.LuceneSearchEngine;
import edu.utdallas.hltri.logging.Logger;
import edu.utdallas.hltri.scribe.text.Identifiable;
import edu.utdallas.hltri.trec.pm.AnalyzedTopic;
import edu.utdallas.hltri.trec.pm.search.fusion.BatchRanker;
import edu.utdallas.hltri.trec.pm.search.fusion.RankFusor;
import edu.utdallas.hltri.trec.pm.search.query.LuceneQueryFactory;
import org.apache.lucene.search.MatchNoDocsQuery;

public class FusedSearchManager<K extends Displayable & Identifiable, T extends LuceneQueryFactory,
    L extends LuceneSearchEngine<K>> extends SearchManager<K, L> {
  private static final Logger log = Logger.get(FusedSearchManager.class);

  private final RankFusor                                            fuser;
  private final BiFunction<? super L, ? super T, ? extends BatchRanker<K>> batchRankerSupplier;
  private final Function<? super L, ? extends T>                   factoryBuilder;

  public FusedSearchManager(List<AnalyzedTopic> topics,
                            Supplier<? extends L> engineSupplier,
                            Function<? super L, ? extends T> factoryBuilder,
                            String hitTemplate,
                            BiFunction<? super L, ? super T, ? extends BatchRanker<K>> batchRankerSupplier,
                            RankFusor fuser) {
    super(topics, engineSupplier, hitTemplate);
    this.factoryBuilder = factoryBuilder;
    this.batchRankerSupplier = batchRankerSupplier;
    this.fuser = fuser;
  }

  @Override
  public SearchManager<K, L> search(int limit) {
    try (final L engine = engineSupplier.get()) {
      final T queryFactory = factoryBuilder.apply(engine);

      final BatchRanker<K> batchRanker = batchRankerSupplier.apply(engine, queryFactory);

      log.info("Retrieving {} topics...", topics.size());
      for (AnalyzedTopic topic : topics) {
        if (queryFactory instanceof LuceneSingleQueryFactory) {
          queries.put(topic, ((LuceneSingleQueryFactory) queryFactory).getQuery(topic));
        } else {
          queries.put(topic, new MatchNoDocsQuery());
        }

        final List<List<K>> rankings = batchRanker.getBatchRankings(topic, limit * 2);

        final SearchResultsList<K, SearchResult<K>> fusedResults =
            fuser.fuseRankings(rankings).trimToLength(limit);

        log.info("Found {} documents for topic {}",
            fusedResults.getTotalHits(), topic.getTopic().getNumber());

        results.put(topic, fusedResults);
      }
    }
    return this;
  }
}
