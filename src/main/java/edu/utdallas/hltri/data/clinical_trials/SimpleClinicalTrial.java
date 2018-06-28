package edu.utdallas.hltri.data.clinical_trials;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

public class SimpleClinicalTrial implements ClinicalTrial {
  private final @Nonnull
  String nctId;                 // required

  private final @Nullable
  String briefTitle;            // required
  private final @Nullable String officialTitle;         // optional

  private final @Nullable String briefSummary;
  private final @Nullable String detailedDescription;   // optional

  private final @Nonnull String phase;                 // required
  private final @Nullable String studyType;             // required
  private final @Nullable String masking;               // required

  private final @Nonnull
  List<String> conditions;      // optional, repeated
  private final @Nonnull List<Intervention> interventions;   // optional, repeated

  private final @Nullable String eligibilityCriteria;
  private final int minAge;
  private final int maxAge;
  private final @Nonnull String gender;

  private final @Nonnull
  Set<String> meshTerms;
  private final @Nonnull Set<String> keywords;         // optional, repeated
  private final @Nonnull Set<String> resultPmids;

  protected SimpleClinicalTrial(@Nonnull String nctId,
      @Nullable String briefTitle,
      @Nullable String officialTitle,
      @Nullable String briefSummary,
      @Nullable String detailedDescription,
      @Nonnull String phase,
      @Nullable String studyType,
      @Nullable String masking,
      @Nonnull List<String> conditions,
      @Nonnull List<Intervention> interventions,
      @Nullable String eligibilityCriteria,
      int minAge,
      int maxAge,
      @Nonnull String gender,
      @Nonnull Iterable<String> meshTerms,
      @Nonnull Iterable<String> keywords,
      @Nonnull Iterable<String> resultPmids) {
    this.nctId = nctId;
    this.briefTitle = briefTitle;
    this.officialTitle = officialTitle;
    this.briefSummary = briefSummary;
    this.detailedDescription = detailedDescription;
    this.phase = phase;
    this.studyType = studyType;
    this.masking = masking;
    this.conditions = conditions;
    this.interventions = interventions;
    this.eligibilityCriteria = eligibilityCriteria;
    this.minAge = minAge;
    this.maxAge = maxAge;
    this.gender = gender;
    this.meshTerms = ImmutableSet.copyOf(meshTerms);
    this.keywords = ImmutableSet.copyOf(keywords);
    this.resultPmids = ImmutableSet.copyOf(resultPmids);
  }

  private static @Nonnull List<String> getFieldValues(@Nonnull Document doc, @Nonnull String fieldName) {
    final List<String> values = new ArrayList<>();
    for (IndexableField field : doc.getFields(fieldName)) {
      values.add(field.stringValue());
    }
    return values;
  }

  private static int getNumericField(Document doc, String field, int defaultValue) {
    return Optional.ofNullable(doc.getField(field))
        .map(IndexableField::numericValue)
        .orElse(defaultValue).intValue();
  }

  @SuppressWarnings("WeakerAccess")
  public static @Nonnull ClinicalTrial fromLucene(@Nonnull Document doc) {
    final String nctId = doc.get("nctid");
    assert nctId != null;

    final String briefTitle = doc.get("brief_title");
    final String officialTitle = doc.get("official_title");

    final String briefSummary = doc.get("summary");
    final String detailedDescription = doc.get("description");

    final String phase = doc.get("phase");
    assert phase != null;
    final String studyType = doc.get("studyType");
    final String masking = doc.get("masking");

    final List<String> conditions = getFieldValues(doc, "conditions");

    final List<Intervention> interventions = new ArrayList<>();
    for (IndexableField field : doc.getFields("interventions_ser")) {
      interventions.add(Intervention.fromBytes(field.binaryValue().bytes));
    }

    final List<String> meshTerms = getFieldValues(doc, "mesh_terms");
    final List<String> keywords = getFieldValues(doc, "keywords");
    final List<String> referencePmids = getFieldValues(doc, "result_pmids");

    final String eligibilityCriteria = doc.get("eligibility_criteria");
    final int minAge = getNumericField(doc, "eligibility_min_age", 0);
    final int maxAge = getNumericField(doc, "eligibility_max_age", 200);
    final String gender = Optional.ofNullable(doc.get("eligibility_gender")).orElse("N/A");

    return new SimpleClinicalTrial(nctId,
        briefTitle,
        officialTitle,
        briefSummary,
        detailedDescription,
        phase,
        studyType,
        masking,
        conditions,
        interventions,
        eligibilityCriteria,
        minAge,
        maxAge,
        gender,
        meshTerms,
        keywords,
        referencePmids);
  }


  @Nonnull
  public String getNctId() {
    return nctId;
  }

  @Nonnull
  public Optional<String> getBriefTitle() {
    return Optional.ofNullable(briefTitle);
  }

  @Nonnull
  public Optional<String> getOfficialTitle() {
    return Optional.ofNullable(officialTitle);
  }

  @Nonnull
  public Optional<String> getBriefSummary() {
    return Optional.ofNullable(briefSummary);
  }

  @Nonnull
  public Optional<String> getDetailedDescription() {
    return Optional.ofNullable(detailedDescription);
  }

  @Nonnull
  public String getPhase() {
    return phase;
  }

  public Optional<String> getStudyType() {
    return Optional.ofNullable(studyType);
  }

  @Nonnull
  public Optional<String> getMasking() {
    return Optional.ofNullable(masking);
  }

  @Nonnull
  public List<String> getConditions() {
    return conditions;
  }

  @Nonnull
  public List<Intervention> getInterventions() {
    return interventions;
  }

  @Nonnull
  public Optional<String> getEligibilityCriteria() {
    return Optional.ofNullable(eligibilityCriteria);
  }

  public int getMinAge() {
    return minAge;
  }

  public int getMaxAge() {
    return maxAge;
  }

  @Nonnull
  public String getGender() {
    return gender;
  }

  @Nonnull
  public Set<String> getMeshTerms() {
    return meshTerms;
  }

  @Nonnull
  public Set<String> getKeywords() {
    return keywords;
  }

  @Nonnull
  public Set<String> getResultPmids() {
    return resultPmids;
  }

  @Override
  public String getSearchHeading() {
    return getBriefTitle().orElse("Untitled") + '[' + getNctId() + ']';
  }

  @Override
  public String getId() {
    return getNctId();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClinicalTrial that = (ClinicalTrial) o;
    return Objects.equals(nctId, that.getNctId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(nctId);
  }
}
