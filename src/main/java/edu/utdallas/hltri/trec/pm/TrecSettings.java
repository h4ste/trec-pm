package edu.utdallas.hltri.trec.pm;

import java.nio.file.Path;

import edu.utdallas.hltri.conf.Config;

/**
 * Created by trg19 on 7/6/2017.
 */
public enum TrecSettings {
  INSTANCE;

  private final Config conf = Config.load("trec.pm");

  public final Path velocityTemplatePath = conf.getPath("velocity.template-path");

  public final Path dgidbCachePath = conf.getPath("dgidb.cache-path");
  public final Path fdaLabelsCachePath = conf.getPath("fda-labels.cache-path");

  public final int getRrfConstant = conf.getInt("search.rrf-constant");


  public final boolean useTreatments = conf.getBoolean("use-treatments");

  public final LuceneSearchSettings MEDLINE =
      LuceneSearchSettings.fromConfig(conf.getConfig("medline.search"));

  public final LuceneSearchSettings CLINICAL_TRIALS =
      LuceneSearchSettings.fromConfig(conf.getConfig("clinical-trials.search"));
}
