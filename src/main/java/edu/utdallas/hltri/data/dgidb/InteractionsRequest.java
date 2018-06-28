package edu.utdallas.hltri.data.dgidb;

import com.google.common.base.Joiner;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import edu.utdallas.hltri.data.dgidb.params.DrugType;
import edu.utdallas.hltri.data.dgidb.params.GeneCategory;
import edu.utdallas.hltri.data.dgidb.params.InteractionSource;
import edu.utdallas.hltri.data.dgidb.params.InteractionType;
import edu.utdallas.hltri.data.dgidb.params.SourceTrustLevel;
import edu.utdallas.hltri.data.dgidb.params.UriParameter;

@SuppressWarnings("unused")
public class InteractionsRequest {
  private final Multimap<String, String> parameters = LinkedHashMultimap.create();

  private InteractionsRequest(String requestType, Iterable<String> values) {
    this.parameters.putAll(requestType, values);
  }

  @SuppressWarnings("WeakerAccess")
  public static InteractionsRequest forGenes(Iterable<String> genes) {
    return new InteractionsRequest("genes", genes);
  }

  @SuppressWarnings("WeakerAccess")
  public static InteractionsRequest forGenes(String... genes) {
    return InteractionsRequest.forGenes(Arrays.asList(genes));
  }

  @SuppressWarnings("WeakerAccess")
  public static InteractionsRequest forDrugs(Iterable<String> drugs) {
    return new InteractionsRequest("drugs", drugs);
  }

  @SuppressWarnings("WeakerAccess")
  public static InteractionsRequest forDrugs(String... drugs) {
    return InteractionsRequest.forDrugs(Arrays.asList(drugs));
  }

  private InteractionsRequest setParameter(String label, Collection<? extends UriParameter> parameters) {
    this.parameters.putAll(
        label,
        parameters.stream()
            .map(UriParameter::getValue)
            .collect(Collectors.toList())
    );
    return this;
  }

  @SuppressWarnings("WeakerAccess")
  public InteractionsRequest setInteractionTypes(Collection<InteractionType> types) {
    return setParameter(InteractionType.getLabel(), types);
  }

  public InteractionsRequest setInteractionTypes(InteractionType... types) {
    return setInteractionTypes(Arrays.asList(types));
  }

  @SuppressWarnings("WeakerAccess")
  public InteractionsRequest setInteractionSources(Collection<InteractionSource> sources) {
    return setParameter(InteractionSource.getLabel(), sources);
  }

  public InteractionsRequest setInteractionSources(InteractionSource... sources) {
    return setInteractionSources(Arrays.asList(sources));
  }

  @SuppressWarnings("WeakerAccess")
  public InteractionsRequest setDrugTypes(Collection<DrugType> types) {
    return setParameter(DrugType.getLabel(), types);
  }

  public InteractionsRequest setDrugTypes(DrugType... types) {
    return setDrugTypes(Arrays.asList(types));
  }

  @SuppressWarnings("WeakerAccess")
  public InteractionsRequest setGeneCategories(Collection<GeneCategory> categories) {
    return setParameter(GeneCategory.getLabel(), categories);
  }

  public InteractionsRequest setGeneCategories(GeneCategory... categories) {
    return setGeneCategories(Arrays.asList(categories));
  }

  @SuppressWarnings("WeakerAccess")
  public InteractionsRequest setSourceTrustLevels(Collection<SourceTrustLevel> levels) {
    return setParameter(SourceTrustLevel.getLabel(), levels);
  }

  @SuppressWarnings("WeakerAccess")
  public InteractionsRequest setSourceTrustLevels(SourceTrustLevel... levels) {
    return setSourceTrustLevels(Arrays.asList(levels));
  }

  Map<String, String> buildQueryMap() {
    final Joiner joiner = Joiner.on(',');
    final Map<String, String> queryMap = new LinkedHashMap<>();
    for (Map.Entry<String,Collection<String>> entry : this.parameters.asMap().entrySet()) {
      queryMap.put(entry.getKey(), joiner.join(entry.getValue()));
    }
    return queryMap;
  }

  @Override
  public String toString() {
    return "InteractionsRequest" + parameters.toString();
  }
}
