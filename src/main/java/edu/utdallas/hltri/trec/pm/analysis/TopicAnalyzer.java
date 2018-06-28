package edu.utdallas.hltri.trec.pm.analysis;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import edu.utdallas.hlt.medbase.snomed.SNOMEDManager;
import edu.utdallas.hlt.medbase.snomed.SNOMEDRelationshipDirection;
import edu.utdallas.hlt.medbase.snomed.SNOMEDRelationshipType;
import edu.utdallas.hlt.medbase.umls.UMLSManager;
import edu.utdallas.hltri.data.cosmic.Cosmic;
import edu.utdallas.hltri.data.cosmic.CosmicSettings;
import edu.utdallas.hltri.data.fda_labels.FdaDrugLabels;
import edu.utdallas.hltri.logging.Logger;
import edu.utdallas.hltri.trec.pm.AnalyzedTopic;
import edu.utdallas.hltri.trec.pm.Mutation;
import edu.utdallas.hltri.trec.pm.Topic;
import edu.utdallas.hltri.trec.pm.TrecSettings;
import edu.utdallas.hltri.trec.pm.io.TopicParser;
import edu.utdallas.hltri.util.Expander;

public class TopicAnalyzer {
  private static final Logger log = Logger.get(TopicAnalyzer.class);

  private final List<Topic> rawTopics;
  private List<AnalyzedTopic> analyzedTopics;

  public TopicAnalyzer(final Path topicsPath) {
    final TopicParser topicParser = new TopicParser();
    log.info("Parsing topics from |{}|...", topicsPath);
    this.rawTopics = topicParser.parse(topicsPath);
  }

  @SuppressWarnings("UnusedReturnValue")
  public TopicAnalyzer analyzeTopics() {
    log.info("Analyzing topics...");
    this.analyzedTopics = rawTopics.stream()
        .map(AnalyzedTopic::new)
        .collect(Collectors.toList());
    log.debug("Parsing genes...");
    try (GeneticVariantParser geneParser = new GeneticVariantParser()) {
      for (AnalyzedTopic t : analyzedTopics) {
        t.parseGenes(geneParser);
      }
    }
    log.debug("Inferring Drugs...");
    for (AnalyzedTopic t : analyzedTopics) {
      t.inferDrugsFromGene("Mutation Targets",
          Mutation::getDrugTargets, DrugNameFilter.NONE);
    }
    final DrugNameFilter filter = new DrugGenericNameSuffixFilter();
    {
      final Cosmic cosmic = new Cosmic(CosmicSettings.DEFAULT);
      for (AnalyzedTopic t : analyzedTopics) {
        t.inferDrugsFromGene("Cosmic:Drug Targets",
            v -> v.getGenesAsStream()
                .map(cosmic::getDrugsByGeneTarget)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet()), DrugNameFilter.NONE);
        t.inferDrugsFromGene("Cosmic:Resistances",
            cosmic::getDrugResistancesForGeneticVariant,
            DrugNameFilter.NONE);
      }
    }
    {
      final CachingDgidbDrugInferrer dgidb =
          new CachingDgidbDrugInferrer(TrecSettings.INSTANCE.dgidbCachePath);

      for (AnalyzedTopic t : analyzedTopics) {
        dgidb.inferDrugs(t, filter);
      }
      dgidb.save();
    }
    {
      final FdaDrugLabels labels =
          FdaDrugLabels.fromCache(TrecSettings.INSTANCE.fdaLabelsCachePath);
      for (AnalyzedTopic t : analyzedTopics) {
        t.inferDrugsFromGene("FDA Labels", labels, DrugNameFilter.NONE);
      }
    }
    return this;
  }

  @SuppressWarnings("UnusedReturnValue")
  public TopicAnalyzer expandTopics() {
    log.info("Expanding topics...");
    log.debug("Beginning UMLS expansions...");
    try (final UMLSManager umls = new UMLSManager()) {
      for (AnalyzedTopic t : analyzedTopics) {
        t.expandProblems(umls.withFixedWeight(.5d));
      }
    }

    log.debug("Beginning SNOMED expansion...");
    try (final SNOMEDManager snomed = new SNOMEDManager()) {
      for (AnalyzedTopic t : analyzedTopics) {
        t.expandProblems(snomed.expandBy(SNOMEDRelationshipType.IS_A,
            2,
            SNOMEDRelationshipDirection.CHILDREN).withFixedWeight(.5d));
      }
    }

    log.debug("Beginning COSMIC expansion...");
    {
      final Cosmic cosmic = new Cosmic(CosmicSettings.DEFAULT);
      for (AnalyzedTopic topic : analyzedTopics) {
        topic.expandGeneNames(
            Expander.fromFunction(
                "Cosmic:GeneSynonyms",
                cosmic::getGeneSynonyms
            ).withFixedWeight(.1d)
        );
      }
    }

    log.debug("Expanding gene loci...");
    {
      final LocusNormalizer normalizer = new LocusNormalizer();
      for (AnalyzedTopic topic : analyzedTopics) {
        topic.expandLocus(normalizer::normalize);
        topic.expandLocus(normalizer::addProteinIdentifier);
        topic.expandLocus(l -> normalizer.addProteinIdentifier(normalizer.normalize(l)));
      }
    }
    return this;
  }

  public List<AnalyzedTopic> getAnalyzedTopics() {
    return analyzedTopics;
  }
}
