package edu.utdallas.hltri.trec.pm.search.query.medline;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.utdallas.hltri.data.medline.MedlineArticle;
import edu.utdallas.hltri.inquire.lucene.LuceneSearchEngine;
import edu.utdallas.hltri.struct.Weighted;
import edu.utdallas.hltri.trec.pm.AnalyzedTopic;
import edu.utdallas.hltri.trec.pm.MedicalProblem;
import edu.utdallas.hltri.trec.pm.TrecSettings;
import edu.utdallas.hltri.trec.pm.search.query.LuceneTopicQueryFactory;

public class MedlineQueryFactory extends LuceneTopicQueryFactory {

  public MedlineQueryFactory(LuceneSearchEngine<MedlineArticle> searchEngine) {
    super(searchEngine, TrecSettings.INSTANCE.MEDLINE);
  }

  private BooleanClause getDiseaseTitleFilter(AnalyzedTopic topic) {
    final MedicalProblem disease = topic.getDisease();
    final List<String> diseaseTerms = new ArrayList<>();
    diseaseTerms.add(disease.toString());
    diseaseTerms.addAll(disease.getExpansions().values().stream()
        .flatMap(Collection::stream)
        .map(Weighted::getValue)
        .collect(Collectors.toList()));
    return new BooleanClause(
        searchEngine.newBooleanQuery(Weighted.fixed(diseaseTerms, 0d),
            "article_title"),
        BooleanClause.Occur.FILTER);
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
    queries.add(bq.build());

    bq = new BooleanQuery.Builder();
    bq.add(diseaseQuery, BooleanClause.Occur.FILTER);
    bq.add(geneQuery, BooleanClause.Occur.MUST);
    if (TrecSettings.INSTANCE.useTreatments) {
      bq.add(treatmentQuery, BooleanClause.Occur.FILTER);
    }
    queries.add(bq.build());
//    queries.add(buildTreatmentAspect(topic));

    bq = new BooleanQuery.Builder();
    bq.add(diseaseQuery, BooleanClause.Occur.FILTER);
    bq.add(geneQuery, BooleanClause.Occur.FILTER);
    if (TrecSettings.INSTANCE.useTreatments) {
      bq.add(treatmentQuery, BooleanClause.Occur.MUST);
    }
    addMiscFactors(bq, topic);

    queries.add(bq.build());

    return queries;
  }

  @Override
  protected Query buildTreatmentAspect(AnalyzedTopic topic) {
    if (TrecSettings.INSTANCE.useTreatments) {
      return new BooleanQuery.Builder()
          .add(getDiseaseTitleFilter(topic))
          .add(super.buildTreatmentAspect(topic), BooleanClause.Occur.SHOULD)
          .build();
    } else {
      return new MatchAllDocsQuery();
    }
  }

  @Override
  protected void addTreatmentAspect(BooleanQuery.Builder tbq, AnalyzedTopic topic) {
    tbq.add(buildTreatmentAspect(topic), BooleanClause.Occur.SHOULD);
  }

  @Override
  protected void addMiscFactors(BooleanQuery.Builder tbq, AnalyzedTopic topic) {
    {
      // Prefer review articles
      tbq.add(searchEngine.newPhraseQuery("article_types", "Review"),
          BooleanClause.Occur.SHOULD);
      tbq.add(searchEngine.newPhraseQuery("article_types", "Comment"),
          BooleanClause.Occur.MUST_NOT);

      final BooleanQuery.Builder bq = new BooleanQuery.Builder();
      // Penalize the presence of "cell"
      //noinspection ConstantConditions
      queryFactory.tryBuildTermQuery(Weighted.create("cell", 2f)).ifPresent(
          query -> bq.add(new BoostQuery(query, -1), BooleanClause.Occur.SHOULD)
      );
      queryFactory.tryBuildTermQuery(Weighted.create("cell line", 3f)).ifPresent(
          query -> bq.add(new BoostQuery(query, -1), BooleanClause.Occur.SHOULD)
      );
      queryFactory.tryBuildTermQuery(Weighted.create("cell cycle", 3f)).ifPresent(
          query -> bq.add(new BoostQuery(query, -1), BooleanClause.Occur.SHOULD)
      );
      // We really don't want "cell" in the title!
      bq.add(new BoostQuery(new TermQuery(new Term("journal_title", "cell")),
              -100f),
          BooleanClause.Occur.SHOULD);

      bq.add(new BoostQuery(searchEngine.newPhraseQuery("mesh_terms", "Cell Line, Tumor"),
              -30f),
          BooleanClause.Occur.SHOULD);
      bq.add(new BoostQuery(searchEngine.newPhraseQuery("mesh_terms", "Humans"),
              5f),
          BooleanClause.Occur.SHOULD);

      // Penalize journals with science-y names
      {
        final BooleanQuery.Builder ibq = new BooleanQuery.Builder();
        final Set<String> terms = new HashSet<>(Arrays.asList(
            "biochemistry", "chemistry", "molecular", "cytogenetics", "pathology"));
        for (String term : terms) {
          queryFactory.tryBuildTermFieldQuery(term,"journal_title", 2).ifPresent(
              q -> ibq.add(new BoostQuery(q, -1), BooleanClause.Occur.SHOULD)
          );
        }
        ibq.add(new WildcardQuery(new Term("journal_title", "*pathology")),
            BooleanClause.Occur.SHOULD);
        ibq.add(new BoostQuery(ibq.build(), -2), BooleanClause.Occur.SHOULD);
      }

      // Prefer abstracts with words indicating clinical trials
      {
        final BooleanQuery.Builder ibq = new BooleanQuery.Builder();
        final Set<String> terms = new HashSet<>(Arrays.asList(
            "phase 1", "phase 2", "phase 3",
            "phase I", "phase II", "phase III",
            "trial",
            "randomized",
            "patient"));
        for (String term : terms) {
          queryFactory.tryBuildTermQuery(Weighted.create(term, 1)).ifPresent(
              q -> ibq.add(q, BooleanClause.Occur.SHOULD)
          );
        }
        ibq.add(new BoostQuery(ibq.build(), 10), BooleanClause.Occur.SHOULD);
      }
      bq.add(getDiseaseTitleFilter(topic));
      tbq.add(bq.build(), BooleanClause.Occur.SHOULD);
    }
  }

  @Override
  protected void addDemographicAspects(BooleanQuery.Builder bq, AnalyzedTopic topic) {
    /* do nothing */
  }

  @Override
  protected void addOtherAspects(BooleanQuery.Builder bq,
                                  Iterable<MedicalProblem> additionalCriteria) {
    /* do nothing */
  }
}

