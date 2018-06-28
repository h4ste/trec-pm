package edu.utdallas.hltri.data.medline.jaxb;

import edu.utdallas.hltri.util.Lazy;
import io.protostuff.GraphIOUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import java.util.Arrays;
import org.apache.lucene.index.IndexReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import edu.utdallas.hltri.data.medline.MedlineArticle;
import edu.utdallas.hltri.data.medline.jaxb.struct.Author;
import edu.utdallas.hltri.data.medline.jaxb.struct.DataBankList;
import edu.utdallas.hltri.data.medline.jaxb.struct.DateCompleted;
import edu.utdallas.hltri.data.medline.jaxb.struct.DateCreated;
import edu.utdallas.hltri.data.medline.jaxb.struct.DateRevised;
import edu.utdallas.hltri.data.medline.jaxb.struct.DescriptorName;
import edu.utdallas.hltri.data.medline.jaxb.struct.Grant;
import edu.utdallas.hltri.data.medline.jaxb.struct.GrantList;
import edu.utdallas.hltri.data.medline.jaxb.struct.Investigator;
import edu.utdallas.hltri.data.medline.jaxb.struct.MedlineCitation;
import edu.utdallas.hltri.data.medline.jaxb.struct.MeshHeading;
import edu.utdallas.hltri.data.medline.jaxb.struct.MeshHeadingList;
import edu.utdallas.hltri.data.medline.jaxb.struct.PublicationType;
import edu.utdallas.hltri.data.medline.jaxb.struct.PubmedArticle;
import edu.utdallas.hltri.data.medline.jaxb.struct.QualifierName;
import edu.utdallas.hltri.inquire.lucene.LuceneUtils;
import edu.utdallas.hltri.logging.Logger;
import org.apache.lucene.util.BytesRef;

import static edu.utdallas.hltri.data.medline.jaxb.JaxbMedlineIndexer.flattenContent;
import static edu.utdallas.hltri.data.medline.jaxb.JaxbMedlineIndexer.flattenFormatting;

public class JaxbMedlineArticle implements MedlineArticle {
  private static final Logger log = Logger.get(JaxbMedlineArticle.class);

  private final int luceneDocId;
  private final Supplier<MedlineCitation> article;

  private static final Schema<PubmedArticle> schema = RuntimeSchema.getSchema(PubmedArticle.class);

  public JaxbMedlineArticle(int luceneDocId, Supplier<MedlineCitation> article) {
    this.luceneDocId = luceneDocId;
    this.article = article;
  }

  protected static MedlineCitation loadCitation(IndexReader reader, int luceneDocId) {
    final BytesRef bytes = LuceneUtils.getBinaryDocValue(reader, "bytes", luceneDocId);
    final PubmedArticle article = schema.newMessage();
    try {
      GraphIOUtil.mergeFrom(bytes.bytes, bytes.offset, bytes.length, article, schema);
    } catch (RuntimeException e) {
      log.error("Failed to read doc {}: {}", luceneDocId, new String(bytes.bytes, bytes.offset, bytes.length));
      throw e;
    }
    return article.getMedlineCitation();
  }

  protected  <O> Optional<O> tryOptionally(Function<MedlineCitation, O> func) {
    try {
      return Optional.ofNullable(func.apply(article.get()));
    } catch (NullPointerException e) {
      return Optional.empty();
    }
  }

  @Override
  public String getPubmedId() {
    return article.get().getPMID().getValue();
  }

  @Override
  public String getJournalTitle() {
    return article.get().getMedlineJournalInfo().getMedlineTA();
  }

  @Override
  public String getArticleTitle() {
    return flattenContent(article.get().getArticle().getArticleTitle().getContent());
  }

  @Override
  public String getAbstractText() {
    return article.get().getArticle().getAbstract().getAbstractTexts().stream()
        .flatMap(at -> flattenFormatting(at.getContent().stream()))
        .collect(Collectors.joining());
  }

  @Override
  public List<String> getPublicationTypes() {
    try {
      return article.get().getArticle().getPublicationTypes().getPublicationTypes().stream()
          .map(PublicationType::getValue)
          .collect(Collectors.toList());
    } catch (NullPointerException npe) {
      return Collections.emptyList();
    }
  }

  @Override
  public List<String> getChemicals() {
    try {
      return article.get().getChemicalList().getChemicals().stream()
          .map(c -> c.getNameOfSubstance().getValue())
          .collect(Collectors.toList());
    } catch (NullPointerException npe) {
      return Collections.emptyList();
    }
  }

  @Override
  public List<String> getMeshTerms() {
    final List<String> headings = new ArrayList<>();
    final MeshHeadingList meshHeadingList = article.get().getMeshHeadingList();
    if (meshHeadingList != null) {
      for (MeshHeading meshHeading : meshHeadingList.getMeshHeadings()) {
        final DescriptorName descriptor = meshHeading.getDescriptorName();
        if (descriptor.getMajorTopicYN().equals("Y")) {
          headings.add(descriptor.getValue());
        } else {
          for (QualifierName qualifierName : meshHeading.getQualifierNames()) {
            if (qualifierName.getMajorTopicYN().equals("Y")) {
              headings.add(descriptor.getValue() + "/" + qualifierName);
            }
          }
        }
      }
    }
    return headings;
  }

  public List<String> getGrantIds() {
    final List<String> grants = new ArrayList<>();
    final GrantList grantList = article.get().getArticle().getGrantList();
    if (grantList != null) {
      for (Grant grant : grantList.getGrants()) {
        final String grantId = grant.getGrantID();
        if (grantId != null) {
          grants.add(grant.getGrantID());
        }
      }
    }
    return grants;
  }

  @Override
  public LocalDate getCreationDate() {
    final DateCreated date = article.get().getDateCreated();
    final int year = Integer.parseInt(date.getYear());
    final int month = Integer.parseInt(date.getMonth());
    final int day = Integer.parseInt(date.getDay());
    return LocalDate.of(year, Month.of(month), day);
  }

  @Override
  public LocalDate getCompletionDate() {
    final DateCompleted date = article.get().getDateCompleted();
    final int year = Integer.parseInt(date.getYear());
    final int month = Integer.parseInt(date.getMonth());
    final int day = Integer.parseInt(date.getDay());
    return LocalDate.of(year, Month.of(month), day);
  }

  @Override
  public LocalDate getRevisionDate() {
    final DateRevised date = article.get().getDateRevised();
    final int year = Integer.parseInt(date.getYear());
    final int month = Integer.parseInt(date.getMonth());
    final int day = Integer.parseInt(date.getDay());
    return LocalDate.of(year, Month.of(month), day);
  }

  @Override
  public Map<String, String> getAbstractTexts() {
    throw new UnsupportedOperationException();
  }

  public List<Author> getAuthors() {
    return tryOptionally(article -> article.getArticle().getAuthorList().getAuthors())
        .orElse(Collections.emptyList())
        .stream()
        .filter(a -> a.getValidYN().equals("Y"))
        .collect(Collectors.toList());
  }

  public List<Investigator> getInvestigators() {
    return tryOptionally(article -> article.getInvestigatorList().getInvestigators())
        .orElse(Collections.emptyList())
        .stream()
        .filter(a -> a.getValidYN().equals("Y"))
        .collect(Collectors.toList());
  }

  public List<String> getRegisteredNctIds() {
    final DataBankList dataBankList = article.get().getArticle().getDataBankList();
    if (dataBankList != null ) {
      return dataBankList.getDataBanks().stream()
          .filter(db -> db.getDataBankName().equals("ClinicalTrials.gov"))
          .flatMap(db -> db.getAccessionNumberList().getAccessionNumbers().stream())
          .collect(Collectors.toList());
    } else {
      return Collections.emptyList();
    }
  }

  public int getLuceneDocId() {
    return luceneDocId;
  }
}
