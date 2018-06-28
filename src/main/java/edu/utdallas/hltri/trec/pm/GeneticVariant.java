package edu.utdallas.hltri.trec.pm;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import edu.utdallas.hltri.struct.Weighted;
import edu.utdallas.hltri.util.Expansion;

/**
 * Created by trg19 on 6/14/2017.
 */
public class GeneticVariant {
  private final String gene;
  private final Mutation mutation;

  private final Map<String, Expansion<Weighted<String>>> geneExpansions = new HashMap<>();
  private final Map<String, Expansion<Weighted<String>>> locusExpansions = new HashMap<>();
  private final @Nullable String locus;
  private final @Nullable String exon;

  private GeneticVariant(String gene, Mutation mutation) {
    this(gene, mutation, null, null);
  }

  private GeneticVariant(String gene, Mutation mutation,
                         @Nullable String locus, @Nullable String exon) {
    this.gene = gene;
    this.mutation = mutation;
    this.locus = locus;
    this.exon = exon;
  }

  @SuppressWarnings("WeakerAccess")
  public static GeneticVariant newVariant(String gene, Mutation mutation) {
    if (mutation == Mutation.POINT_MUTATION) {
      throw new UnsupportedOperationException("Point Mutations must be created using"
          + " GeneticVariant.pointMutation(gene, locus)");
    } else if (mutation == Mutation.UNSPECIFIED) {
      throw new UnsupportedOperationException("Unspecified Mutations must be created"
          + "using GeneticVariant.unspecified(gene)");
    } else {
      return new GeneticVariant(gene, mutation);
    }
  }

  public static GeneticVariant pointMutation(String gene, String locus, String exon) {
    return new GeneticVariant(gene, Mutation.POINT_MUTATION, locus, exon);
  }

  public static GeneticVariant unspecified(String gene) {
    return new GeneticVariant(gene, Mutation.UNSPECIFIED);
  }

  void addGeneExpansions(Expansion<Weighted<String>> expansion) {
    this.geneExpansions.put(expansion.getName(), expansion);
  }

  void addLocusExpansions(Expansion<Weighted<String>> expansion) {
    this.locusExpansions.put(expansion.getName(), expansion);
  }

  public String getGene() {
    return gene;
  }

  public Stream<String> getGenesAsStream() {
    return Arrays.stream(gene.split("-"));
  }

  public Mutation getMutation() {
    return mutation;
  }

  @SuppressWarnings("WeakerAccess")
  @Nullable
  public String getLocus() {
    return locus;
  }

  @Nullable
  public String getExon() {
    return exon;
  }

  public Map<String, Expansion<Weighted<String>>> getGeneExpansions() {
    return geneExpansions;
  }

  public Map<String, Expansion<Weighted<String>>> getLocusExpansions() {
    return locusExpansions;
  }

  @Override
  public int hashCode() {
    return Objects.hash(gene, mutation, locus);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GeneticVariant that = (GeneticVariant) o;
    return Objects.equals(gene, that.gene) &&
        mutation == that.mutation &&
        Objects.equals(locus, that.locus);
  }

  @Override
  public String toString() {
    switch (mutation) {
      case UNSPECIFIED:
        return gene;
      case POINT_MUTATION:
        return mutation.name() + '(' + gene + ", " + locus + ')';
      default:
        return mutation.name() + '(' + gene +')';
    }
  }
}
