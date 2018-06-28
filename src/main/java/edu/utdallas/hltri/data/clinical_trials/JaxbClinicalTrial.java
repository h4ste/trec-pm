package edu.utdallas.hltri.data.clinical_trials;

import com.google.common.base.Objects;
import com.google.common.base.Suppliers;
import edu.utdallas.hltri.data.clinical_trials.jaxb.ClinicalStudy;
import edu.utdallas.hltri.data.clinical_trials.jaxb.GenderEnum;
import edu.utdallas.hltri.data.clinical_trials.jaxb.InvestigatorStruct;
import edu.utdallas.hltri.data.clinical_trials.jaxb.VariableDateStruct;
import edu.utdallas.hltri.inquire.lucene.LuceneUtils;
import edu.utdallas.hltri.logging.Logger;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import javax.annotation.Nonnull;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.util.BytesRef;

public class JaxbClinicalTrial implements ClinicalTrial {
  private static final Logger log = Logger.get(JaxbClinicalTrial.class);
  protected final Supplier<ClinicalStudy> study;
  protected final int docId;

  private static final List<DateTimeFormatter> dateFormats = Arrays.asList(
      DateTimeFormatter.ofPattern("M d y"),
      DateTimeFormatter.ofPattern("L d y"),
      DateTimeFormatter.ofPattern("M y"),
      DateTimeFormatter.ofPattern("L y")
  );

  public JaxbClinicalTrial(int docId, Supplier<ClinicalStudy> study) {
    this.study = study;
    this.docId = docId;
  }

  public JaxbClinicalTrial(IndexReader reader, int luceneDocId) {
    this.docId = luceneDocId;
    this.study = Suppliers.memoize(() -> {
      try {
        final BytesRef bytes = LuceneUtils.getBinaryDocValue(reader, "bytes", luceneDocId);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes.bytes, bytes.offset, bytes.length)) {
          try (ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(bais))) {
            return (ClinicalStudy) ois.readObject();
          } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
          }
        } catch (NullPointerException npe) {
          log.error("Failed to load bytes for document {}: {}", luceneDocId,
              reader.document(luceneDocId));
          throw npe;
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Override
  public @Nonnull String getNctId() {
    return study.get().getIdInfo().getNctId();
  }

  private <O> Optional<O> tryOptionally(Function<ClinicalStudy, O> func) {
    try {
      return Optional.ofNullable(func.apply(study.get()));
    } catch (NullPointerException e) {
      return Optional.empty();
    }
  }

  @Override
  public @Nonnull Optional<String> getBriefTitle() {
    return tryOptionally(ClinicalStudy::getBriefTitle);
  }

  @Override
  public @Nonnull Optional<String> getOfficialTitle() {
    return tryOptionally(ClinicalStudy::getOfficialTitle);
  }

  @Override
  public @Nonnull Optional<String> getBriefSummary() {
    return tryOptionally(study -> study.getBriefSummary().getTextblock());
  }

  @Override
  public @Nonnull Optional<String> getDetailedDescription() {
    return tryOptionally(study -> study.getDetailedDescription().getTextblock());
  }

  @Override
  public @Nonnull String getPhase() {
    return study.get().getPhase().value();
  }

  @Override
  public @Nonnull Optional<String> getStudyType() {
    return tryOptionally(study -> study.getStudyType().value());
  }

  @Override
  public @Nonnull Optional<String> getMasking() {
    return tryOptionally(study -> study.getStudyDesignInfo().getMasking());
  }

  @Override
  public @Nonnull List<String> getConditions() {
    return study.get().getCondition();
  }

  @Override
  public @Nonnull List<Intervention> getInterventions() {
    return study.get().getIntervention().stream()
        .map(Intervention::fromStruct)
        .collect(Collectors.toList());
  }

  @Override
  public @Nonnull Optional<String> getEligibilityCriteria() {
    return tryOptionally(study -> study.getEligibility().getCriteria().getTextblock());
  }

  @Override
  public int getMinAge() {
    return tryOptionally(study -> study.getEligibility().getMinimumAge()).flatMap(ClinicalTrialIndexer::parseAge).orElse(0);
  }

  @Override
  public int getMaxAge() {
    return tryOptionally(study -> study.getEligibility().getMaximumAge()).flatMap(ClinicalTrialIndexer::parseAge).orElse(100);
  }

  @Override
  public @Nonnull String getGender() {
    return tryOptionally(study -> study.getEligibility().getGender()).orElse(GenderEnum.ALL).value();
  }

  @SuppressWarnings("WeakerAccess")
  public @Nonnull List<String> getConditionMeshTerms() {
    return tryOptionally(study -> study.getConditionBrowse().getMeshTerm())
        .orElse(Collections.emptyList());
  }

  @SuppressWarnings("WeakerAccess")
  public @Nonnull List<String> getInterventionMeshTerms() {
    return tryOptionally(study -> study.getInterventionBrowse().getMeshTerm())
        .orElse(Collections.emptyList());
  }

  @Override
  public @Nonnull Set<String> getMeshTerms() {
    final Set<String> meshTerms = new HashSet<>();
    meshTerms.addAll(getConditionMeshTerms());
    meshTerms.addAll(getInterventionMeshTerms());
    return meshTerms;
  }

  @Override
  public @Nonnull Set<String> getKeywords() {
    return new HashSet<>(study.get().getKeyword());
  }

  @Override
  public @Nonnull Set<String> getResultPmids() {
    return tryOptionally(ClinicalStudy::getResultsReference)
        .orElse(Collections.emptyList())
        .stream()
        .map(rs -> rs.getPMID().toString())
        .collect(Collectors.toSet());
  }

  @SuppressWarnings("WeakerAccess")
  public @Nonnull List<InvestigatorStruct> getOfficialInvestigators() {
    return study.get().getOverallOfficial();
  }

  public @Nonnull Collection<InvestigatorStruct> getAllInvestigators() {
    final Map<String, InvestigatorStruct> investigators =
        Stream.concat(
            getOfficialInvestigators().stream(),
            study.get().getLocation().stream().flatMap(l -> l.getInvestigator().stream()))
          .collect(Collectors.toMap(i -> i.getFirstName() + ' ' + i.getLastName(),
              i -> i,
              (a, b) -> b));
    return investigators.values();
  }

  private long parseDate(String date) {
    for (DateTimeFormatter fmt : dateFormats) {
      try {
        return LocalDate.parse(date, fmt).toEpochDay();
      } catch (DateTimeParseException dtpe) {
        // do nothing
      }
    }
    return 0;
  }

  public long getDate() {
    VariableDateStruct date = study.get().getCompletionDate();
    if (date != null) {
      return parseDate(study.get().getCompletionDate().getValue());
    }
    date = study.get().getStartDate();
    if (date != null) {
      return parseDate(study.get().getStartDate().getValue());
    }
    return 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ClinicalTrial that = (ClinicalTrial) o;
    return this.getNctId().equals(that.getNctId());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.getNctId());
  }
}
