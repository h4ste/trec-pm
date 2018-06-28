package edu.utdallas.hltri.data.clinical_trials;

import edu.utdallas.hltri.conf.Config;

/**
 * Created by trg19 on 7/6/2017.
 */
public class ClinicalTrialSettings {
  public static final ClinicalTrialSettings EAGER =
      ClinicalTrialSettings.fromConfig(Config.load("data.clinical-trials.simple"));

  public static final ClinicalTrialSettings LAZY =
      ClinicalTrialSettings.fromConfig(Config.load("data.clinical-trials.lazy"));

  public final String indexPath;
  public final String defaultField;

  public ClinicalTrialSettings(String indexPath, String defaultField) {
    this.indexPath = indexPath;
    this.defaultField = defaultField;
  }

  public static ClinicalTrialSettings fromConfig(Config conf) {
    return new ClinicalTrialSettings(
        conf.getPath("search.index-path").toAbsolutePath().toString(),
        conf.getString("search.default-field")
    );
  }
}
