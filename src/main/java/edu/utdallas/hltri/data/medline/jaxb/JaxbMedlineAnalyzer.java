package edu.utdallas.hltri.data.medline.jaxb;

import com.google.common.collect.Sets;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.DelegatingAnalyzerWrapper;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;

import java.util.Set;

public class JaxbMedlineAnalyzer extends DelegatingAnalyzerWrapper {

  final Set<String> whiteSpaceAnalysisFields = Sets.newHashSet(
      "authors", "authors_initials",
      "investigators", "investigators_initials",
      "journal");

  final Analyzer whitespaceAnalyzer = new WhitespaceAnalyzer();
  final Analyzer englsihAnalyzer = new EnglishAnalyzer();


  public JaxbMedlineAnalyzer() {
    super(PER_FIELD_REUSE_STRATEGY);
  }

  @Override
  protected Analyzer getWrappedAnalyzer(String fieldName) {
    if (whiteSpaceAnalysisFields.contains(fieldName)) {
      return whitespaceAnalyzer;
    } else {
      return englsihAnalyzer;
    }
  }
}
