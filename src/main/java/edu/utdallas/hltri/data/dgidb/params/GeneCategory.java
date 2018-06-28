package edu.utdallas.hltri.data.dgidb.params;

import com.google.common.base.CharMatcher;

public enum GeneCategory implements UriParameter {
  KINASE,
  DNA_REPAIR,
  TUMOR_SUPPRESSOR;

  private static final String label = "gene_categories";
  private final String value = CharMatcher.is('_').replaceFrom(this.name(), ' ');

  @Override
  public String getValue() {
    return value;
  }

  public static String getLabel() {
    return label;
  }
}
