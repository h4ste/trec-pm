package edu.utdallas.hltri.data.nci_thesaurus;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableSet;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import edu.utdallas.hltri.util.Lazy;

public class AntineoplasticAntibodies extends ForwardingSet<String> {
  private final NciSettings settings;
  private final Supplier<ImmutableSet<String>> antibodies = Lazy.lazily(this::parseAntibodies);

  public AntineoplasticAntibodies(NciSettings settings) {
    this.settings = settings;
  }

  private ImmutableSet<String> parseAntibodies() {
    try {
      final List<String> lines = Files.readAllLines(settings.neoplasticAntibodiesPath);
      return ImmutableSet.copyOf(lines);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected Set<String> delegate() {
    return antibodies.get();
  }
}
