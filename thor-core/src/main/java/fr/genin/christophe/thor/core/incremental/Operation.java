package fr.genin.christophe.thor.core.incremental;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Operation {
  Update("U"),
  Insert("I");

  private final static Logger LOG = LoggerFactory.getLogger(Operation.class);


  public final String code;

  Operation(String code) {
    this.code = code;
  }

  public static Operation fromCode(String code) {
    for (Operation v : values()) {
      if (v.code.equals(code)) {
        return v;
      }
    }
    LOG.warn("code not found " + code);
    return null;
  }
}
