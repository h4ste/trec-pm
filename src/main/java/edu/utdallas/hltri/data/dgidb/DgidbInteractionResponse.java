package edu.utdallas.hltri.data.dgidb;

import com.google.common.collect.Iterables;

import java.util.List;
import java.util.Objects;

@SuppressWarnings({"unused", "WeakerAccess"})
public class DgidbInteractionResponse {

  private List<MatchedTerm>    matchedTerms;
  private List<UnmatchedTerm> unmatchedTerms;

  DgidbInteractionResponse() {
  }

  void setMatchedTerms(List<MatchedTerm> matchedTerms) {
    this.matchedTerms = matchedTerms;
  }

  void setUnmatchedTerms(List<UnmatchedTerm> unmatchedTerms) {
    this.unmatchedTerms = unmatchedTerms;
  }

  DgidbInteractionResponse(List<MatchedTerm> matchedTerms, List<UnmatchedTerm> unmatchedTerms) {
    this.matchedTerms = matchedTerms;
    this.unmatchedTerms = unmatchedTerms;
  }

  public List<MatchedTerm> getMatchedTerms() {
    return matchedTerms;
  }

  public List<UnmatchedTerm> getUnmatchedTerms() {
    return unmatchedTerms;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DgidbInteractionResponse that = (DgidbInteractionResponse) o;
    return Iterables.elementsEqual(matchedTerms, that.matchedTerms) &&
        Iterables.elementsEqual(unmatchedTerms, that.unmatchedTerms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(matchedTerms.toArray())
    + 31 * Objects.hash(unmatchedTerms.toArray());
  }

  @Override
  public String toString() {
    return "DgidbInteractionResponse{" +
        "matchedTerms=" + matchedTerms +
        ", unmatchedTerms=" + unmatchedTerms +
        '}';
  }
}
