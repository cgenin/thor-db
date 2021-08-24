package fr.genin.christophe.thor.core.options;

import java.io.Serializable;

public class DynamicViewOption implements Serializable {
  private boolean persistent;
  private Integer minRebuildInterval;

  public boolean isPersistent() {
    return persistent;
  }

  public DynamicViewOption setPersistent(boolean persistent) {
    this.persistent = persistent;
    return this;
  }



  public Integer getMinRebuildInterval() {
    return minRebuildInterval;
  }

  public DynamicViewOption setMinRebuildInterval(Integer minRebuildInterval) {
    this.minRebuildInterval = minRebuildInterval;
    return this;
  }


}
