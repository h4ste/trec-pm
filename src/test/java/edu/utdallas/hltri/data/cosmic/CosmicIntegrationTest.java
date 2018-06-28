package edu.utdallas.hltri.data.cosmic;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.utdallas.hltri.IntegrationTest;
import edu.utdallas.hltri.trec.pm.GeneticVariant;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class CosmicIntegrationTest {
  private Cosmic cosmic;

  @Before
  public void init() {
    cosmic = new Cosmic(CosmicSettings.DEFAULT);
  }

  @Test
  public void testGetGeneSynonyms() {
    final Collection<String> actualSynonyms = cosmic.getGeneSynonyms("BRAF");
    final Collection<String> expectedSynonyms = new HashSet<>(Arrays.asList(
        "MGC126806",
        "673",
        "BRAF",
        "ENSG00000157764",
        "MGC138284",
        "RAFB1",
        "P15056",
        "B-raf 1",
        "BRAF1",
        "B-raf1"));

    assertEquals(expectedSynonyms, actualSynonyms);
  }

  @Test
  public void testGetDrugResistancesForGeneticVariant() {
    final GeneticVariant variant = GeneticVariant.pointMutation("EGFR", "T790M");
    final Collection<String> actualDrugs = cosmic.getDrugResistancesForGeneticVariant(variant);
    final Collection<String> expectedDrugs = new HashSet<>(Arrays.asList(
        "Afatinib",
        "Erlotinib",
        "Gefitinib",
        "Osimertinib",
        "XL647",
        "Tyrosine kinase inhibitor - NS"
    ));

    assertEquals(expectedDrugs, actualDrugs);
  }

  @Test
  public void testGetDrugsByGeneTarget() {
    final Set<String> actualDrugs = cosmic.getDrugsByGeneTarget("KIT");
    final Set<String> expectedDrugs = new HashSet<>(Arrays.asList(
        "Amuvatinib",
        "Axitinib",
        "Cabozantinib",
        "Dasatinib",
        "Imatinib",
        "Linifanib",
        "Masitinib",
        "Motesanib",
        "OSI-930",
        "Pazopanib",
        "Sorafenib",
        "Sunitinib"));

    assertEquals(expectedDrugs, actualDrugs);
  }
}
