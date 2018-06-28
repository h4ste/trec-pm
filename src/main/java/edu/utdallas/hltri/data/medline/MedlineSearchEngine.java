package edu.utdallas.hltri.data.medline;

import edu.utdallas.hltri.data.medline.basic.SimpleMedlineArticle;
import edu.utdallas.hltri.data.medline.jaxb.EagerJaxbMedlineArticle;
import edu.utdallas.hltri.data.medline.jaxb.JaxbMedlineSearchEngine;
import edu.utdallas.hltri.data.medline.jaxb.LazyJaxbMedlineArticle;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.search.similarities.BM25Similarity;

import edu.utdallas.hltri.data.medline.jaxb.JaxbMedlineIndexer;
import edu.utdallas.hltri.data.medline.jaxb.JaxbMedlineArticle;
import edu.utdallas.hltri.inquire.lucene.DocumentFactory;
import edu.utdallas.hltri.inquire.lucene.LuceneSearchEngine;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;

/**
 * Created by travis on 6/19/17.
 */
@SuppressWarnings("WeakerAccess")
public class MedlineSearchEngine<A extends MedlineArticle> extends LuceneSearchEngine<A> {

  public static final String PMID_FIELD = "pmid";

  public static final String ABSTRACT_TEXT_FIELD = "text";

  public static final String ARTICLE_TITLE_FIELD = "article_title";
  public static final String JOURNAL_TITLE_FIELD = "journal_title";

  public static final String ARTICLE_TYPES_FIELD = "article_types";
  public static final String MESH_TERMS_FIELD = "mesh_terms";
  public static final String CHEMICALS_FIELD = "chemicals";

  public static final String CREATION_DATE_FIELD = "creation_date";
  public static final String COMPLETION_DATE_FIELD = "completion_date";
  public static final String REVISION_DATE_FIELD = "revision_date";



  /**
   * Create a new LuceneSearchEngine
   *
   * @param medlineIndexPath (file) path to Lucene index
   * @param defaultFieldName default search field (e.g. TEXT)
   */
  public MedlineSearchEngine(String medlineIndexPath, Analyzer analyzer, String defaultFieldName, DocumentFactory<? extends A> factory) {
    super(medlineIndexPath,
        analyzer,
        defaultFieldName,
        factory,
        new BM25Similarity(1.2f, .1f));
  }

  public MedlineSearchEngine(Directory dir,
      Analyzer analyzer, String defaultField,
      DocumentFactory<? extends A> factory) {
    super(dir, analyzer, defaultField, factory, new BM25Similarity(1.2f, .1f));
  }

  public static MedlineSearchEngine<MedlineArticle> getDefault() {
    return new MedlineSearchEngine<>(
        MedlineSettings.DEFAULT.indexPath,
        new EnglishAnalyzer(),
        MedlineSettings.DEFAULT.defaultField,
        DocumentFactory.eager(SimpleMedlineArticle::fromLucene));
  }

  public static JaxbMedlineSearchEngine getJaxbEager() {
    return new JaxbMedlineSearchEngine(
        MedlineSettings.LAZY.indexPath,
        MedlineSettings.LAZY.defaultField,
        EagerJaxbMedlineArticle::new);
  }

  public static JaxbMedlineSearchEngine getJaxbLazy() {
    return new JaxbMedlineSearchEngine(
        MedlineSettings.LAZY.indexPath,
        MedlineSettings.LAZY.defaultField,
        LazyJaxbMedlineArticle::new);
  }
}
