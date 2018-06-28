package edu.utdallas.hltri.data.dgidb;

import com.google.common.collect.Iterables;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class MatchedTerm implements SearchTerm{
  private String            searchTerm;
  private String            geneName;
  private String            geneLongName;
  private List<String>      geneCategories;
  private List<Interaction> interactions;

  MatchedTerm() {

  }

  void setSearchTerm(String searchTerm) {
    this.searchTerm = searchTerm;
  }

  void setGeneName(String geneName) {
    this.geneName = geneName;
  }

  void setGeneLongName(String geneLongName) {
    this.geneLongName = geneLongName;
  }

  void setGeneCategories(List<String> geneCategories) {
    this.geneCategories = geneCategories;
  }

  void setInteractions(List<Interaction> interactions) {
    this.interactions = interactions;
  }

  MatchedTerm(String searchTerm, String geneName, String geneLongName, List<String> geneCategories, List<Interaction> interactions) {
    this.searchTerm = searchTerm;
    this.geneName = geneName;
    this.geneLongName = geneLongName;
    this.geneCategories = geneCategories;
    this.interactions = interactions;
  }

  @Override
  public String getSearchTerm() {
    return searchTerm;
  }

  public String getGeneName() {
    return geneName;
  }

  public String getGeneLongName() {
    return geneLongName;
  }

  public List<String> getGeneCategories() {
    return geneCategories;
  }

  public List<Interaction> getInteractions() {
    return interactions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MatchedTerm that = (MatchedTerm) o;
    return Objects.equals(searchTerm, that.searchTerm) &&
        Objects.equals(geneName, that.geneName) &&
        Objects.equals(geneLongName, that.geneLongName) &&
        Iterables.elementsEqual(geneCategories, that.geneCategories) &&
        Iterables.elementsEqual(interactions, that.interactions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(searchTerm, geneName, geneLongName)
    + 13 * Objects.hash(geneCategories.toArray())
    + 31 * Objects.hash(interactions.toArray());
  }

  @Override
  public String toString() {
    return "MatchedTerm{" +
        "searchTerm='" + searchTerm + '\'' +
        ", geneName='" + geneName + '\'' +
        ", geneLongName='" + geneLongName + '\'' +
        ", geneCategories=" + geneCategories +
        ", interactions=" + interactions +
        '}';
  }
}
