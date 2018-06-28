package edu.utdallas.hltri.trec.pm.search.query;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import edu.utdallas.hltri.trec.pm.TrecSettings;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import edu.utdallas.hltri.data.nci_thesaurus.AntineoplasticAntibodies;
import edu.utdallas.hltri.data.nci_thesaurus.NciSettings;
import edu.utdallas.hltri.inquire.lucene.LuceneSearchEngine;
import edu.utdallas.hltri.logging.Logger;
import edu.utdallas.hltri.struct.Weighted;
import edu.utdallas.hltri.trec.pm.AnalyzedTopic;
import edu.utdallas.hltri.trec.pm.GeneticVariant;
import edu.utdallas.hltri.trec.pm.MedicalProblem;
import edu.utdallas.hltri.trec.pm.Mutation;
import edu.utdallas.hltri.trec.pm.LuceneSearchSettings;
import edu.utdallas.hltri.util.Expansion;
import edu.utdallas.hltri.util.Strings;

/**
 * Factory for converting an Analyzed TREC-PM Topic @{AnalyzedTopic} to a ${Query} Caches the
 * queries for each topic
 */
public abstract class LuceneTopicQueryFactory implements LuceneSingleQueryFactory,
    LuceneAspectQueryFactory {
  private static final Logger log = Logger.get(LuceneTopicQueryFactory.class);
  protected final LuceneSearchEngine<?>  searchEngine;
  protected final LuceneSearchSettings settings;
  protected final LuceneTermQueryFactory queryFactory;
  private final ConcurrentMap<AnalyzedTopic, Query> memoizer = new ConcurrentHashMap<>();

  protected LuceneTopicQueryFactory(LuceneSearchEngine<?> searchEngine,
                                    LuceneSearchSettings settings) {
    this.searchEngine = searchEngine;
    this.settings = settings;
    this.queryFactory = new LuceneTermQueryFactory(searchEngine, settings);
  }

  @Override
  public Query getQuery(AnalyzedTopic topic) {
    return memoizer.computeIfAbsent(topic, this::buildQuery);
  }

  private Query buildQuery(final AnalyzedTopic topic) {
    final BooleanQuery.Builder bq = new BooleanQuery.Builder();

    addDiseaseAspect(bq, topic);
    addGeneticAspect(bq, topic);
    if (TrecSettings.INSTANCE.useTreatments) {
      addTreatmentAspect(bq, topic);
    }
    addOtherAspects(bq, topic.getAdditionalCriteria());
    addDemographicAspects(bq, topic);
    addMiscFactors(bq, topic);
    return bq.build();
  }

  private void addGeneticAspect(BooleanQuery.Builder tbq, AnalyzedTopic topic) {
    tbq.add(getGeneticAspects(topic.getGenes()), BooleanClause.Occur.MUST);
  }

  protected Query getGeneticAspects(Iterable<GeneticVariant> genes) {
    final BooleanQuery.Builder bq = new BooleanQuery.Builder();
    bq.setDisableCoord(true);
    genes.forEach(gene -> bq.add(buildGeneticQueryAspect(gene), BooleanClause.Occur.SHOULD));
    return bq.build();
  }

  private Query buildGeneticQueryAspect(GeneticVariant gene) {
    final BooleanQuery.Builder ibq = new BooleanQuery.Builder();

    // Add gene and its synonyms
    final List<String> genes = gene.getGenesAsStream().collect(Collectors.toList());

    if (genes.size() > 1) {
      queryFactory.tryBuildTermQuery(Weighted.create(1, gene.getGene()))
          .ifPresent(tq -> ibq.add(tq, BooleanClause.Occur.SHOULD));

      final BooleanQuery.Builder jbq = new BooleanQuery.Builder();
      for (String geneName : genes) {
        queryFactory.tryBuildTermQuery(Weighted.create(geneName, 0.5))
            .ifPresent(tq -> jbq.add(tq, BooleanClause.Occur.MUST));
        jbq.add(queryFactory.buildScaledExpandedQuery(0.5, genes,
            flatten(gene.getGeneExpansions().values())),
            BooleanClause.Occur.MUST);
      }
      ibq.add(jbq.build(), BooleanClause.Occur.SHOULD);

      ibq.add(queryFactory.buildScaledExpandedQuery(0.1, genes,
          flatten(gene.getGeneExpansions().values())),
          BooleanClause.Occur.SHOULD);
    } else {
      ibq.add(queryFactory.buildExpandedQuery(gene.getGene(),
          flatten(gene.getGeneExpansions().values())),
          BooleanClause.Occur.SHOULD);
    }

    // Penalize wild-type and "non-GENE"
    gene.getGenesAsStream().forEach(geneName -> {
          queryFactory.tryBuildTermQuery(Weighted.create("non-" + geneName, 2))
              .ifPresent(
                  tq -> ibq.add(new BoostQuery(tq, -2), BooleanClause.Occur.SHOULD)
              );
          for (final Map.Entry<String, Double> field : settings.fieldWeights.entrySet()) {
            final Query spanQuery =
                searchEngine.newSpanQuery("wild " + geneName, field.getKey());
            ibq.add(new BoostQuery(spanQuery, -2 * field.getValue().floatValue()),
                BooleanClause.Occur.SHOULD);
          }
        }
    );


    // Point mutations should include the locus
    if (gene.getMutation() == Mutation.POINT_MUTATION) {
      final Collection<Expansion<Weighted<String>>> locusExpansions =
          new ArrayList<>(gene.getLocusExpansions().values());
      if (gene.getExon() != null) {
        locusExpansions.add(Expansion.singleton("Exon",
            Weighted.create(1, gene.getExon())));
      }
      locusExpansions.add(Expansion.singleton("Smashed",
          Weighted.create(1, gene.getGene() + gene.getLocus())));
      ibq.add(queryFactory.buildExpandedQuery(gene.getLocus(),
          flatten(locusExpansions)),
          BooleanClause.Occur.SHOULD);
      ibq.add(new BoostQuery(new ConstantScoreQuery(queryFactory.buildExpandedQuery(gene.getLocus(),
          flatten(locusExpansions))), 200f),
          BooleanClause.Occur.SHOULD);

      if (gene.getLocus() != null && gene.getLocus().endsWith("dup")) {
        List<String> loci = Splitter.on('_').splitToList(
            Strings.substringUntil(gene.getLocus(), "dup"));
        for (String locus : loci) {
          queryFactory.tryBuildUnweightedTermQuery(locus).ifPresent(
              tq -> ibq.add(tq, BooleanClause.Occur.SHOULD)
          );
          queryFactory.tryBuildUnweightedTermQuery(gene.getGene() + locus).ifPresent(
              tq -> ibq.add(tq, BooleanClause.Occur.SHOULD)
          );

          ibq.add(new BoostQuery(new ConstantScoreQuery(
                  queryFactory.buildExpandedQuery(gene.getLocus(),
                      flatten(locusExpansions))), 100f),
              BooleanClause.Occur.SHOULD);
        }

      }

      // Other mutations should specify the mutation
    } else {
      final Set<String> mutationSynonyms = gene.getMutation().getSynonyms();
      gene.getGenesAsStream().forEach(geneName -> {
        String keyPhrase;
        if (gene.getMutation() != Mutation.UNSPECIFIED) {
          keyPhrase = geneName + ' ' + gene.getMutation().name().toLowerCase();
        } else {
          keyPhrase = geneName + " mutation";
        }

        final Set<String> groundedSynonyms = mutationSynonyms.stream()
            .map(synonym -> geneName + ' ' + synonym)
            .collect(Collectors.toSet());
        final Collection<Weighted<String>> expansion = Weighted.fixed(groundedSynonyms, 1);
        if (gene.getMutation() == Mutation.POINT_MUTATION &&
            gene.getLocus() != null &&
            Character.isDigit(gene.getLocus().charAt(gene.getLocus().length() - 1))) {
          expansion.add(Weighted.create(gene + " nonsense", 1));
        }
        ibq.add(queryFactory.buildExpandedQuery(keyPhrase, expansion), BooleanClause.Occur.SHOULD);
      });
    }
    final BooleanQuery.Builder obq = new BooleanQuery.Builder();
    obq.add(ibq.build(), BooleanClause.Occur.SHOULD);
    return obq.build();
  }

  private void addDiseaseAspect(BooleanQuery.Builder tbq, AnalyzedTopic topic) {
    tbq.add(buildDiseaseQueryAspect(topic.getDisease()), BooleanClause.Occur.MUST);
  }

  protected Query buildDiseaseQueryAspect(MedicalProblem disease) {
    return queryFactory.buildScaledExpandedQuery(2,
        disease.toString(),
        flatten(disease.getExpansions().values()));
  }

  protected static <E, T extends Collection<? extends E>> Collection<E> flatten(
      Collection<T> collections) {
    return collections.stream().flatMap(Collection::stream).collect(Collectors.toList());
  }

  protected void addTreatmentAspect(BooleanQuery.Builder tbq, AnalyzedTopic topic) {
    tbq.add(buildTreatmentAspect(topic), BooleanClause.Occur.SHOULD);
  }

  protected Query buildTreatmentAspect(AnalyzedTopic topic) {
    if (!TrecSettings.INSTANCE.useTreatments) {
      return new MatchAllDocsQuery();
    }
    final BooleanQuery.Builder bq = new BooleanQuery.Builder();
    bq.setDisableCoord(true);

    final Set<String> drugs = new HashSet<>(flatten(topic.getDrugs().values()));

    // Get drugs from the topic
    final List<Weighted<String>> weightedDrugs =
        drugs.stream()
            .map(drug -> Weighted.create(drug, 1))
            .collect(Collectors.toList());

    // Remove redundant drugs
    Expansion.filterWeightedExpansions(weightedDrugs);

    // Add drugs to query
    final List<Query> drugQueries = weightedDrugs.stream()
        .map(queryFactory::tryBuildTermQuery)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
    bq.add(new DisjunctionMaxQuery(drugQueries, 0.1f), BooleanClause.Occur.SHOULD);

    // Add other types of treatment to query
    final Set<String> treatmentTerms = new HashSet<>(Arrays.asList(
        "prevention", "prophylaxis", "prognosis", "outcome", "survival", "treatment", "therapy",
        "personalized"
    ));

    // Prefer "target" if gene is in the title
    queryFactory.tryBuildTermQuery(Weighted.create("targeted", 1f))
        .ifPresent(tq -> {
          final BooleanQuery.Builder gbq = new BooleanQuery.Builder();
          for (GeneticVariant variant : topic.getGenes()) {
            for (String gene : variant.getGenesAsStream().collect(Collectors.toList())) {
              queryFactory.tryBuildTermFieldQuery(gene, "article_title", 1)
                  .ifPresent(gtq -> gbq.add(gtq, BooleanClause.Occur.SHOULD));
            }
            bq.add(new BooleanQuery.Builder()
                .add(gbq.build(), BooleanClause.Occur.FILTER)
                .add(tq, BooleanClause.Occur.SHOULD)
                .build(), BooleanClause.Occur.SHOULD);
          }
        });


    for (String term : treatmentTerms) {
      bq.add(queryFactory.tryBuildTermQuery(Weighted.create(term, 1))
              .orElseThrow(RuntimeException::new),
          BooleanClause.Occur.SHOULD);
    }

    // Add anti-neoplastic antibodies to query
    final Collection<String> antibodyKeyphrases = topic.getGenes().stream()
        .flatMap(GeneticVariant::getGenesAsStream)
        .map(gene -> gene + " antibody")
        .collect(Collectors.toList());
    final Set<String> antibodies = new HashSet<>(new AntineoplasticAntibodies(NciSettings.DEFAULT));
    antibodies.removeAll(drugs);
    final Collection<Weighted<String>> antiBodyExpansions =
        Weighted.fixed(Iterables.concat(antibodyKeyphrases, antibodies), 1f);

    final BooleanQuery.Builder ibq = new BooleanQuery.Builder();
    ibq.add(queryFactory.buildExpandedQuery(antibodyKeyphrases, antiBodyExpansions),
        BooleanClause.Occur.SHOULD);
    bq.add(ibq.build(), BooleanClause.Occur.SHOULD);

    log.trace("Created BooleanQuery {}", bq);
    return bq.build();
  }

  protected abstract void addDemographicAspects(BooleanQuery.Builder bq, AnalyzedTopic topic);

  protected abstract void addMiscFactors(BooleanQuery.Builder bq, AnalyzedTopic topic);

  protected abstract void addOtherAspects(BooleanQuery.Builder bq,
                                          Iterable<MedicalProblem> additionalCriteria);
}
