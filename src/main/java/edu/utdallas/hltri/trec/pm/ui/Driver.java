package edu.utdallas.hltri.trec.pm.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import edu.utdallas.hltri.trec.pm.analysis.TopicAnalyzer;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 *
 * Created by travis on 6/7/17.
 */
@Command(name = "Driver",
         headerHeading = "Usage:%n%n",
         synopsisHeading = "%n",
         showDefaultValues = true,
         descriptionHeading = "%nDescription:%n%n",
         parameterListHeading = "%nParameters:%n",
         optionListHeading = "%nOptions:%n",
         header = "run the TREC-PM retrieval system",
         description = "Process the given topics and produce TREC-PM submission files in the " +
             "given output directory.")
public class Driver implements Runnable {

  @Option(names = {"-m", "--model"},
          paramLabel = "MODEL",
          description = "search model, valid options are " +
              "[JOINT, ASPECT_FUSION, SIMILARITY_FUSION, REINFORCE, EAGER]")
  private SearchMethod searchMethod = SearchMethod.JOINT;

  @Parameters(index = "0", paramLabel = "TOPICS",
              description = "xml file containing official (or extra) TREC-PM topics")
  private Path topicsPath;

  @Parameters(index = "1", paramLabel = "DIRECTORY",
              description = "path of directory to contain generated output files")
  private Path outputPath;

  @Option(names = {"-t", "--runtag"}, paramLabel = "RUNTAG",
          description = "RUNTAG to be used for TREC-PM submission files")
  private String runTag = "UTDHLT";

  @Override
  public void run() {
    assert Files.isReadable(topicsPath);

    if (Files.exists(outputPath)) {
      assert Files.isDirectory(outputPath);
    } else {
      try {
        Files.createDirectories(outputPath);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    final TopicAnalyzer driver = new TopicAnalyzer(topicsPath);
      driver.analyzeTopics();
      driver.expandTopics();


    // do medline
    searchMethod.getSearchManager(driver.getAnalyzedTopics(), TaskConfiguration.MEDLINE)
        .search(1_000)
        .logResults(10)
        .saveRunFile(outputPath.resolve("medline_submission.txt"), runTag)
        .saveHtml(outputPath.resolve("medline_results.html"));

    // do clinical trials
    searchMethod.getSearchManager(driver.getAnalyzedTopics(), TaskConfiguration.CLINICAL_TRIALS)
        .search(1_000)
        .logResults(10)
        .saveRunFile(outputPath.resolve("clinical_trial_submission.txt"), runTag)
        .saveHtml(outputPath.resolve("clinical_trial_results.html"));
  }

  @SuppressWarnings("Duplicates")
  public static void main(String... args) {
    final Driver driver = new Driver();
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
