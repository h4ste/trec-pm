package edu.utdallas.hltri.data.medline.basic;

import edu.utdallas.hltri.data.medline.MedlineArticle;
import edu.utdallas.hltri.inquire.lucene.LuceneUtils;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

public class SimpleMedlineArticle implements MedlineArticle {
  String pubmedId;

  LocalDate creationDate;
  LocalDate completionDate;
  LocalDate revisionDate;

  String journalTitle;
  String articleTitle;

  String abstractText;
  Map<String, String> abstractTexts;

  List<String> publicationTypes;
  List<String> chemicals;
  List<String> meshTerms;

  public static MedlineArticle sparse(final String id,
      final String articleTitle,
      final String journalTitle,
      final LocalDate creationDate,
      final String abstractText) {
    final SimpleMedlineArticle article = new SimpleMedlineArticle();
    article.pubmedId = id;

    article.creationDate = creationDate;
    article.completionDate = null;
    article.revisionDate = null;


    article.journalTitle = journalTitle;
    article.articleTitle = articleTitle;
    article.abstractText = abstractText;
    article.abstractTexts = Collections.emptyMap();


    article.publicationTypes = Collections.emptyList();
    article.chemicals = Collections.emptyList();
    article.meshTerms = Collections.emptyList();

    return article;
  }

  public static MedlineArticle fromLucene(Document luceneDocument) {
    final SimpleMedlineArticle article = new SimpleMedlineArticle();
    article.pubmedId = luceneDocument.get("pmid");

    article.creationDate = LuceneUtils.getLocalDateField(luceneDocument, "creation_date");
    article.completionDate = LuceneUtils.getLocalDateField(luceneDocument, "completion_date");
    article.revisionDate = LuceneUtils.getLocalDateField(luceneDocument, "revision_date");

    article.journalTitle = luceneDocument.get("journal_title");
    article.articleTitle = luceneDocument.get("article_title");

    article.abstractText = luceneDocument.get("text");
    article.abstractTexts = luceneDocument.getFields().stream()
        .filter(f -> f.name().startsWith("text_"))
        .collect(Collectors.toMap(
            field -> field.name().substring(field.name().indexOf('_')),
            IndexableField::stringValue,
            (text1, text2) -> text1 + '\n' + text2));

    article.publicationTypes = LuceneUtils.getListField(luceneDocument, "article_types");
    article.chemicals = LuceneUtils.getListField(luceneDocument, "chemicals");
    article.meshTerms = LuceneUtils.getListField(luceneDocument, "mesh_terms");

    return article;
  }

  @Override
  public String getPubmedId() {
    return pubmedId;
  }

  @Override
  public LocalDate getCreationDate() {
    return creationDate;
  }

  @Override
  public LocalDate getCompletionDate() {
    return completionDate;
  }

  @Override
  public LocalDate getRevisionDate() {
    return revisionDate;
  }

  @Override
  public String getJournalTitle() {
    return journalTitle;
  }

  @Override
  public String getArticleTitle() {
    return articleTitle;
  }

  @Override
  public String getAbstractText() {
    return abstractText;
  }

  @Override
  public Map<String, String> getAbstractTexts() {
    return abstractTexts;
  }

  @Override
  public List<String> getPublicationTypes() {
    return publicationTypes;
  }

  @Override
  public List<String> getChemicals() {
    return chemicals;
  }

  @Override
  public List<String> getMeshTerms() {
    return meshTerms;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MedlineArticle that = (MedlineArticle) o;
    return Objects.equals(pubmedId, that.getPubmedId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(pubmedId);
  }
}
