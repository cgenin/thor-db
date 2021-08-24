package fr.genin.christophe.thor.core.options;

public class ResultSetDataOptions {
  private boolean forceClones = false;
  private boolean removeMeta = false;

  public boolean isForceClones() {
    return forceClones;
  }

  public ResultSetDataOptions setForceClones(boolean forceClones) {
    this.forceClones = forceClones;
    return this;
  }


  public boolean isRemoveMeta() {
    return removeMeta;
  }

  public ResultSetDataOptions setRemoveMeta(boolean removeMeta) {
    this.removeMeta = removeMeta;
    return this;
  }
}
