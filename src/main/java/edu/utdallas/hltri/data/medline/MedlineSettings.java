package edu.utdallas.hltri.data.medline;

import edu.utdallas.hltri.conf.Config;

/**
 * Created by trg19 on 7/6/2017.
 */
public class MedlineSettings {
  public static final MedlineSettings FULL =
      MedlineSettings.fromConfig(Config.load("data.medline.full"));

  public static final MedlineSettings LAZY =
      MedlineSettings.fromConfig(Config.load("data.medline.lazy"));

  public static final MedlineSettings DEFAULT = FULL;

  public MedlineSettings(String indexPath, String defaultField) {
    this.indexPath = indexPath;
    this.defaultField = defaultField;
  }

  public static MedlineSettings fromConfig(Config conf) {
    return new MedlineSettings(
        conf.getPath("search.index-path").toAbsolutePath().toString(),
        conf.getString("search.default-field")
    );
  }

  public final String indexPath;
  public final String defaultField;
}
