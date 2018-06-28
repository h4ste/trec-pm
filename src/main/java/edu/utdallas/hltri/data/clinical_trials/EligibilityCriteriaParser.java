package edu.utdallas.hltri.data.clinical_trials;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

import edu.utdallas.hltri.logging.Logger;
import edu.utdallas.hltri.scribe.annotators.Annotator;
import edu.utdallas.hltri.scribe.annotators.GeniaAnnotator;
import edu.utdallas.hltri.scribe.annotators.LingScopeNegationSpanAnnotator;
import edu.utdallas.hltri.scribe.annotators.OpenNLPSentenceAnnotator;
import edu.utdallas.hltri.scribe.text.BaseDocument;
import edu.utdallas.hltri.scribe.text.Document;
import edu.utdallas.hltri.scribe.text.annotation.NegationSpan;
import edu.utdallas.hltri.scribe.text.annotation.Sentence;
import edu.utdallas.hltri.scribe.text.annotation.Token;

public class EligibilityCriteriaParser {
  private static final Logger log = Logger.get(EligibilityCriteriaParser.class);

  private final Pattern bullet =
      Pattern.compile("^\\s*(-|\\d+.\\d*)\\s+", Pattern.MULTILINE);
  private final Splitter bulletSplitter = Splitter.on(bullet).omitEmptyStrings().trimResults();

  private final Annotator<BaseDocument> sentenceSplitter =
      new OpenNLPSentenceAnnotator<>();
  private final Annotator<BaseDocument> negationDetector =
      new LingScopeNegationSpanAnnotator<>(
          document -> document.get(OpenNLPSentenceAnnotator.ANNOTATION_SET_NAME, Sentence.TYPE),
          sentence -> sentence.getContained(GeniaAnnotator.ANNOTATION_SET_NAME, Token.TYPE)
      );
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private Annotator<BaseDocument> tokenizer =
      new GeniaAnnotator.Builder<>()
          .annotateTokens()
          .withSentences(document -> document.get(OpenNLPSentenceAnnotator.ANNOTATION_SET_NAME,
              Sentence.TYPE))
          .build();

  void parseCriteria(String criteria,
                     BiConsumer<? super List<String>, ? super List<String>> consumer) {
    if (Strings.isNullOrEmpty(criteria)) {
      return;
    }

    final List<String> positiveSegments = new ArrayList<>();
    final List<String> negativeSegments = new ArrayList<>();
    for (final String bullet : bulletSplitter.split(criteria)) {
      if (Strings.isNullOrEmpty(bullet)) {
        continue;
      }
      try (final Document<BaseDocument> doc = Document.fromString(bullet)) {
        synchronized (this) {
            sentenceSplitter.annotate(doc);
            final Future<?> future = executor.submit(() -> { tokenizer.annotate(doc); });
          try {
            future.get(1, TimeUnit.MINUTES);
          } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Error in GENIA annotator. Restarting GENIA...");
            tokenizer.close();
            tokenizer = new GeniaAnnotator.Builder<>()
                .annotateTokens()
                .withSentences(document -> document.get(OpenNLPSentenceAnnotator.ANNOTATION_SET_NAME,
                    Sentence.TYPE))
                .build();
            continue;
          }
            negationDetector.annotate(doc);
        }
        int start = 0;
        if (doc.hasAnnotationSet(LingScopeNegationSpanAnnotator.ANNOTATION_SET)) {
          for (final NegationSpan span : doc.get(LingScopeNegationSpanAnnotator.ANNOTATION_SET,
              NegationSpan.TYPE)) {
            int delim = span.get(NegationSpan.StartOffset).intValue();
            if (delim != start) {
              positiveSegments.add(bullet.substring(start, delim));
              negativeSegments.add(span.asString());
              start = span.get(NegationSpan.EndOffset).intValue();
            }
          }
        } else {
          positiveSegments.add(bullet);
        }
      }
    }

    consumer.accept(positiveSegments, negativeSegments);
  }
}
