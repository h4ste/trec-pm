package edu.utdallas.hltri.trec.pm.search;

import org.apache.lucene.search.Query;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import edu.utdallas.hltri.inquire.lucene.LuceneSearchEngine;
import edu.utdallas.hltri.inquire.lucene.LuceneSearchResultsList;
import edu.utdallas.hltri.logging.Logger;
import edu.utdallas.hltri.scribe.text.Identifiable;
import edu.utdallas.hltri.trec.pm.AnalyzedTopic;
import edu.utdallas.hltri.trec.pm.search.query.LuceneSingleQueryFactory;

public class JointSearchManager<K extends Displayable & Identifiable,
    L extends LuceneSearchEngine<K>> extends SearchManager<K, L> {
  private static final Logger log = Logger.get(JointSearchManager.class);

  private final Function<? super L, ? extends LuceneSingleQueryFactory> factoryBuilder;

  public JointSearchManager(List<AnalyzedTopic> topics,
                     Supplier<? extends L> engineSupplier,
                     Function<? super L, ? extends LuceneSingleQueryFactory> factoryBuilder,
                     String hitTemplate) {
    super(topics, engineSupplier, hitTemplate);
    this.factoryBuilder = factoryBuilder;
  }

  @Override
  public JointSearchManager<K, L> search(int limit) {
    try (final L engine = engineSupplier.get()) {
      final LuceneSingleQueryFactory queryFactory = factoryBuilder.apply(engine);

      log.info("Retrieving {} topics from MEDLINE...", topics.size());
      for (AnalyzedTopic topic : topics) {
        // Create Lucene query object
        final Query query = queryFactory.getQuery(topic);
        log.trace("Query: {}", query.toString("text"));
        queries.put(topic, query);

        // Retrieve abstracts from Lucene
        final LuceneSearchResultsList<K> topicResults = engine.search(query, limit);
        log.info("Found {} articles for topic {}.", topicResults.getTotalHits(),
            topic.getTopic().getNumber());
        results.put(topic, topicResults);
      }
    }
    return this;
  }




}
