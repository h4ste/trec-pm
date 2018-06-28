package edu.utdallas.hltri.trec.pm;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import edu.utdallas.hltri.struct.Weighted;
import edu.utdallas.hltri.util.CharMatchers;
import edu.utdallas.hltri.util.Expansion;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * Created by trg19 on 6/14/2017.
 */
public class Expandable implements CharSequence {
  private final String term;
  private final Map<String, Collection<Weighted<String>>> expansions = new HashMap<>();

  public Expandable(String term) {
    this.term = term;
  }

  @Override
  public int length() {
    return term.length();
  }

  @Override
  public char charAt(int index) {
    return term.charAt(index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return term.subSequence(start, end);
  }

  @Nonnull
  @Override
  public String toString() {
    return term;
  }

  public void addExpansions(Expansion<Weighted<String>> expansion) {
    expansion.removeIf(w -> w.getValue().length() < 3);
    this.expansions.put(expansion.getName(), expansion);
    Expansion.filterWeightedExpansions(
        Iterables.concat(expansions.values()),
        Splitter.on(CharMatcher.whitespace().or(CharMatchers.PUNCTUATION)));
  }

  public Map<String, Collection<Weighted<String>>> getExpansions() {
    return expansions;
  }

  public Collection<Weighted<String>> getFlatExpansions() {
    return expansions.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
  }

}
