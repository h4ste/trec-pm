package edu.utdallas.hltri.trec.pm.io.html;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

import edu.utdallas.hltri.logging.Logger;

/**
 * This entire class is to accommodate the insanity of the Velocity maintainers
 *
 */
public class HltriLoggingChute implements LogChute {
  private static final Logger log = Logger.get("org.apache.velocity.Velocity");

  private enum Level {
    TRACE, DEBUG, INFO, WARN, ERROR
  }

  @Override
  public void init(RuntimeServices rs) throws Exception {
    // do nothing
  }

  private Level getLevel(int level) {
    assert level >= -1 && level <= 3: "Encountered impossible logging level: " + level;
    return Level.values()[level + 1];
  }

  @Override
  public void log(int level, String message) {
    switch (getLevel(level)) {
      case TRACE:
        log.trace(message);
        break;
      case DEBUG:
        log.debug(message);
        break;
      case INFO:
        log.info(message);
        break;
      case WARN:
        log.warn(message);
        break;
      case ERROR:
        log.error(message);
        break;
    }
  }

  @Override
  public void log(int level, String message, Throwable t) {
    switch (getLevel(level)) {
      case TRACE:
        log.trace(message, t);
        break;
      case DEBUG:
        log.debug(message, t);
        break;
      case INFO:
        log.info(message, t);
        break;
      case WARN:
        log.warn(message, t);
        break;
      case ERROR:
        log.error(message, t);
        break;
    }
  }

  @Override
  public boolean isLevelEnabled(int level) {
    switch (getLevel(level)) {
      case TRACE:
        return log.isTraceEnabled();
      case DEBUG:
        return log.isDebugEnabled();
      case INFO:
        return log.isInfoEnabled();
      case WARN:
        return log.isWarnEnabled();
      case ERROR:
        return log.isErrorEnabled();
      default:
        throw new RuntimeException("We hit an unreachable statement!");
    }
  }
}
