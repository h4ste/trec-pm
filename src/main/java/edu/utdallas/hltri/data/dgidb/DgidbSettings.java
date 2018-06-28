package edu.utdallas.hltri.data.dgidb;

import java.nio.file.Path;

import edu.utdallas.hltri.conf.Config;

/**
 * Created by trg19 on 7/20/2017.
 */
@SuppressWarnings("WeakerAccess")
public class DgidbSettings {
  public static final DgidbSettings DEFAULT = new DgidbSettings(Config.load("data.dgbi"));

  final int maxCacheSize;
  final Path cachePath;
  final String apiUrl;

  private DgidbSettings(Config conf) {
    this(conf.getPath("cache-path"),
        conf.getString("api-uri"),
        conf.getInt("max-cache-size"));
  }

  public DgidbSettings(Path cachePath, String apiUrl, int maxCacheSize) {
    this.cachePath = cachePath;
    this.apiUrl = apiUrl;
    this.maxCacheSize = maxCacheSize;
  }
}
