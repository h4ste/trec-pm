package edu.utdallas.hltri.data.dgidb.params;

public enum InteractionSource implements UriParameter {
  CIVIC("CIViC"),
  CANCER_COMMONS("CancerCommons"),
  CHEMBL("ChEMBL"),
  CLEARITY_FOUNDATION_BIOMARKERS("ClearityFoundationBiomarkers"),
  CLEARITY_FOUNDATION_CLINICAL_TRIALS("ClearityFoundationClinicalTrials"),
  DOCM("DoCM"),
  DRUGBANK("DrugBank"),
  GUIDE_TO_PHARMACOLOGY_INTERACTIONS("GuideToPharmacologyInteractions"),
  MY_CANCER_GENOME("MyCancerGenome"),
  MY_CANCER_GENOME_CLINICAL_TRIALS("MyCancerGenomeClinicalTrials"),
  PHARMGKB("PharmGKB"),
  TALC,
  TEND,
  TTD,
  TDG_CLINICAL_TRIAL("TdgClinicalTrial");

  private static final String label = "interaction_sources";
  private final String value;

  InteractionSource() {
    this.value = this.name();
  }

  InteractionSource(String value) {
    this.value = value;
  }

  @Override
  public String getValue() {
    return this.value;
  }

  public static String getLabel() {
    return label;
  }
}
