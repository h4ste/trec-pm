package edu.utdallas.hltri.trec.pm.search.query;

import com.google.common.base.Joiner;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import edu.utdallas.hltri.inquire.lucene.LuceneSearchEngine;
import edu.utdallas.hltri.inquire.lucene.LuceneUtils;
import edu.utdallas.hltri.logging.Logger;
import edu.utdallas.hltri.struct.Weighted;
import edu.utdallas.hltri.trec.pm.LuceneSearchSettings;
import edu.utdallas.hltri.util.Expansion;

@SuppressWarnings("WeakerAccess")
public class LuceneTermQueryFactory {
  private static final Logger log = Logger.get(LuceneTermQueryFactory.class);

  private final LuceneSearchSettings settings;
  private final LuceneSearchEngine<?> searchEngine;

  private final Joiner joiner = Joiner.on(' ');

  public LuceneTermQueryFactory(LuceneSearchEngine<?> engine, LuceneSearchSettings settings) {
    this.searchEngine = engine;
    this.settings = settings;
  }

  public Optional<Query> tryBuildUnweightedTermQuery(CharSequence term) {
    final List<Query> fieldQueries = settings.fieldWeights.entrySet().stream()
        .map(e -> tryBuildUnweightedTermFieldQuery(term.toString(), e.getKey()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());

    if (fieldQueries.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(new DisjunctionMaxQuery(fieldQueries,
          settings.disjunctionTieBreaker));
    }
  }

  public Optional<Query> tryBuildUnweightedTermFieldQuery(String term, String field) {
    return LuceneUtils.getUnweightedSubQuery(term,
        field,
        searchEngine.getAnalyzer(),
        settings.keyphraseSlop,
        settings.enforceKeyphraseTermOrder);
  }

  public Optional<Query> tryBuildTermQuery(Weighted<? extends CharSequence> term) {
    final List<Query> fieldQueries = settings.fieldWeights.entrySet().stream()
        .map(e -> tryBuildTermFieldQuery(term.getValue().toString(),
            e.getKey(),
            e.getValue().floatValue() * (float) term.getWeight()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());

    if (fieldQueries.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(new DisjunctionMaxQuery(fieldQueries,
          settings.disjunctionTieBreaker));
    }
  }

  public Optional<Query> tryBuildTermFieldQuery(String term, String field, float weight) {
    return LuceneUtils.getSubQuery(term,
        field,
        searchEngine.getAnalyzer(),
        weight,
        settings.keyphraseSlop,
        settings.enforceKeyphraseTermOrder);
  }

  public Query buildExpandedQuery(
      String term,
      Collection<? extends Weighted<? extends CharSequence>> expansions) {
    return buildExpandedQuery(Collections.singleton(term), expansions);
  }

  public Query buildExpandedQuery(
      Collection<? extends CharSequence> baseTerms,
      Collection<? extends Weighted<? extends CharSequence>> expansions) {
    return buildScaledExpandedQuery(1, baseTerms, expansions);
  }

  public Query buildScaledExpandedQuery(
      double scale,
      Collection<? extends CharSequence> baseTerms,
      Collection<? extends Weighted<? extends CharSequence>> expansions) {
    final BooleanQuery.Builder query = new BooleanQuery.Builder();
    query.setDisableCoord(true);

    expansions = filterExpansions(baseTerms, expansions);

    final List<Weighted<? extends CharSequence>> terms =
        new ArrayList<>(expansions.size() + baseTerms.size());
    terms.addAll(Weighted.fixed(baseTerms, settings.keyphraseWeight * scale));
    for (Weighted<? extends CharSequence> term : expansions) {
      terms.add(Weighted.create(term.weight * scale, term.value));
    }

    Expansion.filterWeightedExpansions(terms);

    // We may not be able to get a query for each expanded term (i.e., an expansion may be
    // killed by lucene's analyzer, so skip those
    terms.stream().map(this::tryBuildTermQuery)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(q -> query.add(q, BooleanClause.Occur.SHOULD));
    final Query q = query.build();
    log.trace("For term(s) {}, created expanded BooleanQuery {}", baseTerms, q);
    return q;
  }

  public <T extends CharSequence, W extends Weighted<? extends T>>
  Collection<W> filterExpansions(
      Collection<? extends CharSequence> keywords,
      Collection<W> expansions) {
    final Set<String> seenTerms = new HashSet<>();
    final Set<W> savedTerms = new HashSet<>();

    for (CharSequence keyword : keywords) {
      final String tokenizedKeyword = joiner.join(searchEngine.tokenize(keyword));
      seenTerms.add(tokenizedKeyword);
    }

    for (final W expansionTerm : expansions) {
      final String tokenizedTerm = joiner.join(searchEngine.tokenize(expansionTerm.getValue()));
      if (!seenTerms.contains(tokenizedTerm)) {
        seenTerms.add(tokenizedTerm);
        savedTerms.add(expansionTerm);
      } else {
        log.trace("Removed expansion term {} due to {}", expansionTerm, seenTerms);
      }
    }

    return savedTerms;
  }

  @SuppressWarnings("SameParameterValue")
  public Query buildScaledExpandedQuery(double scale,
                                        String term,
                                        Collection<Weighted<? extends CharSequence>> expansions) {
    return buildScaledExpandedQuery(scale, Collections.singleton(term), expansions);
  }

}
