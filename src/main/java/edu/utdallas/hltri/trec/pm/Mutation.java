package edu.utdallas.hltri.trec.pm;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import edu.utdallas.hltri.util.Expansion;

/**
 * Created by travis on 6/15/17.
 */
public enum Mutation {
  AMPLIFICATION(Arrays.asList("increased copy number", "activation"),
                Arrays.asList("inhibitor", "antagonist", "suppressor", "antisense", "blocker", "suppressor")),

  DUPLICATION(AMPLIFICATION.getSynonyms(), AMPLIFICATION.getDrugTargets()),

  TRANSLOCATION(Collections.singleton("fusion"),
                AMPLIFICATION.getDrugTargets()),

  DELETION(Collections.singleton("loss"),
           Arrays.asList("agonist", "activator", "inducer", "potentiator", "stimulator")),

  POINT_MUTATION(Iterables.concat(AMPLIFICATION.getSynonyms(), DELETION.getSynonyms()),
                 Iterables.concat(AMPLIFICATION.getDrugTargets(), DELETION.getDrugTargets())),

  INACTIVATION(DELETION.getSynonyms(),
               DELETION.getDrugTargets()),

  UNSPECIFIED(POINT_MUTATION.getSynonyms(), POINT_MUTATION.getDrugTargets());

  final Set<String> synonyms;

  final Set<String> drugTargets;

  Mutation(Iterable<String> synonyms, Iterable<String> drugTargets) {
    this.synonyms = ImmutableSet.copyOf(synonyms);
    this.drugTargets = ImmutableSet.copyOf(drugTargets);
  }

  public Set<String> getDrugTargets() {
    return drugTargets;
  }

  public Set<String> getSynonyms() {
    return synonyms;
  }

  public static Set<String> getDrugTargets(GeneticVariant variant) {
    final Set<String> drugTargets = variant.getGenesAsStream()
        .flatMap(gene -> variant.getMutation().getDrugTargets().stream()
            .map(target -> gene + ' ' + target))
        .collect(Collectors.toSet());
    Expansion.reduceEntries(drugTargets);
    return drugTargets;
  }
}
