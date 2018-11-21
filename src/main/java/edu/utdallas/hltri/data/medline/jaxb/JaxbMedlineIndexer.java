package edu.utdallas.hltri.data.medline.jaxb;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.MoreExecutors;
import edu.utdallas.hltri.concurrent.BoundedThreadPool;
import edu.utdallas.hltri.data.medline.jaxb.struct.Abstract;
import edu.utdallas.hltri.data.medline.jaxb.struct.AffiliationInfo;
import edu.utdallas.hltri.data.medline.jaxb.struct.Article;
import edu.utdallas.hltri.data.medline.jaxb.struct.ArticleTitle;
import edu.utdallas.hltri.data.medline.jaxb.struct.Author;
import edu.utdallas.hltri.data.medline.jaxb.struct.AuthorList;
import edu.utdallas.hltri.data.medline.jaxb.struct.B;
import edu.utdallas.hltri.data.medline.jaxb.struct.DataBankList;
import edu.utdallas.hltri.data.medline.jaxb.struct.DescriptorName;
import edu.utdallas.hltri.data.medline.jaxb.struct.Grant;
import edu.utdallas.hltri.data.medline.jaxb.struct.GrantList;
import edu.utdallas.hltri.data.medline.jaxb.struct.I;
import edu.utdallas.hltri.data.medline.jaxb.struct.Investigator;
import edu.utdallas.hltri.data.medline.jaxb.struct.InvestigatorList;
import edu.utdallas.hltri.data.medline.jaxb.struct.MedlineCitation;
import edu.utdallas.hltri.data.medline.jaxb.struct.MedlineJournalInfo;
import edu.utdallas.hltri.data.medline.jaxb.struct.MeshHeading;
import edu.utdallas.hltri.data.medline.jaxb.struct.MeshHeadingList;
import edu.utdallas.hltri.data.medline.jaxb.struct.PublicationType;
import edu.utdallas.hltri.data.medline.jaxb.struct.PublicationTypes;
import edu.utdallas.hltri.data.medline.jaxb.struct.PubmedArticle;
import edu.utdallas.hltri.data.medline.jaxb.struct.QualifierName;
import edu.utdallas.hltri.data.medline.jaxb.struct.Sub;
import edu.utdallas.hltri.data.medline.jaxb.struct.Sup;
import edu.utdallas.hltri.data.medline.jaxb.struct.U;
import edu.utdallas.hltri.framework.ProgressLogger;
import edu.utdallas.hltri.io.AC;
import edu.utdallas.hltri.logging.Logger;
import edu.utdallas.hltri.util.Unsafe;
import io.protostuff.GraphIOUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.BytesRef;

/**
 * Asynchronous Lucene indexer relying on JAXB Parsing;
 * can be invoked to do the indexing process on the command line via its main method
 * Assumes gzipped XML articles.
 * Created by travis on 6/6/2017.
 */
@SuppressWarnings("WeakerAccess")
public class JaxbMedlineIndexer implements AC {
  private static final Logger log =
      Logger.get(JaxbMedlineIndexer.class);

  private final IndexWriter indexWriter;

  private final ExecutorService indexingService = BoundedThreadPool.create(512, "indexing");

  private final Schema<PubmedArticle> schema = RuntimeSchema.getSchema(PubmedArticle.class);

  static Stream<String> flattenFormatting(Stream<Serializable> content) {
    return content.flatMap(s -> {
      if (s instanceof I) {
        return flattenFormatting(((I) s).getContent().stream());
      } else if (s instanceof Sup) {
        return flattenFormatting(((Sup) s).getContent().stream());
      } else if (s instanceof U) {
        return flattenFormatting(((U) s).getContent().stream());
      } else if (s instanceof Sub) {
        return flattenFormatting(((Sub) s).getContent().stream());
      } else if (s instanceof String) {
        return Stream.of((String) s);
      } else if (s instanceof B) {
        return flattenFormatting(((B) s).getContent().stream());
      } else {
        throw new IllegalStateException("Encountered unexpected content of type " +
            s.getClass().getSimpleName());
      }
    });
  }

  static String flattenContent(Collection<Serializable> content) {
    return flattenFormatting(content.stream()).collect(Collectors.joining());
  }
  
  private final static Analyzer analyzer = new PerFieldAnalyzerWrapper(
      new EnglishAnalyzer(),
      Stream.of(
          "authors", "authors_initials", "author_affiliations",
          "investigators", "investigators_initials", "investigator_affiliations",
          "journal")
          .collect(Collectors.toMap(k -> k, v -> new WhitespaceAnalyzer())
      )
  );

  public static Analyzer getAnalyzer() {
    return analyzer;
  }

  public JaxbMedlineIndexer(final Path indexPath) {
    try {
      final Directory indexDir = new NIOFSDirectory(indexPath);
      final IndexWriterConfig config = new IndexWriterConfig(getAnalyzer())
          .setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND)
          .setUseCompoundFile(false)
          .setRAMBufferSizeMB(4000)
          .setSimilarity(new BM25Similarity());
      this.indexWriter = new IndexWriter(indexDir, config);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public CompletableFuture<Boolean> indexArticleAsync(final PubmedArticle pubmedArticle) {
    return CompletableFuture.supplyAsync(() -> indexArticle(pubmedArticle), indexingService);
  }

  public boolean indexArticle(final PubmedArticle pubmedArticle) {
    final MedlineCitation citation = pubmedArticle.getMedlineCitation();
    Preconditions.checkNotNull(citation, "MEDLINE citation cannot be null");

    final String pmid = citation.getPMID().getValue();
    Preconditions.checkNotNull(pmid, "pmid cannot be null");


    final Set<String> citationSubsets = citation.getCitationSubsets();
    // Filter out articles that aren't in desired citation subsets
    if (citationSubsets == null ||
        // We don't want old articles
        citationSubsets.contains("OM") ||
        // We want citations from Abridged Index Medicus journals,
        // a list of about 120 core clinical, English language journals.
        (!citationSubsets.contains("AIM") &&
        // We also want citations from any other Index Medicus journals
        !citationSubsets.contains("IM"))) {
      log.debug("Skipping {} due to Citation Subsets: {}", pmid, citationSubsets);
      return false;
    }

    final Article article = citation.getArticle();
    if (// Skip articles that don't have an article element (e.g., comments)
        article == null) {
      log.debug("Skipping {} due to NULL article", pmid);
      return false;
    }
    if (article.getLanguages() == null ||
        // We are only interested in English articles
        !article.getLanguages().contains("eng")) {
      log.debug("Skipping {} due to languages: {}", pmid, article.getLanguages());
      return false;
    }

    final Abstract articleAbstract = article.getAbstract();
    if (articleAbstract == null) {
      log.debug("Skipping {} due to missing abstract", pmid);
      return false;
    }

    final Document document = new Document();
    document.add(new StringField("pmid", pmid, Store.YES));

    final String abstractText =
        articleAbstract.getAbstractTexts().stream()
        .flatMap(at -> flattenFormatting(at.getContent().stream()))
        .collect(Collectors.joining());
    document.add(new TextField("abstract", abstractText, Store.NO));

    final ArticleTitle articleTitle = article.getArticleTitle();
    Preconditions.checkNotNull(articleTitle);
    final String title = flattenContent(articleTitle.getContent());
    document.add(new TextField("title", title, Store.NO));

    final PublicationTypes articlePublicationTypes = article.getPublicationTypes();
    if (articlePublicationTypes != null ) {
      for (PublicationType publicationType : articlePublicationTypes.getPublicationTypes()) {
        document.add(new StringField("publication_types", publicationType.getValue(), Store.NO));
      }
    }

    final DataBankList dataBankList = article.getDataBankList();
    if (dataBankList != null ) {
      dataBankList.getDataBanks().stream()
          .filter(db -> db.getDataBankName().equals("ClinicalTrials.gov"))
          .flatMap(db -> db.getAccessionNumberList().getAccessionNumbers().stream())
          .forEach(nctId ->
              document.add(new StringField("registered_trials", nctId, Store.NO))
          );
    }

    final GrantList grantList = article.getGrantList();
    if (grantList != null) {
      for (Grant grant : grantList.getGrants()) {
        final String grantId = grant.getGrantID();
        if (grantId != null) {
          document.add(new StringField("grants", grant.getGrantID(), Store.NO));
        }
      }
    }

    final AuthorList authorList = article.getAuthorList();
    if (authorList != null) {
      for (Author a : article.getAuthorList().getAuthors()) {
        if (a.getValidYN().equals("Y")) {
          document.add(new TextField("authors",
              a.getForeName() + ' ' + a.getLastName(), Store.NO));
          document.add(new TextField("authors_initials",
              a.getInitials() + ' ' + a.getLastName(), Store.NO));
        }
        for (AffiliationInfo ai : a.getAffiliationInfos()) {
          document.add(new TextField("author_affiliations",
              flattenFormatting(ai.getAffiliation().getContent().stream())
                  .collect(Collectors.joining()),
              Store.NO));
        }
      }
    }

    final InvestigatorList investigatorList = citation.getInvestigatorList();
    if (investigatorList != null) {
      for (Investigator i : citation.getInvestigatorList().getInvestigators()) {
        if (i.getValidYN().equals("Y")) {
          document.add(new TextField("investigators",
              i.getForeName() + ' ' + i.getLastName(), Store.NO));
          document.add(new TextField("investigators_initials",
              i.getInitials() + ' ' + i.getLastName(), Store.NO));
        }
        for (AffiliationInfo ai : i.getAffiliationInfos()) {
          document.add(new TextField("investigator_affiliations",
              flattenContent(ai.getAffiliation().getContent()), Store.NO));
        }
      }
    }

    final MedlineJournalInfo medlineJournalInfo = citation.getMedlineJournalInfo();
    if (medlineJournalInfo != null) {
      document.add(new TextField("journal", medlineJournalInfo.getMedlineTA(), Store.NO));
    }

    {
      final String creationYear = citation.getDateCreated().getYear();
      final String creationMonth = citation.getDateCreated().getMonth();
      final String creationDay = citation.getDateCreated().getDay();
      final long date = LocalDate.parse(creationYear + creationMonth + creationDay,
          DateTimeFormatter.BASIC_ISO_DATE).toEpochDay();
      document.add(new LongPoint("date", date));
    }


    final MeshHeadingList meshHeadingList = citation.getMeshHeadingList();
    if (meshHeadingList != null) {
      for (MeshHeading meshHeading : meshHeadingList.getMeshHeadings()) {
        final DescriptorName descriptor = meshHeading.getDescriptorName();
        if (descriptor.getMajorTopicYN().equals("Y")) {
          document.add(new TextField("mesh_headings", descriptor.getValue(), Store.NO));
        } else {
          for (QualifierName qualifierName : meshHeading.getQualifierNames()) {
            if (qualifierName.getMajorTopicYN().equals("Y")) {
              document.add(new TextField("mesh_headings",
                  descriptor.getValue() + " > " + qualifierName.getValue(), Store.NO));
            }
          }
        }
      }
    }

    final LinkedBuffer buffer = LinkedBuffer.allocate();

    try {
      document.add(
          new BinaryDocValuesField(
              "bytes",
              new BytesRef(GraphIOUtil.toByteArray(pubmedArticle, schema, buffer))));
    } finally {
      buffer.clear();
    }

    try {
        indexWriter.updateDocument(new Term("pmid", pmid), document);
        return true;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() {
    MoreExecutors.shutdownAndAwaitTermination(this.indexingService, 1, TimeUnit.HOURS);

    // Close the index
    try {
      this.indexWriter.flush();
      this.indexWriter.forceMerge(4);

      this.indexWriter.commit();
      this.indexWriter.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  static <T> CompletableFuture<?> mergeFutures(List<CompletableFuture<T>> futuresList) {
    return CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[0]));
  }

  public static void main(String... args) throws IOException {
    final Path indexDir = Paths.get(args[0]);
    final boolean replaceIndex = args[1].equals("--replace");

    if (replaceIndex && Files.isDirectory(indexDir)) {
      log.info("Deleting existing index at {}", indexDir);
      //noinspection ResultOfMethodCallIgnored
      Files.walk(indexDir, FileVisitOption.FOLLOW_LINKS)
          .sorted(Comparator.reverseOrder())
          .map(Path::toFile)
          .peek(file -> log.debug("deleting {}", file))
          .forEach(File::delete);
    }

    log.info("Enumerating files to index...");
    final List<Path> files = Arrays.stream(args)
        .skip(replaceIndex ? 2 : 1)
        .map(Paths::get)
        .peek(d -> log.debug("Traversing directory {}", d.toString()))
        .flatMap(Unsafe.function(d -> Files.walk(d, FileVisitOption.FOLLOW_LINKS)))
        .filter(f -> f.getFileName().toString().endsWith(".xml.gz"))
        .collect(Collectors.toList());

    log.info("Configuring parser...");
    final JaxbMedlineParser parser = new JaxbMedlineParser();

    log.info("Indexing {} files...", files.size());
    try (ProgressLogger plog = ProgressLogger.fixedSize("indexing", files.size(),
        1, TimeUnit.MINUTES);
        JaxbMedlineIndexer indexer = new JaxbMedlineIndexer(indexDir)) {
      for (final Path file : files) {
        final AtomicInteger numIndexed = new AtomicInteger(0);
        final AtomicInteger numSkipped = new AtomicInteger(0);
        final AtomicInteger numFailed = new AtomicInteger(0);
        try (final InputStream fileStream = Files.newInputStream(file);
             final GZIPInputStream decompressor = new GZIPInputStream(fileStream)) {
          final List<CompletableFuture<Boolean>> articleFutures = parser.streamArticles(decompressor)
              .map(a -> indexer.indexArticleAsync(a)
                  .whenCompleteAsync((b, t) -> {
                if (b == null || t != null) {
                  numFailed.incrementAndGet();
                  log.debug("Parsing failure", t);
                  return;
                }

                if (b) {
                  numIndexed.incrementAndGet();
                } else {
                  numSkipped.incrementAndGet();
                }

              })
              ).collect(Collectors.toList());

          mergeFutures(articleFutures)
              .thenRun(() -> {
                final int failed = numFailed.get();
                if (failed == 0) {
                  plog.update("{} indexed ({} skipped{}) from {}",
                      numIndexed.get(), numSkipped.get(), file.getFileName());
                } else {
                  plog.update("{} indexed ({} skipped{}; {} failed) from {}",
                      numIndexed.get(), numSkipped.get(), failed, file.getFileName());
                }
              })
              .join();
        }
      }
    }
  }
}

