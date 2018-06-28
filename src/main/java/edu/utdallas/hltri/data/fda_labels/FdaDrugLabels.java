package edu.utdallas.hltri.data.fda_labels;

import com.google.common.collect.ImmutableSetMultimap;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.utdallas.hltri.trec.pm.analysis.DrugInferenceMethod;
import edu.utdallas.hltri.trec.pm.GeneticVariant;

public class FdaDrugLabels implements DrugInferenceMethod {
  private final ImmutableSetMultimap<String, String> drugsByGene;

  private FdaDrugLabels(ImmutableSetMultimap<String, String> drugsByGene) {
    this.drugsByGene = drugsByGene;
  }

  public static FdaDrugLabels fromCache(Path path) {
    final ImmutableSetMultimap.Builder<String, String> drugsByGene = new ImmutableSetMultimap.Builder<>();
    try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(path)))) {
      //noinspection unchecked
      final Map<String, List<String>> map = (Map<String, List<String>>) ois.readObject();
      for (Map.Entry<String, List<String>> entry : map.entrySet()) {
        drugsByGene.putAll(entry.getKey(), entry.getValue());
      }
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    return new FdaDrugLabels(drugsByGene.build());
  }


  @Override
  public Collection<String> apply(GeneticVariant geneticVariant) {
    return drugsByGene.get(geneticVariant.getGene());
  }
}
