package edu.utdallas.hltri.data.cosmic;


import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import edu.utdallas.hltri.trec.pm.GeneticVariant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CosmicTest {
  private static final String DRUG_TARGET_PATH = "drug-target-sample.csv";
  private static final String RESISTANCE_MUTATION_PATH = "resistance-mutations-sample.tsv";
  private static final String GENE_CENSUS_PATH = "gene-census-sample.csv";

  private static final FileSystem jimfs = Jimfs.newFileSystem(Configuration.unix());

  @BeforeClass
  public static void initAll() throws IOException {
    Files.copy(CosmicTest.class.getResourceAsStream(DRUG_TARGET_PATH), jimfs.getPath(DRUG_TARGET_PATH));
    Files.copy(CosmicTest.class.getResourceAsStream(RESISTANCE_MUTATION_PATH), jimfs.getPath(RESISTANCE_MUTATION_PATH));
    Files.copy(CosmicTest.class.getResourceAsStream(GENE_CENSUS_PATH), jimfs.getPath(GENE_CENSUS_PATH));
  }

  @Test
  public void testGetGeneSynonyms() {
    final CosmicSettings settings = new CosmicSettings.Builder()
        .setGeneCensusPath(jimfs.getPath(GENE_CENSUS_PATH))
        .build();

    final Cosmic cosmic = new Cosmic(settings);

    final Set<String> actualSynonyms = cosmic.getGeneSynonyms("ABI1");
    final Set<String> expectedSynonyms = new HashSet<>(Arrays.asList(
        "E3B1",
        "ABI1",
        "SSH3BP1",
        "ABI-1"));

    assertEquals(expectedSynonyms, actualSynonyms);
  }

  @Test
  public void testGetGeneSynonymsEmpty() {
    final CosmicSettings settings = new CosmicSettings.Builder()
        .setGeneCensusPath(jimfs.getPath(GENE_CENSUS_PATH))
        .build();

    final Cosmic cosmic = new Cosmic(settings);

    assertTrue(cosmic.getGeneSynonyms("NULL").isEmpty());
  }

  @Test
  public void testGetDrugResistancesForGeneticVariant() {
    final CosmicSettings settings = new CosmicSettings.Builder()
        .setResistanceMutationPath(jimfs.getPath(RESISTANCE_MUTATION_PATH))
        .build();

    final Cosmic cosmic = new Cosmic(settings);

    final GeneticVariant variant = GeneticVariant.pointMutation("KIT", "N822K");
    final Set<String> actualDrugs = cosmic.getDrugResistancesForGeneticVariant(variant);
    final Set<String> expectedDrugs = Collections.singleton("Imatinib");

    assertEquals(expectedDrugs, actualDrugs);
  }

  @Test
  public void testGetDrugResistancesForGeneticVariantEmpty() {
    final CosmicSettings settings = new CosmicSettings.Builder()
        .setResistanceMutationPath(jimfs.getPath(RESISTANCE_MUTATION_PATH))
        .build();

    final Cosmic cosmic = new Cosmic(settings);

    final GeneticVariant variant = GeneticVariant.unspecified("NULL");
    assertTrue(cosmic.getDrugResistancesForGeneticVariant(variant).isEmpty());
  }

  @Test
  public void testGetDrugsByGeneTarget() {
    final CosmicSettings settings = new CosmicSettings.Builder()
        .setDrugTargetPath(jimfs.getPath(DRUG_TARGET_PATH))
        .build();

    final Cosmic cosmic = new Cosmic(settings);

    final Set<String> actualDrugs = cosmic.getDrugsByGeneTarget("BRAF");
    final Set<String> expectedDrugs = new HashSet<>(Arrays.asList(
        "Dabrafenib",
        "PLX-4720",
        "SB590885",
        "PLX-4720"));

    assertEquals(expectedDrugs, actualDrugs);
  }

  @Test
  public void testGetDrugsByGeneTargetEmpty() {
    final CosmicSettings settings = new CosmicSettings.Builder()
        .setDrugTargetPath(jimfs.getPath(DRUG_TARGET_PATH))
        .build();

    final Cosmic cosmic = new Cosmic(settings);

    assertTrue(cosmic.getDrugsByGeneTarget("NULL").isEmpty());
  }
}