package edu.utdallas.hltri.trec.pm.search.fusion;

import java.util.List;

import edu.utdallas.hltri.trec.pm.AnalyzedTopic;

public interface BatchRanker<K> {
  List<List<K>> getBatchRankings(AnalyzedTopic topic, int limit);
}
