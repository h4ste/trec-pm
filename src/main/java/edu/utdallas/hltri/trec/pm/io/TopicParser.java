package edu.utdallas.hltri.trec.pm.io;

import com.fasterxml.aalto.sax.SAXParserFactoryImpl;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import edu.utdallas.hltri.trec.pm.Topics;

/**
 * Created by travis on 6/7/17.
 */
public class TopicParser {
  private final SAXParserFactory  parserFactory = new SAXParserFactoryImpl();

  public Topics parse(Path path) {
    try {
      final SAXParser saxParser = parserFactory.newSAXParser();
      final TopicXmlHandler handler = new TopicXmlHandler();
      saxParser.parse(new InputSource(Files.newBufferedReader(path)), handler);
      return handler.getTopics();
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new RuntimeException(e);
    }
  }
}
