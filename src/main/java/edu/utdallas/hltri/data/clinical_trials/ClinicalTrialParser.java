package edu.utdallas.hltri.data.clinical_trials;

import com.fasterxml.aalto.stax.InputFactoryImpl;

import edu.utdallas.hltri.data.clinical_trials.jaxb.ClinicalStudy;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;

import java.nio.file.Path;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;

public class ClinicalTrialParser {
  private final ThreadLocal<XMLInputFactory2> factory;
  private final ThreadLocal<Unmarshaller>     unmarshaller;

  public ClinicalTrialParser() {
    this.factory = ThreadLocal.withInitial(InputFactoryImpl::new);
    this.factory.get().configureForSpeed();
    this.unmarshaller = ThreadLocal.withInitial(() -> {
      try {
        final JAXBContext context = JAXBContext.newInstance(ClinicalStudy.class);
        return context.createUnmarshaller();
      } catch (JAXBException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public ClinicalStudy parseXmlFile(Path path) {
    try {
      final XMLStreamReader2 reader = factory.get().createXMLStreamReader(path.toFile());
      final ClinicalStudy study = unmarshaller.get().unmarshal(reader, ClinicalStudy.class).getValue();
      reader.closeCompletely();
      return study;
    } catch (XMLStreamException | JAXBException e) {
      throw new RuntimeException(e);
    }
  }
}
