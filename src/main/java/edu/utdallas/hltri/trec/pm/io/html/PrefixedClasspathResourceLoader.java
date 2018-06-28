package edu.utdallas.hltri.trec.pm.io.html;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.InputStream;
import java.nio.file.Paths;

import edu.utdallas.hltri.logging.Logger;

public class PrefixedClasspathResourceLoader  extends ClasspathResourceLoader {
  private static final Logger log    = Logger.get(PrefixedClasspathResourceLoader.class);

  /** Prefix to be added to any names */
  private              String prefix = "";

  @Override
  public void init(ExtendedProperties configuration) {
    prefix = configuration.getString("prefix","");
    log.debug("Setting Velocity template base to {}", prefix);
  }

  @Override
  public InputStream getResourceStream(String name) throws ResourceNotFoundException {
    final String path = Paths.get(prefix).resolve(name).toString();
    return super.getResourceStream(path);
  }
}
