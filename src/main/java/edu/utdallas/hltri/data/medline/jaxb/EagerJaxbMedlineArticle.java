package edu.utdallas.hltri.data.medline.jaxb;

import static edu.utdallas.hltri.data.medline.jaxb.JaxbMedlineIndexer.flattenContent;
import static edu.utdallas.hltri.data.medline.jaxb.JaxbMedlineIndexer.flattenFormatting;

import com.google.common.base.Suppliers;
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
import org.apache.lucene.index.IndexReader;

public class EagerJaxbMedlineArticle extends JaxbMedlineArticle {
  public EagerJaxbMedlineArticle(IndexReader reader, int luceneDocId) {
    super(luceneDocId, Suppliers.ofInstance(JaxbMedlineArticle.loadCitation(reader, luceneDocId)));
  }
}
