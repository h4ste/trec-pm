package edu.utdallas.hltri.data.dgidb;

import com.google.common.collect.Iterables;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class UnmatchedTerm implements SearchTerm {
  private String       searchTerm;
  private List<String> suggestions;

  public UnmatchedTerm() {
  }

  void setSearchTerm(String searchTerm) {
    this.searchTerm = searchTerm;
  }

  void setSuggestions(List<String> suggestions) {
    this.suggestions = suggestions;
  }

  UnmatchedTerm(String searchTerm, List<String> suggestions) {
    this.searchTerm = searchTerm;
    this.suggestions = suggestions;
  }

  @Override
  public String getSearchTerm() {
    return searchTerm;
  }

  public List<String> getSuggestions() {
    return suggestions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UnmatchedTerm that = (UnmatchedTerm) o;
    return Objects.equals(searchTerm, that.searchTerm) &&
        Iterables.elementsEqual(suggestions, that.suggestions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(searchTerm)
    + 31 * Objects.hash(suggestions.toArray());
  }

  @Override
  public String toString() {
    return "UnmatchedTerm{" +
        "searchTerm='" + searchTerm + '\'' +
        ", suggestions=" + suggestions +
        '}';
  }
}
