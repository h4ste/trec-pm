package edu.utdallas.hltri.trec.pm.ui;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.utdallas.hltri.inquire.SearchResult;
import edu.utdallas.hltri.inquire.SimpleSearchResult;
import edu.utdallas.hltri.inquire.eval.TrecRunReader;
import edu.utdallas.hltri.inquire.eval.TrecRunWriter;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@CommandLine.Command(name = "SubmissionMerger",
    headerHeading = "Usage:%n%n",
    synopsisHeading = "%n",
    showDefaultValues = true,
    descriptionHeading = "%nDescription:%n%n",
    parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n",
    header = "merge trec submission files",
    description = "create a new submission file by concatenating the results for each topic"
    + "in the provided submission files (in the order they are passed)")
public class SubmissionMerger implements Runnable {
  @Option(names = {"--runTag"})
  private String runTag;

  @Option(names = {"-L, --limit"},
          description = "max number of results to produce for each topic")
  private int limit = 1000;

  @Parameters(index = "0",
              paramLabel = "OUTPUT-FILE")
  private Path outputFile;

  @Parameters(index = "1..",
      paramLabel = "INPUT-FILES")
  private Path[] inputFiles;


  @Override
  public void run() {
    final List<TrecRunReader> readers = new ArrayList<>();
    for (final Path inputFile : inputFiles) {
      readers.add(new TrecRunReader(inputFile));
    }


    final Map<String, List<SearchResult<String>>> mergedResults = new LinkedHashMap<>();
    final Map<String, Set<String>> seenResults = new HashMap<>();
    for (TrecRunReader run : readers) {
      for (String topic : run.keySet()) {
        final List<SearchResult<String>> results =
            mergedResults.computeIfAbsent(topic, x -> new ArrayList<>());
        final Set<String> seen =
            seenResults.computeIfAbsent(topic, x -> new HashSet<>());
        for (SearchResult<String> result : run.get(topic)) {
          if (seen.add(result.getValue())) {
            results.add(new SimpleSearchResult<>(results.size() + 1,
                result.getScore(), result.getValue()));
          }
        }
      }
    }

    final String runTag = this.runTag == null ? readers.get(0).getRuntag() : this.runTag;
    try (TrecRunWriter writer = new TrecRunWriter(outputFile, runTag)) {
      for (String topic : mergedResults.keySet()) {
        List<SearchResult<String>> results = mergedResults.get(topic);
        if (results.size() < limit) {
          System.err.println("Only found " + results.size() + " merged results for topic " + topic);
        } else {
          results = results.subList(0, limit);
        }
        for (SearchResult<String> result : results) {
          writer.writeResult(topic, result.getValue(), result.getRank(), 1.0 / result.getRank());
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("Duplicates")
  public static void main(String... args) {
    final SubmissionMerger driver = new SubmissionMerger();
    final CommandLine command = new CommandLine(driver);
    command.registerConverter(Path.class, Paths::get);
    try {
      command.parse(args);
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
      command.usage(System.err);
      System.exit(1);
    }
    driver.run();
  }
}
