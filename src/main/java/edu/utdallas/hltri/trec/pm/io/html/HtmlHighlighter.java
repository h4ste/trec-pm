package edu.utdallas.hltri.trec.pm.io.html;

import com.google.common.base.CharMatcher;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Encoder;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.NullFragmenter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleHTMLEncoder;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HtmlHighlighter<T> {
  private final Map<T, Highlighter> highlighters;
  private final Analyzer                        analyzer;

  private final CharMatcher matcher = CharMatcher.anyOf("\r\n");

  HtmlHighlighter(Map<T, Query> queries, Analyzer analyzer) {
    // Wraps hits in documents with HTML tags
    final Formatter formatter =
        new SimpleHTMLFormatter("<span class=\"hit\">", "</span>");
    // Encodes document text for inclusion in HTML documents
    final Encoder encoder = new SimpleHTMLEncoder();
    // Highlights are query-specific, so pre-generate all highlighters
    this.highlighters = new HashMap<>();
    for (Map.Entry<T, Query> entry : queries.entrySet()) {
      Verify.verifyNotNull(entry.getValue());
      // Scores spans of text for a query
      final Scorer scorer = new QueryScorer(entry.getValue());
      Verify.verifyNotNull(scorer);
      final Highlighter highlighter = new Highlighter(formatter, encoder, scorer);
      Verify.verifyNotNull(highlighter);
      // We want to print the entire document, not just a fragment
      highlighter.setTextFragmenter(new NullFragmenter());
      this.highlighters.put(entry.getKey(), highlighter);
    }
    this.analyzer = analyzer;
  }

  public String highlight(final T topic, String field, String text)
      throws IOException, InvalidTokenOffsetsException {
    Preconditions.checkNotNull(topic);
    Preconditions.checkNotNull(field);
    Preconditions.checkNotNull(analyzer);
    if (text == null) {
      return "None";
    } else {
      final String highlighted = highlighters.get(topic).getBestFragment(analyzer, field, text);
      final String result;
      if (highlighted == null) {
        result = text;
      } else {
        result = highlighted;
      }
      return result;
    }
  }

  public String highlightWithNewlines(final T topic, String field, String text)
      throws IOException, InvalidTokenOffsetsException {
    return matcher.replaceFrom(
        matcher.trimAndCollapseFrom(highlight(topic, field, text), '\n'),
        "<br/>");
  }
}
