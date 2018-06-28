package edu.utdallas.hltri.trec.pm.search.query;

import org.apache.lucene.search.Query;

import edu.utdallas.hltri.trec.pm.AnalyzedTopic;

public interface LuceneSingleQueryFactory extends LuceneQueryFactory {
  Query getQuery(AnalyzedTopic topic);
}
