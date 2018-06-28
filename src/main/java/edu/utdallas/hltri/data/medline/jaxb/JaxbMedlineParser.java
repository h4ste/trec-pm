package edu.utdallas.hltri.data.medline.jaxb;

import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.google.common.collect.Streams;
import edu.utdallas.hltri.data.medline.jaxb.struct.PubmedArticle;
import edu.utdallas.hltri.data.medline.jaxb.struct.PubmedArticleSet;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.codehaus.stax2.XMLInputFactory2;

class JaxbMedlineParser {
  private final XMLInputFactory2 factory;
  private final Unmarshaller     unmarshaller;

  JaxbMedlineParser() {
    this.factory = new InputFactoryImpl();
    factory.configureForSpeed();

    try {
      final JAXBContext context = JAXBContext.newInstance("edu.utdallas.hltri.data.medline.jaxb.struct");
      this.unmarshaller = context.createUnmarshaller();
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
  }

  List<PubmedArticle> parseXmlFile(final InputStream source) {
    try {
      final XMLStreamReader reader = factory.createXMLStreamReader(source);
      final PubmedArticleSet articleSet = unmarshaller.unmarshal(reader, PubmedArticleSet.class).getValue();
      reader.close();
      return articleSet.getPubmedArticlesAndPubmedBookArticles().stream()
          .filter(PubmedArticle.class::isInstance).map(PubmedArticle.class::cast)
          .collect(Collectors.toList());
    } catch (XMLStreamException | JAXBException e) {
      throw new RuntimeException(e);
    }
  }


  Stream<PubmedArticle> streamArticles(final InputStream source) {
    try {
      return Streams.stream(new Iterator<PubmedArticle>() {
        final XMLStreamReader reader = factory.createXMLStreamReader(source);

        @Override
        public boolean hasNext() {
          try {
            while (reader.hasNext()) {
              reader.next();
              if (reader.getEventType() == XMLStreamConstants.START_ELEMENT &&
                  reader.getLocalName().equals("PubmedArticle")) {
                return true;
              }
            }
            reader.close();
            return false;
          } catch (XMLStreamException e) {
            throw new RuntimeException(e);
          }
        }

        @Override
        public PubmedArticle next() {
          try {
            return unmarshaller.unmarshal(reader, PubmedArticle.class).getValue();
          } catch (JAXBException e) {
            throw new RuntimeException(e);
          }
        }
      });
    } catch (XMLStreamException e) {
      throw new RuntimeException(e);
    }
  }
}