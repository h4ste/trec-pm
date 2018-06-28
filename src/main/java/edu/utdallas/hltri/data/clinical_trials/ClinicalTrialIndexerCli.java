package edu.utdallas.hltri.data.clinical_trials;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import edu.utdallas.hltri.data.clinical_trials.jaxb.ClinicalStudy;
import edu.utdallas.hltri.framework.Commands;
import edu.utdallas.hltri.framework.ProgressLogger;
import edu.utdallas.hltri.logging.Logger;
import edu.utdallas.hltri.util.Unsafe;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

public class ClinicalTrialIndexerCli implements Runnable {
  private static final Logger log = Logger.get(ClinicalTrialIndexerCli.class);


  @Option(names = {"-D", "--delete"},
      description = "delete existing index first")
  private boolean replaceIndex;

  @Option(names = {"-n", "--negate"},
      description = "use negation processing for inclusion/exclusion criteria")
  private boolean doNegation;

  @Parameters(paramLabel = "INDEX-PATH",
      index = "0")
  private Path indexPath;

  @Parameters(paramLabel = "TRIAL-XML-DIR",
      index = "1..*")
  private Path[] dataDirs;

  @Override
  public void run() {
    try {
      if (replaceIndex && Files.isDirectory(indexPath)) {
        log.info("Deleting existing index at {}", indexPath);
        //noinspection ResultOfMethodCallIgnored
        Files.walk(indexPath, FileVisitOption.FOLLOW_LINKS)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .peek(file -> log.debug("deleting {}", file))
            .forEach(File::delete);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    final List<Path> files = Arrays.stream(dataDirs)
        .flatMap(Unsafe.function(dir -> Files.walk(dir, FileVisitOption.FOLLOW_LINKS)))
        .filter(path -> path.getFileName().toString().endsWith(".xml"))
        .collect(Collectors.toList());

    final ClinicalTrialParser parser = new ClinicalTrialParser();
    try (final ClinicalTrialIndexer indexer = new ClinicalTrialIndexer(indexPath, replaceIndex,
        doNegation);
        final ProgressLogger plog = ProgressLogger.fixedSize("indexing", files.size(),
            5, TimeUnit.MINUTES)) {
      for (Path file : files) {
        final ClinicalStudy study = parser.parseXmlFile(file);
        indexer.indexClinicalStudy(study);
        plog.update("indexed {}", study.getIdInfo().getNctId());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String... args) {
    Commands.run(new ClinicalTrialIndexerCli(), args);
  }
}
