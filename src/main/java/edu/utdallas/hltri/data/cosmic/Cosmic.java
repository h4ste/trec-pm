package edu.utdallas.hltri.data.cosmic;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSetMultimap;

import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import edu.utdallas.hltri.logging.Logger;
import edu.utdallas.hltri.trec.pm.GeneticVariant;
import edu.utdallas.hltri.util.Lazy;


/**
 * Manager for COSMIC, the Catalogue Of Somatic Mutations In Cancer.
 * The world's largest and most comprehensive resource for exploring the impact of somatic
 * mutations in human cancer.
 * See: http://cancer.sanger.ac.uk/cosmic
 *
 * All tables are lazily-loaded from the disk
 */
public class Cosmic {
  private static final Logger log = Logger.get(Cosmic.class);

  private final CosmicSettings settings;

  private Supplier<ImmutableSetMultimap<String, String>> geneTargetsByDrug =
      Lazy.lazily(this::parseDrugTargets);

  private Supplier<ImmutableSetMultimap<GeneticVariant, String>> drugResistancesByGeneticVariant =
      Lazy.lazily(this::parseResistanceMutations);

  private Supplier<ImmutableSetMultimap<String, String>> geneSynonyms =
      Lazy.lazily(this::parseCancerGeneCensus);

  private final Pattern digits = Pattern.compile("\\d+");


  public Cosmic(@Nonnull CosmicSettings settings) {
    this.settings = settings;
  }

  private ImmutableSetMultimap<String, String> parseDrugTargets() {
    return parseDrugTargets(settings.drugTargetPath);
  }

  private ImmutableSetMultimap<String, String> parseDrugTargets(Path drugTargetPath) {
    final RowListProcessor rowProcessor = new RowListProcessor();
    final CsvParserSettings settings = new CsvParserSettings();

    settings.setLineSeparatorDetectionEnabled(true);
    settings.selectFields("Drug", "Drug Target");
    settings.setProcessor(rowProcessor);
    settings.setHeaderExtractionEnabled(true);

    final CsvParser parser = new CsvParser(settings);
    try (BufferedReader reader = Files.newBufferedReader(drugTargetPath)) {
      parser.parse(reader);
    } catch (IOException e) {
      log.error("Failed to parse drug targets", e);
      throw new RuntimeException("Failed to parse drug targets", e);
    }

    final Splitter splitter = Splitter.on(',').omitEmptyStrings().trimResults();
    final ImmutableSetMultimap.Builder<String, String> mmap = ImmutableSetMultimap.builder();
    for (String[] row : rowProcessor.getRows()) {
      mmap.putAll(row[0], splitter.split(row[1]));
    }
    return mmap.build();
  }

  private ImmutableSetMultimap<GeneticVariant, String> parseResistanceMutations() {
    return parseResistanceMutations(settings.resistanceMutationPath);
  }

  private ImmutableSetMultimap<GeneticVariant, String> parseResistanceMutations(Path resistanceMutationPath) {
    final RowListProcessor rowProcessor = new RowListProcessor();

    final TsvParserSettings settings = new TsvParserSettings();
    settings.setLineSeparatorDetectionEnabled(true);
//    settings.setHeaderExtractionEnabled(true);
    settings.selectFields("Gene Name", "AA Mutation", "Drug Name");
    settings.setProcessor(rowProcessor);
    settings.setHeaderExtractionEnabled(true);

    final TsvParser parser = new TsvParser(settings);
    try (BufferedReader reader = Files.newBufferedReader(resistanceMutationPath)) {
      parser.parse(reader);
    } catch (IOException e) {
      log.error("Failed to parse resistance mutations", e);
      throw new RuntimeException("Failed to parse resistance mutations", e);
    }

    final ImmutableSetMultimap.Builder<GeneticVariant, String> mmap = ImmutableSetMultimap.builder();
    for (String[] row : rowProcessor.getRows()) {
      // Loci are encoded with the prefix "p.", so remove the prefix
      final String locus = row[1].substring(2);
      final GeneticVariant key = GeneticVariant.pointMutation(row[0], locus, null);
      mmap.put(key, row[2]);
    }
    return mmap.build();
  }


  private ImmutableSetMultimap<String, String> parseCancerGeneCensus() {
    return parseCancerGeneCensus(settings.geneCensusPath);
  }

  private ImmutableSetMultimap<String, String> parseCancerGeneCensus(final Path cancerGeneCensusPath) {
    final RowListProcessor rowProcessor = new RowListProcessor();
    final CsvParserSettings settings = new CsvParserSettings();

    settings.setLineSeparatorDetectionEnabled(true);
    settings.selectFields("Gene Symbol", "Synonyms");
    settings.setProcessor(rowProcessor);
    settings.setHeaderExtractionEnabled(true);

    final CsvParser parser = new CsvParser(settings);
    try (BufferedReader reader = Files.newBufferedReader(cancerGeneCensusPath)) {
      parser.parse(reader);
    } catch (IOException e) {
      log.error("Failed to parse drug targets", e);
      throw new RuntimeException("Failed to parse drug targets", e);
    }

    final Splitter splitter = Splitter.on(',').omitEmptyStrings().trimResults();
    final ImmutableSetMultimap.Builder<String, String> mmap = ImmutableSetMultimap.builder();
    for (String[] row : rowProcessor.getRows()) {
      if (!Strings.isNullOrEmpty(row[1])) {
        mmap.putAll(row[0], splitter.split(row[1]));
      }
    }

    return mmap.build();
  }

  /**
   * Returns a set of synonyms for a given gene
   * @param gene a gene
   * @return set of gene synonyms
   */
  public @Nonnull Set<String> getGeneSynonyms(String gene) {
    final Set<String> synonyms = new HashSet<>(this.geneSynonyms.get().get(gene));
    synonyms.removeIf(synonym -> digits.matcher(synonym).matches() || synonym.length() < 3);
    return synonyms;
  }

  /**
   * Returns the set of drugs against which resistance is conferred by the specified genetic
   * variant
   * @param variant a genetic variant
   * @return set of drugs for which the specific genetic variant is known to confer resistance
   */
  public @Nonnull Set<String> getDrugResistancesForGeneticVariant(GeneticVariant variant) {
    return this.drugResistancesByGeneticVariant.get().get(variant);
  }

  /**
   * Returns the set of drugs that target the specified gene
   * @param gene name of the gene
   * @return Set of drugs targeting the specific gene
   */
  public @Nonnull Set<String> getDrugsByGeneTarget(String gene) {
    // Note: Guava caches the inverse of an immutable map!
    return this.geneTargetsByDrug.get().inverse().get(gene);
  }
}

