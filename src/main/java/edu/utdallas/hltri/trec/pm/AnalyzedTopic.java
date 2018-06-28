package edu.utdallas.hltri.trec.pm;

import com.google.common.base.Splitter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.utdallas.hltri.logging.Logger;
import edu.utdallas.hltri.struct.Weighted;
import edu.utdallas.hltri.trec.pm.analysis.DrugInferenceMethod;
import edu.utdallas.hltri.trec.pm.analysis.DrugNameFilter;
import edu.utdallas.hltri.trec.pm.analysis.GeneticVariantParser;
import edu.utdallas.hltri.util.Expander;
import edu.utdallas.hltri.util.Expansion;


/**
 * Created by travis on 6/19/17.
 */
public class AnalyzedTopic {
  private static final Logger log = Logger.get(AnalyzedTopic.class);

  private final Topic                                   topic;
  private final MedicalProblem                          disease;
  private final Collection<MedicalProblem>              additionalCriteria;
  private       Collection<GeneticVariant>              genes;
  private       Map<String, Collection<String>> drugs = new HashMap<>();

  public AnalyzedTopic(Topic topic) {
    this.topic = topic;
    this.disease = new MedicalProblem(topic.getDisease());
    this.additionalCriteria = topic.getAdditionalCriteria().stream()
        .map(MedicalProblem::new).collect(Collectors.toList());
  }

  @SuppressWarnings("UnusedReturnValue")
  public AnalyzedTopic parseGenes(final GeneticVariantParser parser) {
    this.genes = topic.getGeneticVariations().stream()
        .map(parser::parse)
        .collect(Collectors.toSet());
    return this;
  }

  @SuppressWarnings("UnusedReturnValue")
  public AnalyzedTopic addInferedDrugsForGene(String name, Collection<String> drugs) {
    this.drugs.put(name, drugs);
    return this;
  }

  @SuppressWarnings("UnusedReturnValue")
  public AnalyzedTopic inferDrugsFromGene(String name,
                                          DrugInferenceMethod drugInferenceMethod,
                                          DrugNameFilter filter) {
    drugs.put(name, genes.stream()
        .map(drugInferenceMethod)
        .flatMap(Collection::stream)
        .filter(filter)
        .collect(Collectors.toSet()));
    return this;
  }

  @SuppressWarnings("UnusedReturnValue")
  public AnalyzedTopic expandProblems(final Expander<CharSequence, Weighted<String>> expander) {
    expandMedicalProblem(disease, expander);
    for (MedicalProblem problem : additionalCriteria) {
      expandMedicalProblem(problem, expander);
    }
    return this;
  }

  private void expandMedicalProblem(final MedicalProblem problem,
                                    final Expander<CharSequence, Weighted<String>> expander) {
    log.debug("Expanding problem |{}| using {}", problem, expander.getName());
    problem.addExpansions(expander.expand(problem));
    for (Collection<Weighted<String>> expansions : problem.getExpansions().values()) {
      for (Weighted<String> expansion : expansions) {
        final String synonym = expansion.getValue();
        problem.addExpansions(expander.expand(synonym));
      }
    }
  }

  @SuppressWarnings("UnusedReturnValue")
  public AnalyzedTopic expandGeneNames(final Expander<? super String, Weighted<String>> expander) {
    for (GeneticVariant gene : genes) {
      gene.getGenesAsStream().forEach(
          geneName -> {
            log.debug("Expanding gene |{}| using {}", geneName, expander.getName());
            gene.addGeneExpansions(expander.expand(geneName));
          }
      );
    }
    return this;
  }

  @SuppressWarnings("UnusedReturnValue")
  public AnalyzedTopic expandLocus(final Function<String, String> expander) {
    for (GeneticVariant gene : genes) {
      if (gene.getMutation() == Mutation.POINT_MUTATION) {
        final String locus = gene.getLocus();
        assert locus != null : "encountered null locus!";
        if (locus.endsWith("dup")) {
          final Iterable<String> loci =
              Splitter.on('_').split(locus.substring(0, locus.length() - 3));
          log.debug("Expanding duplication loci |{}|", loci);
          for (String subLocus : loci) {
            final String expandedLocus = expander.apply(subLocus);
            log.debug("Expanding locus |{}| to {}", subLocus, expandedLocus);
            final Weighted<String> weightedLocus = Weighted.create(expandedLocus, 1f);
            gene.addLocusExpansions(Expansion.singleton("3LetterAbbrev", weightedLocus));
          }
        } else {
          final String expandedLocus = expander.apply(locus);
          log.debug("Expanding locus |{}| to {}", locus, expandedLocus);
          final Weighted<String> weightedLocus = Weighted.create(expandedLocus, 1f);
          gene.addLocusExpansions(Expansion.singleton("3LetterAbbrev", weightedLocus));
        }
      }
    }
    return this;
  }

  public Topic getTopic() {
    return topic;
  }

  public MedicalProblem getDisease() {
    return disease;
  }

  public Collection<MedicalProblem> getAdditionalCriteria() {
    return additionalCriteria;
  }

  public Collection<GeneticVariant> getGenes() {
    return genes;
  }

  public Map<String, Collection<String>> getDrugs() {
    return drugs;
  }
}