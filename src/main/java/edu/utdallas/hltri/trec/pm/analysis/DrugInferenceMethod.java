package edu.utdallas.hltri.trec.pm.analysis;

import java.util.Collection;
import java.util.function.Function;

import edu.utdallas.hltri.trec.pm.GeneticVariant;

public interface DrugInferenceMethod extends Function<GeneticVariant, Collection<String>> {
}
