package edu.utdallas.hltri.data.dgidb;

import java.util.Objects;

@SuppressWarnings("unused")
public class Interaction {
  private String interactionId;
  private String interactionType;
  private String drugName;
  private String source;

  Interaction() {
  }

  void setInteractionId(String interactionId) {
    this.interactionId = interactionId;
  }

  void setInteractionType(String interactionType) {
    this.interactionType = interactionType;
  }

  void setDrugName(String drugName) {
    this.drugName = drugName;
  }

  void setSource(String source) {
    this.source = source;
  }

  Interaction(String interactionId, String interactionType, String drugName, String source) {
    this.interactionId = interactionId;
    this.interactionType = interactionType;
    this.drugName = drugName;
    this.source = source;
  }

  public String getInteractionId() {
    return interactionId;
  }

  public String getInteractionType() {
    return interactionType;
  }

  @SuppressWarnings("WeakerAccess")
  public String getDrugName() {
    return drugName;
  }

  public String getSource() {
    return source;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Interaction that = (Interaction) o;
    return Objects.equals(interactionId, that.interactionId) &&
        Objects.equals(interactionType, that.interactionType) &&
        Objects.equals(drugName, that.drugName) &&
        Objects.equals(source, that.source);
  }

  @Override
  public int hashCode() {
    return Objects.hash(interactionId, interactionType, drugName, source);
  }

  @Override
  public String toString() {
    return "Interaction{" +
        "interactionId='" + interactionId + '\'' +
        ", interactionType='" + interactionType + '\'' +
        ", drugName='" + drugName + '\'' +
        ", source='" + source + '\'' +
        '}';
  }
}
