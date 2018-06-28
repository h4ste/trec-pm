package edu.utdallas.hltri.trec.pm.search.fusion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.utdallas.hltri.logging.Logger;
import edu.utdallas.hltri.scribe.text.Identifiable;
import edu.utdallas.hltri.struct.Weighted;
import edu.utdallas.hltri.trec.pm.TrecSettings;

public class MeanReciprocalRankFusor extends ReciprocalRankFusor {
  private static final Logger log = Logger.get(MeanReciprocalRankFusor.class);

  @SuppressWarnings("WeakerAccess")
  public MeanReciprocalRankFusor(int k) {
    super(k);
  }

  protected <K> List<Weighted<K>> getScores(Collection<? extends List<K>> rankings) {
    final List<Weighted<K>> rrfScores = new ArrayList<>();
    for (final K document : getDocumentSet(rankings)) {
      double rrfScore = 1;
      for (List<K> ranking : rankings) {
        int rank = ranking.indexOf(document) + 1;
        if (rank == 0) {
          rank = ranking.size();
        }
        rrfScore *= 1.0 / (double) (k + rank);
        log.trace("Document {} had rank {}", ((Identifiable) document).getId(), rank);
      }
      rrfScores.add(Weighted.create(document, Math.pow(rrfScore, 1.0 / rankings.size())));
    }
    return rrfScores;
  }

  public static MeanReciprocalRankFusor getDefault() {
    return new MeanReciprocalRankFusor(TrecSettings.INSTANCE.getRrfConstant);
  }
}
