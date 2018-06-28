package edu.utdallas.hltri.data.medline;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import edu.utdallas.hltri.scribe.text.Identifiable;
import edu.utdallas.hltri.trec.pm.search.Displayable;

/**
 * Representation of a MedlineArticle
 * Consumed/Indexed by MedlineIndexer
 * Created by trg19 on 6/6/2017.
 */
@SuppressWarnings("unused")
public interface MedlineArticle extends Displayable, Identifiable {

  String getPubmedId();

  LocalDate getCreationDate();
  LocalDate getCompletionDate();
  LocalDate getRevisionDate();

  String getJournalTitle();
  String getArticleTitle();

  String getAbstractText();
  Map<String, String> getAbstractTexts();

  List<String> getPublicationTypes();
  List<String> getChemicals();

  List<String> getMeshTerms();

  @Override
  default String getSearchHeading() {
    return '"' + getArticleTitle() + " [" + getPubmedId() + ']';
  }

  @Override
  default String getId() {
    return getPubmedId();
  }
}
