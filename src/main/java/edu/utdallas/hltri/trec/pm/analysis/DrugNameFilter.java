package edu.utdallas.hltri.trec.pm.analysis;

import java.util.function.Predicate;

public interface DrugNameFilter extends Predicate<CharSequence> {
  DrugNameFilter NONE = x -> true;
}
