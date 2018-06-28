package edu.utdallas.hltri.trec.pm.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.utdallas.hltri.logging.Logger;

public class DrugGenericNameSuffixFilter implements DrugNameFilter {
  private static final Logger log = Logger.get(DrugGenericNameSuffixFilter.class);

  private final Pattern pattern =
      Pattern.compile(
          Stream.of(
              "mapimod",
              "tinib",
              "ciclib",
              "cidib",
              "mab"
          )
          .map(s -> "(.+" + s + "$)")
          .collect(Collectors.joining("|")),
          Pattern.CASE_INSENSITIVE);

  @Override
  public boolean test(CharSequence drug) {
    final Matcher matcher = pattern.matcher(drug);
    if (matcher.matches()) {
      log.trace("drug |{}| passed filter by {}", drug, matcher.group(0));
      return true;
    } else {
      log.trace("drug |{}| failed filter {}!", drug, pattern.toString());
      return false;
    }
  }
}
