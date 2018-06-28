package edu.utdallas.hltri.trec.pm.search;

import org.apache.lucene.search.Query;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import edu.utdallas.hltri.inquire.SearchResult;
import edu.utdallas.hltri.inquire.engines.SearchResultsList;
import edu.utdallas.hltri.inquire.eval.TrecRunWriter;
import edu.utdallas.hltri.inquire.lucene.LuceneSearchEngine;
import edu.utdallas.hltri.logging.Logger;
import edu.utdallas.hltri.scribe.text.Identifiable;
import edu.utdallas.hltri.trec.pm.AnalyzedTopic;
import edu.utdallas.hltri.trec.pm.io.html.HtmlOutputManager;

public abstract class SearchManager<K extends Identifiable & Displayable,
    L extends LuceneSearchEngine<K>> {
  private static final Logger log = Logger.get(JointSearchManager.class);

  private final String hitTemplate;

  protected final List<AnalyzedTopic>                                                 topics;

  @SuppressWarnings("WeakerAccess")
  protected final Supplier<? extends L>                                               engineSupplier;

  @SuppressWarnings("WeakerAccess")
  protected final Map<AnalyzedTopic, Query>                                           queries;

  protected final Map<AnalyzedTopic, SearchResultsList<K, ? extends SearchResult<K>>> results;

  protected boolean useTreatments = true;

  SearchManager(List<AnalyzedTopic> topics,
                Supplier<? extends L> engineSupplier,
                String hitTemplate) {
    this.topics = topics;
    this.engineSupplier = engineSupplier;
    this.hitTemplate = hitTemplate;
    this.queries = new LinkedHashMap<>();
    this.results = new LinkedHashMap<>();
  }

  public SearchManager<K, L> setUseTreatments(boolean useTreatments) {
    this.useTreatments = useTreatments;
    return this;
  }

  public abstract SearchManager<K, L> search(int limit);

  public SearchManager<K, L> logResults(int limit) {
    for (AnalyzedTopic topic : this.results.keySet()) {
      final List<? extends SearchResult<K>> topicResults = results.get(topic).getResults();
      final int shownResults = Math.min(limit, topicResults.size());
      log.debug("Topic {}: top {} results", topic.getTopic().getNumber(), shownResults);
      for (int i = 0; i < shownResults; i++) {
        final SearchResult<K> result = topicResults.get(i);
        final K doc = result.getValue();
        log.debug("{}. {}\t{}", result.getRank(), result.getScore(), doc.getSearchHeading());
      }
    }
    return this;
  }

  public SearchManager<K, L> saveRunFile(final Path path, final String runTag) {
    try (TrecRunWriter writer = new TrecRunWriter(path, runTag)) {
      for (AnalyzedTopic topic : this.results.keySet()) {
        for (SearchResult<K> result : this.results.get(topic).getResults()) {
          writer.writeResult(
              topic.getTopic().getNumber(),
              result.getValue().getId(),
              result.getRank(),
              result.getScore());
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return this;
  }

  @SuppressWarnings("UnusedReturnValue")
  public SearchManager<K, L> saveHtml(final Path path) {
    log.info("Beginning HTML generation...");
    HtmlOutputManager<AnalyzedTopic> manager = new HtmlOutputManager<>();
    manager.generateHtmlOutput(topics, results, queries, path, hitTemplate);
    return this;
  }
}
