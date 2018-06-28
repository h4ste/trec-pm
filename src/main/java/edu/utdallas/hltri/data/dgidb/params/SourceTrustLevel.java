package edu.utdallas.hltri.data.dgidb.params;

public enum SourceTrustLevel implements UriParameter {
  EXPERT_CURATED("Expert%20curated"),
  NONCURATED("Non-curated");

  private static final String label = "source_trust_levels";

  private final String value;

  SourceTrustLevel(String value) {
    this.value = value;
  }

  @Override
  public String getValue() {
    return value;
  }

  public static String getLabel() {
    return label;
  }
}
