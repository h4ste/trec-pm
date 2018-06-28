package edu.utdallas.hltri.data.medline.basic;

import java.util.HashMap;
import java.util.Map;

/**
 * Incomplete (currently-being-parsed) Medline Article used by MedlineXmlHandler
 * Created by trg19 on 6/6/2017.
 */
final class MedlineArticleState {
  String dateContext = null;
  int year = -1;
  int month = -1;
  int day = -1;
  String sectionLabel = null;
  String sectionNlmCategory = null;
  StringBuilder abstractText = new StringBuilder();
  Map<String, StringBuilder> abstractTexts = new HashMap<>();
  StringBuilder meshTerm = new StringBuilder();

  void reset() {
    dateContext = null;
    resetDate();
    sectionLabel = null;
    sectionNlmCategory = null;
    abstractText.setLength(0);
    abstractTexts.clear();
    meshTerm.setLength(0);
  }

  void resetDate() {
    year = -1;
    month = -1;
    day = -1;
  }
}
