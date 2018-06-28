package edu.utdallas.hltri.trec.pm.search.query;

import org.apache.lucene.search.Query;

import java.util.List;

import edu.utdallas.hltri.trec.pm.AnalyzedTopic;

public interface LuceneAspectQueryFactory extends LuceneQueryFactory {
  List<Query> getQueries(final AnalyzedTopic topic);
}
