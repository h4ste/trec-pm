package edu.utdallas.hltri.data.cosmic;

import java.nio.file.Files;
import java.nio.file.Path;

import edu.utdallas.hltri.conf.Config;
import edu.utdallas.hltri.logging.Logger;

public class CosmicSettings {
  private static final Logger log = Logger.get(CosmicSettings.class);

  final Path drugTargetPath;
  final Path resistanceMutationPath;
  final Path geneCensusPath;

  private CosmicSettings(Config conf) {
    this(conf.getPath("drug-target-path"),
        conf.getPath("resistance-mutations-path"),
        conf.getPath("cancer-gene-census-path"));
  }

  private CosmicSettings(Path drugTargetPath, Path resistanceMutationPath, Path geneCensusPath) {
    this.drugTargetPath = drugTargetPath;
    this.resistanceMutationPath = resistanceMutationPath;
    this.geneCensusPath = geneCensusPath;

    if (this.drugTargetPath == null) {
      log.warn("Cosmic drug target path is NULL");
    } else if (!Files.isReadable(this.drugTargetPath)) {
      log.error("Cosmic drug target path |{}| is not readable.", this.drugTargetPath);
    }

    if (this.resistanceMutationPath == null) {
      log.warn("Cosmic resistance mutation path is NULL");
    } else if (!Files.isReadable(this.resistanceMutationPath)) {
      log.error("Cosmic resistance mutation path |{}| is not readable.",
          this.resistanceMutationPath);
    }

    if (this.geneCensusPath == null) {
      log.warn("Cosmic gene census path is NULL");
    } else if (!Files.isReadable(this.geneCensusPath)) {
      log.error("Cosmic gene census path |{}| is not readable.", this.geneCensusPath);
    }
  }

  public static final CosmicSettings DEFAULT = new CosmicSettings(Config.load("data.cosmic"));

  @SuppressWarnings("WeakerAccess")
  public static class Builder {
    Path drugTargetPath, resistanceMutationPath,geneCensusPath;

    public Builder setDrugTargetPath(Path drugTargetPath) {
      this.drugTargetPath = drugTargetPath;
      return this;
    }

    public Builder setResistanceMutationPath(Path resistanceMutationPath) {
      this.resistanceMutationPath = resistanceMutationPath;
      return this;
    }

    public Builder setGeneCensusPath(Path geneCensusPath) {
      this.geneCensusPath = geneCensusPath;
      return this;
    }

    public CosmicSettings build() {
      return new CosmicSettings(drugTargetPath, resistanceMutationPath, geneCensusPath);
    }
  }
}
