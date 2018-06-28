package edu.utdallas.hltri.trec.pm.io;

import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.utdallas.hltri.trec.pm.Topic;

/**
 * Created by travis on 6/7/17.
 */
class TopicXmlState {
  String task;
  int number;
  String disease;
  String gene;
  String demographic;
  String other;

  private final Pattern  demographicRegex = Pattern.compile("(\\d+)-year-old\\s+(female|male)");
  private final Splitter splitter         = Splitter.on(',').omitEmptyStrings().trimResults();

  TopicXmlState() {
    reset();
  }

  void reset() {
    this.number = -1;
    this.disease = null;
    this.gene = null;
    this.demographic = null;
    this.other = null;
  }

  void resetTask() {
    this.task = null;
  }

  Topic buildTopic() {
    assert this.task != null : "Cannot create topic with NULL task";
    assert this.number > -1 : "Cannot create topic without a number";
    assert this.disease != null : "Cannot create topic with NULL disease";
    assert this.gene != null : "Cannot create topic with NULL genetic variations";
    assert this.demographic != null : "Cannot create topic with NULL demographics";
    assert this.other != null : "Cannot create topic with NULL additional criteria";

    final List<String> geneticVariations = splitter.splitToList(gene);
    final List<String> additionalCriteria = new ArrayList<>(splitter.splitToList(other));
    additionalCriteria.remove("None");

    final Matcher matcher = demographicRegex.matcher(demographic);
    assert matcher.matches() : "Cannot parse demographic information from |" + demographic + "|";
    final short age = Short.parseShort(matcher.group(1));
    final Topic.Gender gender = Topic.Gender.valueOf(matcher.group(2).toUpperCase());

    return new Topic(task, number, disease, geneticVariations, additionalCriteria, age, gender);
  }
}
