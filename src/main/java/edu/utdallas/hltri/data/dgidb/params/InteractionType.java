package edu.utdallas.hltri.data.dgidb.params;

public enum InteractionType implements UriParameter {
  ACTIVATOR,
  INHIBITOR,
  UNKNOWN;

  private static final String label = "interaction_types";

  @Override
  public String getValue() {
    return name().toLowerCase();
  }

  public static String getLabel() {
    return label;
  }
}
