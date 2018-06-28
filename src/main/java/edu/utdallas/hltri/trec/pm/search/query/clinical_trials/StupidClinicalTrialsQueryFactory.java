package edu.utdallas.hltri.trec.pm.search.query.clinical_trials;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import edu.utdallas.hltri.data.clinical_trials.ClinicalTrial;
import edu.utdallas.hltri.inquire.lucene.LuceneSearchEngine;
import edu.utdallas.hltri.struct.Weighted;
import edu.utdallas.hltri.trec.pm.AnalyzedTopic;
import edu.utdallas.hltri.trec.pm.GeneticVariant;

public class StupidClinicalTrialsQueryFactory extends ClinicalTrialQueryFactory {
  public StupidClinicalTrialsQueryFactory(LuceneSearchEngine<ClinicalTrial> engine) {
    super(engine);
  }

  @Override
  public Query getQuery(AnalyzedTopic topic) {
    final BooleanQuery.Builder bq = new BooleanQuery.Builder();
    queryFactory.tryBuildUnweightedTermQuery(topic.getDisease()).ifPresent(
        tq -> bq.add(tq, BooleanClause.Occur.SHOULD)
    );
    queryFactory.tryBuildTermQuery(Weighted.create(0.1, "solid tumor")).ifPresent(
        tq -> bq.add(tq, BooleanClause.Occur.SHOULD)
    );
    for (GeneticVariant gene : topic.getGenes()) {
      queryFactory.tryBuildUnweightedTermQuery(gene.getGene()).ifPresent(
          tq -> bq.add(tq, BooleanClause.Occur.SHOULD)
      );

      if (gene.getLocus() != null)
      queryFactory.tryBuildUnweightedTermQuery(gene.getLocus()).ifPresent(
          tq -> bq.add(tq, BooleanClause.Occur.SHOULD)
      );
    }

    return bq.build();
  }
}
