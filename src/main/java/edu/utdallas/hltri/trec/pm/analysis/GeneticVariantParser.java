package edu.utdallas.hltri.trec.pm.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import edu.utdallas.hltri.logging.Logger;
import edu.utdallas.hltri.trec.pm.GeneticVariant;
import edu.utdallas.hltri.trec.pm.Mutation;

/**
 * Parses Gene, Mutation Type, and Point information from a given genetic variation String
 * Usage: new GeneticVariantParser().parse(string)
 */
public class GeneticVariantParser implements AutoCloseable {
  private static final Logger log = Logger.get(GeneticVariantParser.class);

  private final Pattern pointMutationPattern =
      Pattern.compile("([A-Z0-9]+)\\s*(Exon [0-9]+)?\\s*\\(([a-zA-Z0-9_]+)\\)");
  private final Pattern unspecifiedMutationPattern = Pattern.compile("([A-Z0-9]+)");
  private final EnumMap<Mutation, Pattern> lexica;

  @SuppressWarnings("WeakerAccess")
  public GeneticVariantParser() {
    lexica = new EnumMap<>(Mutation.class);
    lexica.put(Mutation.AMPLIFICATION, lexicon("Amplification", "Amplify"));
    lexica.put(Mutation.DUPLICATION, lexicon("Duplication", "Duplicate"));
    lexica.put(Mutation.TRANSLOCATION,
        lexicon("Fusion", "Fuse", "Translate", "Translocation"));
    lexica.put(Mutation.DELETION, lexicon("Deletion", "Delete", "Loss"));
    lexica.put(Mutation.INACTIVATION, lexicon("Inactivating", "Inactive"));
  }

  private static Pattern lexicon(String... words) {
    return Pattern.compile(
            Arrays.stream(words)
                .collect(
                    Collectors.joining("|", "([A-Z0-9-]+).*(\\b", "\\b)")));
  }

  public GeneticVariant parse(@Nonnull String string) {
    final List<GeneticVariant> mutations = new ArrayList<>(1);
    {
      final Matcher matcher = pointMutationPattern.matcher(string);
      if (matcher.find()) {
        final GeneticVariant variant =
            GeneticVariant.pointMutation(matcher.group(1), matcher.group(3), matcher.group(2));
        log.debug("Parsed |{}| as {} (match = |{}|)", string, variant, matcher.group(0));
        mutations.add(variant);
      }
    }

    for (Map.Entry<Mutation, Pattern> mutation : lexica.entrySet()) {
      final Matcher matcher = mutation.getValue().matcher(string);
      if (matcher.find()) {
        final GeneticVariant variant =
            GeneticVariant.newVariant(matcher.group(1), mutation.getKey());
        log.debug("Parsed |{}| as {} (match = |{}|)", string, variant, matcher.group(2));
        mutations.add(variant);
      }
    }

    if (mutations.isEmpty()) {
      final Matcher matcher = unspecifiedMutationPattern.matcher(string);
      if (matcher.matches()) {
        final GeneticVariant variant = GeneticVariant.unspecified(matcher.group(1));
        log.debug("Parsed |{}| as {}", string, variant);
        mutations.add(variant);
      } else {
        throw new IllegalStateException("Unable to detect any genetic variants in |" +
            string + "|");
      }
    }

    if (mutations.size() > 1) {
      throw new IllegalStateException("Detected " + mutations.size() + " mutations in |" +
          string + "|");
    }

    return mutations.get(0);
  }

  @Override public void close() {
    // do nothing
  }
}
