package edu.utdallas.hltri.trec.pm.analysis;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;

@SuppressWarnings("WeakerAccess")
public final class LocusNormalizer {
  private ImmutableMap<Character, String> aminoAccidByLetter = new ImmutableMap.Builder<Character, String>()
  .put('A', "Ala")
  .put('R', "Arg")
  .put('N', "Asn")
  .put('D', "Asp")
  .put('C', "Cys")
  .put('E', "Glu")
  .put('Q', "Gln")
  .put('G', "Gly")
  .put('H', "His")
  .put('I', "Ile")
  .put('L', "Leu")
  .put('K', "Lys")
  .put('M', "Met")
  .put('F', "Phe")
  .put('P', "Pro")
  .put('S', "Ser")
  .put('T', "Thr")
  .put('W', "Trp")
  .put('Y', "Tyr")
  .put('V', "Val")
  .build();

  public @Nonnull String normalize(@Nonnull String locus) {
    final char wildLetter = locus.charAt(0);
    final String wildAbbrev = aminoAccidByLetter.get(wildLetter);

    final char mutantLetter = locus.charAt(locus.length() - 1);
    // Check for non-sense mutation
    if (Character.isAlphabetic(mutantLetter)) {
      // We're safe so just replace the letters at either ends
      final String mutantAbbrev = aminoAccidByLetter.get(mutantLetter);
      return wildAbbrev + locus.substring(1, locus.length() - 1) + mutantAbbrev;
    } else {
      // We have a non-sense mutation
      return wildAbbrev + locus.substring(1);
    }
  }

  public @Nonnull String addProteinIdentifier(@Nonnull String locus) {
    return "p." + locus;
  }
}
