package edu.utdallas.hltri.data.dgidb.params;

public enum DrugType implements UriParameter {
  ANTINEOPLASTIC,
  OTHER;

  private static final String label = "drug_types";

  @Override
  public String getValue() {
    return this.name();
  }

  public static String getLabel() {
    return label;
  }
}
