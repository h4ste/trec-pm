package edu.utdallas.hltri.data.medline.basic;

import edu.utdallas.hltri.data.medline.MedlineArticle;
import org.xml.sax.Attributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import edu.utdallas.hltri.io.ExtendedDefaultHandler;
import edu.utdallas.hltri.logging.Logger;

/**
 * DefaultHandler used by the SAX Parsing interface
 * This class is responsible for producing MedlineArticle objects as it processes the XML
 * The constructor requires a MedlineArticle consumer which is called to consume/process
 * Each parsed MedlineArticle object
 * Created by travis on 6/6/2017.
 */
public final class MedlineXmlHandler extends ExtendedDefaultHandler {
  static private final Logger log = Logger.get(MedlineXmlHandler.class);

  // Constants for XML tags we care about
  private static final String ARTICLE = "PubmedArticle";

  private static final String DATE_CREATED = "DateCreated";
  private static final String DATE_COMPLETED = "DateCompleted";
  private static final String DATE_REVISED = "DateRevised";

  private static final String ABSTRACT_TEXT = "AbstractText";
  private static final String ABSTRACT_SECTION_NLMCATEGORY = "NlmCategory";
  private static final String ABSTRACT_SECTION_LABEL = "Label";

  private static final String PMID = "PMID";
  private static final String YEAR = "Year";
  private static final String MONTH = "Month";
  private static final String DAY = "Day";
  private static final String JOURNAL_TITLE = "Title";
  private static final String ARTICLE_TITLE = "ArticleTitle";

  // MeSH terms
  private static final String MESH_HEADING_LIST = "MeshHeadingList";
  private static final String MESH_HEADING = "MeshHeading";
  private static final String MESH_HEADING_DESCRIPTOR = "DescriptorName";
  private static final String MESH_HEADING_QUALIFIER = "QualifierName";

  // Chemicals & Substances
  private static final String CHEMICAL_LIST = "ChemicalList";
  private static final String CHEMICAL_NAME = "NameOfSubstance";

  private static final String PUBLICATION_TYPE = "PublicationType";
  private static final String PUBLICATION_TYPE_LIST = "PublicationTypeList";

  // This AbstractText section is automatically added by NLM, and we don't care about it
  private static final String DATA_AVAILABILITY = "DATA AVAILABILITY";

  // Intermediate state for currently-being-parsed medline article
  private final MedlineArticleState state = new MedlineArticleState();

  // Currently-being-parsed medline article
  private SimpleMedlineArticle article;

  // Whether to save the characters (text) for the current tag
  private boolean saveCharacters = false;

  // What to do with the final (parsed) medline articles
  private final Consumer<MedlineArticle> indexer;

  MedlineXmlHandler(Consumer<MedlineArticle> indexer) {
    this.indexer = indexer;
  }

  @Override
  public void start(String qName, Attributes attributes) {
    switch (qName) {
      case ARTICLE:
        log.trace("Creating new article");
        state.reset();
        article = new SimpleMedlineArticle();
        break;

      case DATE_CREATED:
      case DATE_COMPLETED:
      case DATE_REVISED:
        log.trace("Setting date context to |{}|", qName);
        state.dateContext = qName;
        state.resetDate();
        break;

      case ABSTRACT_TEXT:
        final String sectionTitle = attributes.getValue(ABSTRACT_SECTION_NLMCATEGORY);
        final String sectionLabel = attributes.getValue(ABSTRACT_SECTION_LABEL);
        log.trace("Setting abstract section title to |{}| (label: |{}|)", sectionTitle, sectionLabel);
        state.sectionNlmCategory = sectionTitle;
        state.sectionLabel = sectionLabel;
        saveCharacters = true;
        break;

      case MESH_HEADING_LIST:
        assert article.meshTerms == null : "Found multiple MeSH lists";
        article.meshTerms = new ArrayList<>(16);
        break;

      case CHEMICAL_LIST:
        assert article.chemicals == null : "Found multiple chemical lists";
        article.chemicals = new ArrayList<>(16);
        break;

      case PUBLICATION_TYPE_LIST:
        assert article.publicationTypes == null : "Found multiple publication type lists";
        article.publicationTypes = new ArrayList<>(2);
        break;

      case MESH_HEADING:
        state.meshTerm.setLength(0);
        break;

      // Remaining tags we want to save characters from
      case MESH_HEADING_DESCRIPTOR:
      case MESH_HEADING_QUALIFIER:
      case CHEMICAL_NAME:
      case PUBLICATION_TYPE:
      case PMID:
      case YEAR:
      case MONTH:
      case DAY:
      case JOURNAL_TITLE:
      case ARTICLE_TITLE:
        saveCharacters = true;
        break;

      default:
        log.trace("Not saving |{}|", qName);
        saveCharacters = false;
        break;
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) {
    if (saveCharacters) {
      super.characters(ch, start, length);
    }
  }

  @Override
  public void end(String qName) {
    log.trace("End |{}|");
    switch (qName) {
      case PMID:
        if (stackMatch("PubmedArticleSet", "PubmedArticle", "MedlineCitation", "PMID")) {
          assert article.getPubmedId() == null : "Cannot replace PMID " + article.getPubmedId()
              + " with " + getText();
          article.pubmedId = getText();
          log.trace("Set PMID = {}", article.pubmedId);
        }
        break;

      // We only care about Year, Month, and Day tags used in the date contexts we care about
      case YEAR:
        if (state.dateContext != null) {
          state.year = Integer.parseInt(getCleanText());
        }
        break;

      case MONTH:
        if (state.dateContext != null) {
          state.month = Integer.parseInt(getCleanText());
        }
        break;

      case DAY:
        if (state.dateContext != null) {
          state.day = Integer.parseInt(getCleanText());
        }
        break;

      case DATE_CREATED:
        if (state.year > -1 &&
            state.month > -1 &&
            state.day > -1 && Objects.equals(state.dateContext, qName)) {
          article.creationDate = LocalDate.of(state.year, state.month, state.day);
          log.trace("Set Creation Date = {}", article.creationDate);
          state.dateContext = null;
        } else {
          log.warn("Failed to parse {} of {}/{}/{} with context {}", qName,
              state.year, state.month, state.day, state.dateContext);
        }
        break;

      case DATE_COMPLETED:
        if (state.year > -1 &&
            state.month > -1 &&
            state.day > -1 && Objects.equals(state.dateContext, qName)) {
          article.completionDate = LocalDate.of(state.year, state.month, state.day);
          log.trace("Set Completion Date = {}", article.completionDate);
          state.dateContext = null;
        } else {
          log.warn("Failed to parse {} of {}/{}/{} with context {}", qName,
              state.year, state.month, state.day, state.dateContext);
        }
        break;

      case DATE_REVISED:
        if (state.year > -1 &&
            state.month > -1 &&
            state.day > -1 && Objects.equals(state.dateContext, qName)) {
          article.revisionDate = LocalDate.of(state.year, state.month, state.day);
          log.trace("Set Revision Date = {}", article.revisionDate);
          state.dateContext = null;
        } else {
          log.warn("Failed to parse {} of {}/{}/{} with context {}", qName,
              state.year, state.month, state.day, state.dateContext);
        }
        break;

      case JOURNAL_TITLE:
        if (stackMatch("PubmedArticleSet", "PubmedArticle", "MedlineCitation", "Article", "Journal", "Title")) {
          article.journalTitle = getCleanText();
          log.trace("Set Journal Title = |{}|", article.journalTitle);
        }
        break;

      case ARTICLE_TITLE:
        if (stackMatch("PubmedArticleSet", "PubmedArticle", "MedlineCitation", "Article", "ArticleTitle")) {
          article.articleTitle = getCleanText();
          log.trace("Set Article Title = |{}|", article.articleTitle);
        }
        break;

      case ABSTRACT_TEXT:
        if (state.sectionNlmCategory == null) {
          state.abstractText.append(getCleanText());
        } else if (!state.sectionNlmCategory.equals(DATA_AVAILABILITY)) {
          if (!state.abstractTexts.containsKey(state.sectionNlmCategory)) {
            state.abstractTexts.put(state.sectionNlmCategory, new StringBuilder());
          }
          final StringBuilder abstractSectionText = state.abstractTexts.get(state.sectionNlmCategory);
          if (abstractSectionText.length() > 1) {
            abstractSectionText.append('\n');
          }
          abstractSectionText.append(getCleanText());
          if (state.abstractText.length() > 1) {
            state.abstractText.append('\n');
          }
          state.abstractText.append(getCleanText());
        }
        break;

      case CHEMICAL_NAME:
        article.chemicals.add(getCleanText());
        break;

      case PUBLICATION_TYPE:
        article.publicationTypes.add(getCleanText());
        break;

      case MESH_HEADING_DESCRIPTOR:
        assert state.meshTerm.length() == 0 : "found multiple MeSH descriptors for the same heading";
        state.meshTerm.append(getCleanText());
        article.meshTerms.add(getCleanText());
        break;

      // Add each level of qualification as an additional MeSH term
      case MESH_HEADING_QUALIFIER:
        assert state.meshTerm.length() > 0 : "found MeSH qualifier without descriptor";
        state.meshTerm.append('>').append(getCleanText());
        article.meshTerms.add(getCleanText());
        article.meshTerms.add(state.meshTerm.toString());
        break;

      case ARTICLE:
        article.abstractText = state.abstractText.toString();
        log.trace("Set abstract text to {}-length String", article.abstractText.length());
        article.abstractTexts = state.abstractTexts.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> {
              log.trace("Set abstract text section [{}] to {}-length String",
                  e.getKey(), e.getValue().length());
              return e.getValue().toString();
            }));
        indexer.accept(article);
    }
  }
}
