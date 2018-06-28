package edu.utdallas.hltri.trec.pm.search.query.medline;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.WildcardQuery;

import edu.utdallas.hltri.inquire.lucene.LuceneSearchEngine;
import edu.utdallas.hltri.logging.Logger;
import edu.utdallas.hltri.trec.pm.AnalyzedTopic;
import edu.utdallas.hltri.trec.pm.GeneticVariant;
import edu.utdallas.hltri.trec.pm.Mutation;
import edu.utdallas.hltri.trec.pm.search.query.LuceneSingleQueryFactory;

public class SimpleMedlineQueryFactory implements LuceneSingleQueryFactory {
  private static final Logger log = Logger.get(SimpleMedlineQueryFactory.class);
  private final LuceneSearchEngine<?> searchEngine;

  public SimpleMedlineQueryFactory(LuceneSearchEngine<?> searchEngine) {
    this.searchEngine = searchEngine;
  }

  private Query getPhaseTrialQuery() {
    final BooleanQuery.Builder bq = new BooleanQuery.Builder();
    bq.add(searchEngine.newPhraseQuery("article_title", "phase"), Occur.SHOULD);
    bq.add(searchEngine.newPhraseQuery("article_title", "trial"), Occur.SHOULD);
    return bq.build();
  }

  private Query getTumorInTitleQuery(final AnalyzedTopic topic) {
    return searchEngine.newPhraseQuery("article_title", topic.getDisease());
  }

  private Query getGenesInTitleQuery(final AnalyzedTopic topic) {
    if (topic.getGenes().size() > 1) {
      final BooleanQuery.Builder bq = new BooleanQuery.Builder();
      for (final GeneticVariant gene : topic.getGenes()) {
        bq.add(searchEngine.newPhraseQuery("article_title", gene.getGene()), Occur.SHOULD);
      }
      return bq.build();
    } else {
      return searchEngine.newPhraseQuery("article_title",
          topic.getGenes().iterator().next().getGene());
    }
  }

  @SuppressWarnings("Duplicates")
  private Query getSpecificMutationsInFieldQuery(final AnalyzedTopic topic, final String field) {
    final BooleanQuery.Builder bq = new BooleanQuery.Builder();
    for (final GeneticVariant variant : topic.getGenes()) {
      final BooleanQuery.Builder gbq = new BooleanQuery.Builder();
      if (variant.getMutation() == Mutation.POINT_MUTATION && variant.getLocus() != null) {
        if (variant.getExon() != null) {
          final BooleanQuery.Builder lbq = new BooleanQuery.Builder();
          lbq.add(searchEngine.newPhraseQuery(field, variant.getExon()), Occur.SHOULD);
          lbq.add(searchEngine.newPhraseQuery(field, variant.getLocus()), Occur.SHOULD);
          gbq.add(lbq.build(), Occur.MUST);
        } else {
          gbq.add(searchEngine.newPhraseQuery(field, variant.getLocus()), Occur.MUST);
        }
      } else {
        final BooleanQuery.Builder mbq = new BooleanQuery.Builder();
        for (String mutation : variant.getMutation().getSynonyms()) {
          mbq.add(searchEngine.newPhraseQuery(field, mutation), Occur.SHOULD);
        }
        gbq.add(mbq.build(), Occur.MUST);
      }
      bq.add(gbq.build(), Occur.SHOULD);
    }
    return bq.build();
  }

  @Override
  public Query getQuery(AnalyzedTopic topic) {
    final BooleanQuery.Builder obq = new BooleanQuery.Builder();

    BooleanQuery.Builder ibq = new BooleanQuery.Builder();
    ibq.add(getPhaseTrialQuery(), Occur.MUST);
    ibq.add(getTumorInTitleQuery(topic), Occur.MUST);
    ibq.add(getGenesInTitleQuery(topic), Occur.MUST);
    ibq.add(getSpecificMutationsInFieldQuery(topic, "text"), Occur.MUST);
    ibq.add(searchEngine.newPhraseQuery("text", "wild"), Occur.MUST_NOT);
    obq.add(new BoostQuery(ibq.build(), 100f), Occur.SHOULD);

    ibq = new BooleanQuery.Builder();
    ibq.add(getTumorInTitleQuery(topic), Occur.MUST);
    ibq.add(getSpecificMutationsInFieldQuery(topic, "text"), Occur.MUST);
    ibq.add(new WildcardQuery(new Term("text", "progno*")), Occur.MUST);
    obq.add(ibq.build(), Occur.SHOULD);

    ibq = new BooleanQuery.Builder();
    ibq.add(getTumorInTitleQuery(topic), Occur.MUST);
    ibq.add(getSpecificMutationsInFieldQuery(topic, "text"), Occur.MUST);
    ibq.add(searchEngine.newPhraseQuery("text", "target"), Occur.MUST);
    obq.add(new BoostQuery(ibq.build(), 20), Occur.SHOULD);

    ibq = new BooleanQuery.Builder();
    ibq.add(getTumorInTitleQuery(topic), Occur.MUST);
    ibq.add(getSpecificMutationsInFieldQuery(topic, "article_title"), Occur.MUST);
    ibq.add(searchEngine.newPhraseQuery("article_title", "target"), Occur.MUST);
    obq.add(new BoostQuery(ibq.build(), 20), Occur.SHOULD);

    final BooleanQuery query = obq.build();
    log.debug("Created query {}", query);
    return query;
  }
}