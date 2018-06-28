package edu.utdallas.hltri.trec.pm.analysis;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import edu.utdallas.hltri.data.dgidb.Dgidb;
import edu.utdallas.hltri.data.dgidb.DgidbSettings;
import edu.utdallas.hltri.logging.Logger;
import edu.utdallas.hltri.trec.pm.AnalyzedTopic;
import edu.utdallas.hltri.trec.pm.GeneticVariant;

@SuppressWarnings("WeakerAccess")
public class CachingDgidbDrugInferrer {
  private static final Logger log = Logger.get(CachingDgidbDrugInferrer.class);

  private final ConcurrentMap<Set<String>, ImmutableSetMultimap<String, String>> cache;
  private final Path                                                    cachePath;
  private final Dgidb                                                   dgidb;

  @SuppressWarnings("WeakerAccess")
  public CachingDgidbDrugInferrer(Path cachePath) {
    this.cachePath = cachePath;
    ConcurrentMap<Set<String>, ImmutableSetMultimap<String, String>> fileCache;
    try (ObjectInputStream ois =
             new ObjectInputStream(new BufferedInputStream(Files.newInputStream(cachePath)))) {
      //noinspection unchecked
      fileCache = (ConcurrentMap<Set<String>, ImmutableSetMultimap<String, String>>) ois.readObject();
      log.debug("Restored DGIdb drug interactions from {}", cachePath);
    } catch (IOException | ClassNotFoundException e) {
      log.error("Unable to read cache", e);
      fileCache = new ConcurrentHashMap<>();
    }
    this.cache = fileCache;
    this.dgidb = new Dgidb(DgidbSettings.DEFAULT);
  }

  @SuppressWarnings("WeakerAccess")
  public ImmutableSetMultimap<String, String> inferDrugsFromGenes(Collection<String> genes) {
    Set<String> geneSet = (genes instanceof Set) ? (Set<String>) genes : new HashSet<>(genes);
    return cache.computeIfAbsent(geneSet, dgidb::getDrugsForGenes);
  }

  private ImmutableSetMultimap<String, String> filterDrugs(SetMultimap<String, String> sourcesByDrugs,
                                                           DrugNameFilter filter) {
    ImmutableSetMultimap.Builder<String, String> sourcesByFilteredDrugs =
        new ImmutableSetMultimap.Builder<>();

    sourcesByDrugs.keySet().stream().filter(filter).forEach(
        drug -> sourcesByFilteredDrugs.putAll(drug, sourcesByDrugs.get(drug))
    );

    return sourcesByFilteredDrugs.build();
  }

  @SuppressWarnings("WeakerAccess")
  public void inferDrugs(AnalyzedTopic topic, DrugNameFilter filter) {
    final Set<String> genes = topic.getGenes().stream()
        .flatMap(GeneticVariant::getGenesAsStream)
        .collect(Collectors.toSet());

    final ImmutableSetMultimap<String, String> sourcesByDrug =
        filterDrugs(inferDrugsFromGenes(genes), filter);
    log.debug("Found drugs {} for genes {}", sourcesByDrug, topic.getGenes());
    final ImmutableSetMultimap<String, String> drugsBySource = sourcesByDrug.inverse();
    for (final String source : drugsBySource.keySet()) {
      topic.addInferedDrugsForGene("DGIdb:" + source, drugsBySource.get(source));
    }
  }

  @SuppressWarnings("WeakerAccess")
  public void save() {
    try (ObjectOutputStream oos
             = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(cachePath)))) {
      oos.writeObject(cache);
      log.debug("Cached DGIdb interactions to {}", cachePath);
    } catch (IOException e) {
      log.error("Unable to write cache", e);
    }
  }
}
