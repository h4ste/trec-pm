package edu.utdallas.hltri.trec.pm;

import com.google.common.collect.ForwardingList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by travis on 6/7/17.
 */
public class Topics extends ForwardingList<Topic> {
  private final List<Topic> topics;

  @SuppressWarnings("WeakerAccess")
  public Topics(List<Topic> topics) {
    this.topics = topics;
  }

  public Topics() {
    this(new ArrayList<>());
  }

  @Override
  protected List<Topic> delegate() {
    return this.topics;
  }
}
