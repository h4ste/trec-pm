package edu.utdallas.hltri.trec.pm.search.fusion;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import edu.utdallas.hltri.inquire.SearchResult;
import edu.utdallas.hltri.inquire.engines.SearchResultsList;

public interface RankFusor {
  <K> SearchResultsList<K, SearchResult<K>> fuseRankings(Collection<? extends List<K>> rankings);
}
