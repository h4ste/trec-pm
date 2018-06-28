package edu.utdallas.hltri.data.nci_thesaurus;

import java.nio.file.Path;

import edu.utdallas.hltri.conf.Config;

@SuppressWarnings("WeakerAccess")
public class NciSettings {

  public final Path neoplasticAntibodiesPath;

  public NciSettings(Path neoplasticAntibodiesPath) {
    this.neoplasticAntibodiesPath = neoplasticAntibodiesPath;
  }

  public static NciSettings fromConfig(final Config conf) {
    return new NciSettings(conf.getPath("neoplastic-antibodies-path"));
  }

  public static NciSettings DEFAULT = fromConfig(Config.load("data.nci"));
}
