package edu.utdallas.hltri.trec.pm.io.html;

import com.google.common.base.Preconditions;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.search.Query;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.tools.ToolManager;
import org.springframework.beans.MethodInvocationException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Map;

import edu.utdallas.hltri.inquire.SearchResult;
import edu.utdallas.hltri.inquire.engines.SearchResultsList;
import edu.utdallas.hltri.logging.Logger;
import edu.utdallas.hltri.trec.pm.TrecSettings;

public class HtmlOutputManager<T> {
  private static final Logger log = Logger.get(HtmlOutputManager.class);

  private final VelocityEngine ve;
  private final ToolManager manager;

  public HtmlOutputManager() {
    log.debug("Initializing Velocity Engine...");
    this.ve = new VelocityEngine();

    // Try to make Velocity log to SLF4J (via log4j converter)
    ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
        HltriLoggingChute.class.getName());

    // Tell Velocity to look for templates using the classpath.
    ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
    ve.setProperty("classpath.resource.loader.class",
        PrefixedClasspathResourceLoader.class.getName());
    ve.setProperty("classpath.resource.loader.prefix",
        TrecSettings.INSTANCE.velocityTemplatePath.toString());
    ve.setProperty("classpath.resource.loader.cache", false);

    ve.init();

    this.manager = new ToolManager();
    manager.setVelocityEngine(ve);
  }

  public <K> void generateHtmlOutput(
      final Collection<T> topics,
      final Map<T, ? extends SearchResultsList<K, ? extends SearchResult<K>>> results,
      final Map<T, Query> queries,
      final Path path,
      final String hitTemplate) {
    Preconditions.checkNotNull(topics);
    Preconditions.checkNotNull(results);
    Preconditions.checkNotNull(queries);
    Preconditions.checkNotNull(path);
    Preconditions.checkNotNull(hitTemplate);


    //noinspection ResultOfMethodCallIgnored
    path.getParent().toFile().mkdirs();
    if (Files.isReadable(path)) {
      log.warn("Replacing existing output at {}!", path);
    }

    try {
      Files.copy(this.getClass().getResourceAsStream("/styles.css"),
          path.getParent().resolve("styles.css"),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      log.error("Failed to create 'styles.css'", e);
      throw new RuntimeException(e);
    }

    log.trace("Preparing Velocity context...");
    final Context context = manager.createContext();
    // List of topics
    context.put("topics", topics);

    // Results by topic
    context.put("results", results);

    // Lucene Query by topic
    context.put("queries", queries);

    context.put("highlighter", new HtmlHighlighter<>(queries, new EnglishAnalyzer()));

    // Name of template used to render hits
    context.put("hitTemplate", hitTemplate);

    try {
      log.trace("Fetching Velocity template...");
      Template template = ve.getTemplate("index.vm");
      try (BufferedWriter writer = Files.newBufferedWriter(path)) {
        log.debug("Generating HTML from Velocity template...");
        template.merge(context, writer);
      }
    } catch (ResourceNotFoundException | ParseErrorException |
        MethodInvocationException | IOException e) {
      log.error("Failed to generate HTML from Velocity Template", e);
      throw new RuntimeException(e);
    }
  }
}
