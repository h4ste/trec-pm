package edu.utdallas.hltri.trec.pm;

import java.util.Map;

import edu.utdallas.hltri.conf.Config;

public class LuceneSearchSettings {
  public final double keyphraseWeight;
  public final int keyphraseSlop;
  public final boolean enforceKeyphraseTermOrder;

  public final Map<String, Double> fieldWeights;
  public final float               disjunctionTieBreaker;

  private LuceneSearchSettings(double keyphraseWeight,
                            int keyphraseSlop,
                            boolean enforceKeyphraseTermOrder,
                            Map<String, Double> fieldWeights,
                            float disjunctionTieBreaker) {
    this.keyphraseWeight = keyphraseWeight;
    this.keyphraseSlop = keyphraseSlop;
    this.enforceKeyphraseTermOrder = enforceKeyphraseTermOrder;
    this.fieldWeights = fieldWeights;
    this.disjunctionTieBreaker = disjunctionTieBreaker;
  }

  static LuceneSearchSettings fromConfig(Config conf) {
    return new LuceneSearchSettings(
        conf.getDouble("keyphrase.weight"),
        conf.getInt("keyphrase.slop"),
        conf.getBoolean("keyphrase.enforce-term-order"),
        conf.getDoubleMap("field-weights"),
        conf.getFloat("disjunction-tie-breaker"));
  }

  public LuceneSearchSettings cloneWithFieldWeights(Map<String, Double> newFieldWeights) {
    return new LuceneSearchSettings(
        this.keyphraseWeight,
        this.keyphraseSlop,
        this.enforceKeyphraseTermOrder,
        newFieldWeights,
        this.disjunctionTieBreaker);
  }

  @Override
  public String toString() {
    return "LuceneSearchSettings{" +
        "keyphraseWeight=" + keyphraseWeight +
        ", keyphraseSlop=" + keyphraseSlop +
        ", enforceKeyphraseTermOrder=" + enforceKeyphraseTermOrder +
        ", fieldWeights=" + fieldWeights +
        ", disjunctionTieBreaker=" + disjunctionTieBreaker +
        '}';
  }
}
