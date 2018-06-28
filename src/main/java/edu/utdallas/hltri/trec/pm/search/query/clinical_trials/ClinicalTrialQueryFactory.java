package edu.utdallas.hltri.trec.pm.search.query.clinical_trials;

import com.google.common.collect.ImmutableMap;

import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.util.ArrayList;
import java.util.List;

import edu.utdallas.hltri.data.clinical_trials.ClinicalTrial;
import edu.utdallas.hltri.inquire.lucene.LuceneSearchEngine;
import edu.utdallas.hltri.struct.Weighted;
import edu.utdallas.hltri.trec.pm.AnalyzedTopic;
import edu.utdallas.hltri.trec.pm.MedicalProblem;
import edu.utdallas.hltri.trec.pm.TrecSettings;
import edu.utdallas.hltri.trec.pm.search.query.LuceneTermQueryFactory;
import edu.utdallas.hltri.trec.pm.search.query.LuceneTopicQueryFactory;

public class ClinicalTrialQueryFactory extends LuceneTopicQueryFactory {

  public ClinicalTrialQueryFactory(LuceneSearchEngine<ClinicalTrial> searchEngine) {
    super(searchEngine, TrecSettings.INSTANCE.CLINICAL_TRIALS);
  }

  private void addFilters(BooleanQuery.Builder bq, AnalyzedTopic topic) {
    addMiscFactors(bq, topic);
    addDemographicAspects(bq, topic);
    addOtherAspects(bq, topic.getAdditionalCriteria());
  }

  @Override
  public List<Query> getQueries(AnalyzedTopic topic) {
    final List<Query> queries = new ArrayList<>();

    final Query diseaseQuery = buildDiseaseQueryAspect(topic.getDisease());
    final Query geneQuery = getGeneticAspects(topic.getGenes());
    final Query treatmentQuery = buildTreatmentAspect(topic);

    BooleanQuery.Builder bq;

    bq = new BooleanQuery.Builder();
    bq.add(diseaseQuery, BooleanClause.Occur.MUST);
    bq.add(geneQuery, BooleanClause.Occur.FILTER);
    if (TrecSettings.INSTANCE.useTreatments) {
      bq.add(treatmentQuery, BooleanClause.Occur.FILTER);
    }
    addFilters(bq, topic);
    queries.add(bq.build());

    bq = new BooleanQuery.Builder();
    bq.add(diseaseQuery, BooleanClause.Occur.FILTER);
    bq.add(geneQuery, BooleanClause.Occur.MUST);
    if (TrecSettings.INSTANCE.useTreatments) {
      bq.add(treatmentQuery, BooleanClause.Occur.FILTER);
    }
    addFilters(bq, topic);
    queries.add(bq.build());
//    queries.add(buildTreatmentAspect(topic));

    bq = new BooleanQuery.Builder();
    bq.add(diseaseQuery, BooleanClause.Occur.FILTER);
    bq.add(geneQuery, BooleanClause.Occur.FILTER);
    if (TrecSettings.INSTANCE.useTreatments) {
      bq.add(treatmentQuery, BooleanClause.Occur.MUST);
    }
    addFilters(bq, topic);
    queries.add(bq.build());

    return queries;
  }

  @Override
  protected Query buildDiseaseQueryAspect(MedicalProblem disease) {
    final List<Query> clauses = new ArrayList<>();
    queryFactory.tryBuildTermQuery(Weighted.create("solid tumor", .1))
        .ifPresent(clauses::add);
    queryFactory.tryBuildTermQuery(Weighted.create("solid neoplasm", .1))
        .ifPresent(clauses::add);
    clauses.add(super.buildDiseaseQueryAspect(disease));
    return new DisjunctionMaxQuery(clauses, settings.disjunctionTieBreaker);
  }

  @Override
  protected Query buildTreatmentAspect(AnalyzedTopic topic) {
    return super.buildTreatmentAspect(topic);
  }

  @Override
  protected void addDemographicAspects(BooleanQuery.Builder bq, AnalyzedTopic topic) {
    // Add gender
    switch (topic.getTopic().getGender()) {
      case MALE:
        bq.add(new TermQuery(new Term("eligibility_gender", "FEMALE")),
            BooleanClause.Occur.MUST_NOT);
      case FEMALE:
        bq.add(new TermQuery(new Term("eligibility_gender", "MALE")),
            BooleanClause.Occur.MUST_NOT);
      default:
        // do nothing
    }

    // Add age
    bq.add(IntPoint.newRangeQuery("eligibility_max_age",
        0, topic.getTopic().getAge() - 1),
        BooleanClause.Occur.MUST_NOT);
    bq.add(IntPoint.newRangeQuery("eligibility_min_age",
        topic.getTopic().getAge() + 1, Integer.MAX_VALUE),
        BooleanClause.Occur.MUST_NOT);
  }

  @Override
  protected void addMiscFactors(BooleanQuery.Builder bq, AnalyzedTopic topic) {
    bq.add(new TermQuery(new Term("intervention_types", "DRUG")),
        BooleanClause.Occur.SHOULD);
    bq.add(new TermQuery(new Term("intervention_types", "GENETIC")),
        BooleanClause.Occur.SHOULD);
    queryFactory.tryBuildTermFieldQuery("Phase", "brief_title", 2).ifPresent(
        tq -> bq.add(tq, BooleanClause.Occur.SHOULD)
    );
  }

  @Override
  protected void addOtherAspects(BooleanQuery.Builder bq,
                                 Iterable<MedicalProblem> additionalCriteria) {
    final LuceneTermQueryFactory queryFactory = new LuceneTermQueryFactory(
        searchEngine,
        settings.cloneWithFieldWeights(
            ImmutableMap.<String, Double>builder().put("exclusion_criteria", -10d).build()
        ));

    final BooleanQuery.Builder ibq = new BooleanQuery.Builder();
    for (MedicalProblem problem : additionalCriteria) {
      ibq.add(queryFactory.buildExpandedQuery(problem.toString(),
          flatten(problem.getExpansions().values())), BooleanClause.Occur.SHOULD);
    }

    bq.add(ibq.build(), BooleanClause.Occur.SHOULD);
  }
}

