package edu.utdallas.hltri.data.clinical_trials;

import edu.utdallas.hltri.scribe.text.Identifiable;
import edu.utdallas.hltri.trec.pm.search.Displayable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public interface ClinicalTrial extends Displayable, Identifiable {

  @Nonnull String getNctId();

  @Nonnull Optional<String> getBriefTitle();
  @Nonnull Optional<String> getOfficialTitle();

  @Nonnull Optional<String> getBriefSummary();
  @Nonnull Optional<String> getDetailedDescription();

  @Nonnull String getPhase();
  @Nonnull Optional<String> getStudyType();
  @Nonnull Optional<String> getMasking();

  @Nonnull List<String> getConditions();
  @Nonnull List<Intervention> getInterventions();

  @Nonnull Optional<String> getEligibilityCriteria();
  int getMinAge();
  int getMaxAge();
  @Nonnull String getGender();

  @Nonnull Set<String> getMeshTerms();
  @Nonnull Set<String> getKeywords();
  @Nonnull Set<String> getResultPmids();

  @Override
  default String getSearchHeading() {
    return getBriefTitle().orElse("Untitled") + '[' + getNctId() + ']';
  }

  @Override
  default String getId() {
    return getNctId();
  }
}
