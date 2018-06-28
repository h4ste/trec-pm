package edu.utdallas.hltri.data.cancer_abst;

import edu.utdallas.hltri.data.medline.basic.SimpleMedlineArticle;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import edu.utdallas.hltri.data.medline.MedlineArticle;
import edu.utdallas.hltri.data.medline.basic.MedlineIndexer;
import edu.utdallas.hltri.framework.ProgressLogger;
import edu.utdallas.hltri.io.IOUtils;
import edu.utdallas.hltri.logging.Logger;

@SuppressWarnings("WeakerAccess")
public class ExtraCancerAbstractIndexer extends MedlineIndexer {
  private static final Logger log = Logger.get(ExtraCancerAbstractIndexer.class);

//  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");

  public ExtraCancerAbstractIndexer(final Path indexPath, final ProgressLogger plog) {
    super(indexPath, plog, true);
  }

  public MedlineArticle parseAbstract(final Path path) {
    try (final BufferedReader reader = Files.newBufferedReader(path)) {
      final String id = IOUtils.removeExtension(path.getFileName().toString());

      final String meetingLine = reader.readLine();
      assert meetingLine.startsWith("Meeting: ") : "No meeting line on " + path.getFileName();
      final String meeting = meetingLine.substring(9);
      LocalDate creationDate;
      try {
        creationDate = LocalDate.of(Integer.valueOf(meeting.substring(0, 4)), 1, 1);
      } catch (DateTimeParseException e) {
        log.error("Failed to parse {}", path.getFileName());
        throw new RuntimeException(e);
      }

      final String titleLine = reader.readLine();
      assert titleLine.startsWith("Title: ") : "No title line on " + path.getFileName();
      final String title = titleLine.substring(7);

      final StringBuilder sb = new StringBuilder();
      for (String line; ((line = reader.readLine()) != null); ) {
        sb.append(line).append('\n');
      }
      final String text = sb.toString().trim();

      return SimpleMedlineArticle.sparse(
          id,
          title,
          meeting,
          creationDate,
          text
      );
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void indexDirectory(Path path) {
    try {
      Files.walk(path)
          .filter(file -> file.getFileName().toString().endsWith(".txt"))
          .parallel()
          .map(this::parseAbstract)
          .forEach(this::indexDocument);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String... args) {
    final Path indexDir = Paths.get(args[0]);

    Arrays.stream(args)
        .skip(1)
        .map(Paths::get)
        .forEachOrdered( dir -> {
          try (ProgressLogger plog = ProgressLogger.indeterminateSize(
              "indexing " + dir.getFileName(), 1, TimeUnit.MINUTES);
               ExtraCancerAbstractIndexer indexer = new ExtraCancerAbstractIndexer(indexDir, plog)) {
            indexer.indexDirectory(dir);
          }
        });

  }
}
