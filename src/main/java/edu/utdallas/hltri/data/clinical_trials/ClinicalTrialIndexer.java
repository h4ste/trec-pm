package edu.utdallas.hltri.data.clinical_trials;

import static edu.utdallas.hltri.inquire.lucene.LuceneSearchEngine.TEXT_FIELD_TYPE;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import edu.utdallas.hltri.data.clinical_trials.jaxb.BrowseStruct;
import edu.utdallas.hltri.data.clinical_trials.jaxb.ClinicalStudy;
import edu.utdallas.hltri.data.clinical_trials.jaxb.EligibilityStruct;
import edu.utdallas.hltri.data.clinical_trials.jaxb.InterventionStruct;
import edu.utdallas.hltri.data.clinical_trials.jaxb.ReferenceStruct;
import edu.utdallas.hltri.logging.Logger;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.BytesRef;

public class ClinicalTrialIndexer implements  AutoCloseable {
  private static final Logger log = Logger.get(ClinicalTrialIndexer.class);

  private final IndexWriter indexWriter;
  private final boolean replace;

  private static final Pattern ageRegex =
      Pattern.compile("([0-9]+) (Years?|Months?|Days?|Weeks?|Hours?|Minutes?|Seconds?)",
        Pattern.CASE_INSENSITIVE);

  private final Splitter splitter = Splitter.on(CharMatcher.anyOf("\r\n"))
          .omitEmptyStrings()
          .trimResults();

  private final EligibilityCriteriaParser eligibilityCriteriaParser =
      new EligibilityCriteriaParser();

  private final Pattern inclusionCriteriaHeading =
      Pattern.compile("^.{0,14}I[nN][cC][lL][uU][sS][iI][oO][nN]\\s+"
          + "[cC][rR][iI][tT][eE][rR][iI][aA].{0,14}:?\\s*$");

  private final Pattern exclusionCriteriaHeading =
      Pattern.compile("^.{0,14}E[xX][cC][lL][uU][sS][iI][oO][nN]\\s+"
          + "[cC][rR][iI][tT][eE][rR][iI][aA].{0,14}:?\\s*$");

  private final ExecutorService executor = Executors.newSingleThreadExecutor();

  private final boolean doNegation;


  public ClinicalTrialIndexer(final Path indexPath, boolean create, boolean doNegation) {
    try {
      final Directory indexDir = new NIOFSDirectory(indexPath);
      final Analyzer analyzer = new EnglishAnalyzer();
      final IndexWriterConfig config = new IndexWriterConfig(analyzer)
          .setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND)
          .setSimilarity(new BM25Similarity());
      this.indexWriter = new IndexWriter(indexDir, config);
      this.replace = create;
      this.doNegation = doNegation;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void addNullableField(@Nonnull Document document,
                                @Nonnull String field,
                                @Nullable String value,
                                @Nonnull FieldType type) {
    if (value != null) {
      document.add(new Field(field, value, type));
      if (type == TEXT_FIELD_TYPE) {
        document.add(new Field("text", value, type));
      }
    }
  }

  private void addNullableFields(@Nonnull Document document,
                                 @Nonnull String field,
                                 @Nullable Iterable<String> values,
                                 @Nonnull FieldType type) {
    if (values != null) {
      for (String value : values) {
        if (value != null) {
          document.add(new Field(field, value, type));
          if (type == TEXT_FIELD_TYPE) {
            document.add(new Field("text", value, type));
          }
        }
      }
    }
  }

  public static Optional<Integer> parseAge(String ageString) {
    if (ageString != null) {
      if (!ageString.equals("N/A")) {
        final Matcher matcher = ageRegex.matcher(ageString);
        final boolean matches = matcher.matches();
        assert  matches : "age |" + ageString + "| was malformed!";
        int age = Integer.valueOf(matcher.group(1));
        if (matcher.group(2).equalsIgnoreCase("months")) {
          age /= 12;
        } else if (matcher.group(2).equalsIgnoreCase("days")) {
          age /= 365;
        } else if (matcher.group(2).equalsIgnoreCase("weeks")) {
          age /= 52;
        } else if (matcher.group(2).equalsIgnoreCase("hours")) {
          age /= 365 * 24;
        } else if (matcher.group(2).equalsIgnoreCase("minutes")) {
          age /= 365 * 24 * 60;
        } else if (matcher.group(2).equalsIgnoreCase("seconds")) {
          age /= 365 * 24 * 60 * 60;
        }
        return Optional.of(age);
      }
    }
    return Optional.empty();
  }

  private void addAgeField(@Nonnull Document document,
                           @Nonnull String field,
                           @Nullable String ageString) {
    parseAge(ageString).ifPresent(age -> document.add(new IntPoint(field, age)));
  }

  private void indexEligibilityCriteria(Document doc, String criteria) {
    // Skip empty criteria
    if (Strings.isNullOrEmpty(criteria)) {
      return;
    }
    doc.add(new Field("eligibility_criteria", criteria, TextField.TYPE_NOT_STORED));

    final List<String> lines = splitter.splitToList(criteria);

    boolean foundInclusionHeader = false;
    boolean foundExclusionHeader = false;
    int inclusionStart = 0;
    int inclusionEnd = lines.size();
    int exclusionStart = lines.size();
    int exclusionEnd = lines.size();
    for (int i = 0; i < lines.size(); i++) {
      final String line = lines.get(i);
      final Matcher inclusionMatcher = inclusionCriteriaHeading.matcher(line);
      final Matcher exclusionMatcher = exclusionCriteriaHeading.matcher(line);
      final boolean exclusionHeading = exclusionMatcher.matches();
      if (inclusionMatcher.matches()) {
        // Ensure this is the first inclusion header
        if (!foundInclusionHeader) {
          assert !exclusionHeading : "Found inclusion and exclusion heading on same line: "
              + inclusionMatcher.group() + " & " + exclusionMatcher.group();
          // Start the inclusion section here!
          inclusionStart = i;
          foundInclusionHeader = true;
        } else {
          log.warn("Found repeated inclusion criteria headings for {}", doc.get("nctid"));
        }

        // End the exclusion criteria span if needed
        if (foundExclusionHeader && exclusionEnd == lines.size()) {
          exclusionEnd = i;
        }
      } else if (exclusionHeading) {
        // Ensure this is the first exclusion criteria header
        if (!foundExclusionHeader) {
          exclusionStart = i;
          foundExclusionHeader = true;
        } else {
          log.warn("Found multiple exclusion criteria headings for {}", doc.get("nctid"));
        }

        // End inclusion span if needed
        if (foundInclusionHeader && inclusionEnd == lines.size()) {
          inclusionEnd = i;
        }
      }
    }

    assert inclusionStart <= inclusionEnd : "Malformed inclusion criteria [start: " + inclusionStart
        + " & end: " + inclusionEnd + "] for " + doc.get("nctid");
    assert exclusionStart <= exclusionEnd : "Malformed exclusion criteria [start: " + exclusionStart
        + " & end: " + exclusionEnd + "] for " + doc.get("nctid");

    final String inclusionCriteria = lines.subList(inclusionStart, inclusionEnd).stream()
        .collect(Collectors.joining("\n"));
    if (!inclusionCriteria.isEmpty()) {
      if (doNegation) {
        final Future<?> result = executor.submit(() ->
            eligibilityCriteriaParser.parseCriteria(inclusionCriteria,
                (positives, negatives) -> {
                  addNullableFields(doc, "inclusion_criteria", positives, TextField.TYPE_STORED);
                  addNullableFields(doc, "text", positives, TextField.TYPE_NOT_STORED);
                  addNullableFields(doc, "exclusion_criteria", negatives, TextField.TYPE_STORED);
                }
            )
        );
        try {
          result.get(5, TimeUnit.MINUTES);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
          log.error("Error annotating inclusion criteria in document {}", doc.get("nctid"), e);
        }
      } else {
        addNullableField(doc, "inclusion_criteria", inclusionCriteria, TextField.TYPE_STORED);
        addNullableField(doc, "text", inclusionCriteria, TextField.TYPE_NOT_STORED);
      }
    }
    final String exclusionCriteria = lines.subList(exclusionStart, exclusionEnd).stream()
        .collect(Collectors.joining("\n"));
    if (!exclusionCriteria.isEmpty()) {
      if (doNegation) {
        final Future<?> result = executor.submit(() ->
            eligibilityCriteriaParser.parseCriteria(exclusionCriteria,
                (positives, negatives) -> {
                  addNullableFields(doc, "exclusion_criteria", positives, TextField.TYPE_STORED);
                  addNullableFields(doc, "inclusion_criteria", negatives, TextField.TYPE_STORED);
                  addNullableFields(doc, "text", negatives, TextField.TYPE_NOT_STORED);
                }
            )
        );
        try {
          result.get(5, TimeUnit.MINUTES);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
          log.error("Error annotating exclusion criteria in {}", doc.get("nctid"), e);
        }
      } else {
        addNullableField(doc, "exclusion_criteria", exclusionCriteria, TextField.TYPE_STORED);
      }
    }
  }

  public void indexClinicalStudy(ClinicalStudy study) {
    final String nctId = study.getIdInfo().getNctId();
    assert !Strings.isNullOrEmpty(nctId) : "blank NCTID";

    log.trace("Indexing trial {}", nctId);
    final Document luceneDoc = new Document();


    // Raw (non-processed) textual fields
    luceneDoc.add(new Field("nctid", nctId, StringField.TYPE_STORED));

    addNullableField(luceneDoc, "brief_title", study.getBriefTitle(), TextField.TYPE_NOT_STORED);
    addNullableField(luceneDoc, "official_title", study.getOfficialTitle(), TextField.TYPE_NOT_STORED);

    if (study.getBriefSummary() != null) {
      addNullableField(luceneDoc, "summary",
          study.getBriefSummary().getTextblock(), TextField.TYPE_NOT_STORED);
    }
    if (study.getDetailedDescription() != null) {
      addNullableField(luceneDoc, "description",
          study.getDetailedDescription().getTextblock(), TextField.TYPE_NOT_STORED);
    }

    luceneDoc.add(new Field("phase",
        study.getPhase().value(), StringField.TYPE_NOT_STORED));
    luceneDoc.add(new Field("study_type",
        study.getStudyType().value(), StringField.TYPE_NOT_STORED));

    if (study.getStudyDesignInfo() != null) {
      addNullableField(luceneDoc, "masking",
          study.getStudyDesignInfo().getMasking(), StringField.TYPE_NOT_STORED);
    }

    addNullableFields(luceneDoc, "conditions", study.getCondition(), TextField.TYPE_NOT_STORED);

    final List<InterventionStruct> interventionStructs = study.getIntervention();
    if (interventionStructs != null ) {
      for (final InterventionStruct interventionStruct : interventionStructs) {
        luceneDoc.add(new Field("intervention_names",
            interventionStruct.getInterventionName(),
            TextField.TYPE_NOT_STORED));
        luceneDoc.add(new Field("text",
            interventionStruct.getInterventionName(),
            TextField.TYPE_NOT_STORED));

        addNullableFields(luceneDoc, "intervention_names",
            interventionStruct.getOtherName(), TextField.TYPE_NOT_STORED);
        addNullableFields(luceneDoc, "text",
            interventionStruct.getOtherName(),
            TextField.TYPE_NOT_STORED);
        addNullableField(luceneDoc, "intervention_types",
            interventionStruct.getInterventionType().toString(), StringField.TYPE_STORED);
      }
    }

    {
      final BrowseStruct meshStruct = study.getConditionBrowse();
      if (meshStruct != null) {
        addNullableFields(luceneDoc, "condition_mesh_terms", meshStruct.getMeshTerm(),
            TextField.TYPE_NOT_STORED);
      }
    }
    {
      final BrowseStruct meshStruct = study.getInterventionBrowse();
      if (meshStruct != null) {
        addNullableFields(luceneDoc, "intervention_mesh_terms", meshStruct.getMeshTerm(),
            TextField.TYPE_NOT_STORED);
      }
    }


    addNullableFields(luceneDoc, "keywords", study.getKeyword(), TextField.TYPE_NOT_STORED);

    List<String> list = new ArrayList<>();
    for (ReferenceStruct referenceStruct : study.getReference()) {
      if (referenceStruct != null) {
        final BigInteger pmid = referenceStruct.getPMID();
        if (pmid != null) {
          final String s = Objects.toString(pmid);
          list.add(s);
        }
      }
    }
    addNullableFields(luceneDoc, "result_pmids",
        list,
        StringField.TYPE_NOT_STORED);

    final EligibilityStruct eligibility = study.getEligibility();
    if (eligibility != null) {
      if (eligibility.getCriteria() != null) {
        indexEligibilityCriteria(luceneDoc, eligibility.getCriteria().getTextblock());
      }
      addAgeField(luceneDoc, "eligibility_min_age", eligibility.getMinimumAge());
      addAgeField(luceneDoc, "eligibility_max_age", eligibility.getMaximumAge());
      luceneDoc.add(new Field("eligibility_gender",
          eligibility.getGender().name(), StringField.TYPE_STORED));
    }

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      try (ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(baos))) {
        oos.writeObject(study);
      }
      luceneDoc.add(new BinaryDocValuesField("bytes", new BytesRef(baos.toByteArray())));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    try {
      if (!replace) {
        indexWriter.updateDocument(new Term("nctid", nctId), luceneDoc);
      } else {
        indexWriter.addDocument(luceneDoc);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() throws IOException {
      this.indexWriter.flush();
      this.indexWriter.commit();
      this.indexWriter.close();
  }
}
