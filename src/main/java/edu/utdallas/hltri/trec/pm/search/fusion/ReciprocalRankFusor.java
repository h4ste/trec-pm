package edu.utdallas.hltri.trec.pm.search.fusion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.utdallas.hltri.inquire.SearchResult;
import edu.utdallas.hltri.inquire.SimpleSearchResult;
import edu.utdallas.hltri.inquire.engines.SearchResultsList;
import edu.utdallas.hltri.logging.Logger;
import edu.utdallas.hltri.scribe.text.Identifiable;
import edu.utdallas.hltri.struct.Weighted;
import edu.utdallas.hltri.trec.pm.TrecSettings;

public class ReciprocalRankFusor implements RankFusor {
  private static final Logger log = Logger.get(ReciprocalRankFusor.class);

  protected final int k;

  @SuppressWarnings("WeakerAccess")
  public ReciprocalRankFusor(int k) {
    this.k = k;
  }

  @SuppressWarnings("WeakerAccess")
  protected <K> Set<K> getDocumentSet(Collection<? extends List<K>> rankings) {
    return rankings.stream().flatMap(List::stream).collect(Collectors.toSet());
  }

  protected <K> List<Weighted<K>> getScores(Collection<? extends List<K>> rankings) {
    final List<Weighted<K>> rrfScores = new ArrayList<>();
    for (final K document : getDocumentSet(rankings)) {
      double rrfScore = 0;
      for (List<K> ranking : rankings) {
        int rank = ranking.indexOf(document) + 1;
        if (rank == 0) {
          rank = ranking.size();
        }
        rrfScore += 1.0 / (double) (k + rank);
        log.trace("Document {} had rank {}", ((Identifiable) document).getId(), rank);
      }
      rrfScores.add(Weighted.create(document, rrfScore));
    }
    return rrfScores;
  }

  @Override
  public <K> SearchResultsList<K, SearchResult<K>> fuseRankings(
      Collection<? extends List<K>> rankings) {
    final List<Weighted<K>> rrfScores = getScores(rankings);

    // Sort documents by highest weight (score)
    rrfScores.sort(Comparator.<Weighted<K>>comparingDouble(Weighted::getWeight).reversed());

    // Wrap documents in SearchResult objects
    final List<SearchResult<K>> rankedResults = new ArrayList<>();
    for (int i = 0; i < rrfScores.size(); i++) {
      final Weighted<K> scoredDoc = rrfScores.get(i);
      rankedResults.add(new SimpleSearchResult<>((i + 1),
          scoredDoc.getWeight(),
          scoredDoc.getValue()));

    }

    // Wrap in SearchResultsList
    return new SearchResultsList<>(rankedResults.get(0).getScore(),
            rankedResults.size(),
            rankedResults);
  }

  public static ReciprocalRankFusor getDefault() {
    return new ReciprocalRankFusor(TrecSettings.INSTANCE.getRrfConstant);
  }
}
