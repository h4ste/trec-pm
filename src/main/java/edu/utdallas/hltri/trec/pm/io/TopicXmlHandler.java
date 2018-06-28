package edu.utdallas.hltri.trec.pm.io;

import org.xml.sax.Attributes;

import edu.utdallas.hltri.trec.pm.Topics;
import edu.utdallas.hltri.io.ExtendedDefaultHandler;

/**
 * Created by travis on 6/7/17.
 */


public class TopicXmlHandler extends ExtendedDefaultHandler {

  private Topics        topics = null;
  private TopicXmlState state  = new TopicXmlState();


  @Override
  public void start(String tagname, Attributes attributes) {
    switch (tagname) {
      case "topics":
        assert topics == null : "Encountered multiple topics in single file";
        state.task = attributes.getValue("task");
        topics = new Topics();
        break;
      case "topic":
        assert topics != null : "Found topic outside of topics tag";
        state.reset();
        state.number = Short.parseShort(attributes.getValue("number"));
        break;
    }
  }

  @Override
  public void end(String tagname) {
    switch (tagname) {
      case "disease":
        state.disease = getCleanText();
        break;
      case "gene":
        state.gene = getCleanText();
        break;
      case "demographic":
        state.demographic = getCleanText();
        break;
      case "other":
        state.other = getCleanText();
        break;
      case "topic":
        topics.add(state.buildTopic());
        break;
      case "topics":
        state.resetTask();
        break;
    }
  }

  public Topics getTopics() {
    return topics;
  }
}
