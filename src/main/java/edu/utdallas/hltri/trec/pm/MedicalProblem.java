package edu.utdallas.hltri.trec.pm;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import edu.utdallas.hltri.struct.Weighted;
import edu.utdallas.hltri.util.CharMatchers;
import edu.utdallas.hltri.util.Expansion;

/**
 * Created by trg19 on 6/14/2017.
 */
public class MedicalProblem extends Expandable {
  public MedicalProblem(String problem) {
    super(problem);
  }
}
