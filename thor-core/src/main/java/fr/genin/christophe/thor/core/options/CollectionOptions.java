package fr.genin.christophe.thor.core.options;


import java.util.List;

public class CollectionOptions {
  private boolean disableMeta = false;
  private boolean asyncListeners = false;
  private boolean disableDeltaChangesApi = true;
  private boolean disableChangesApi = true;
  private boolean autoupdate = false;
  private boolean disableFreeze = false;
  private boolean clone = false;
  private boolean removeIndices = false;
  private Long ttl;
  private Integer ttlInterval;
  private List<String> unique;
  private List<String> exact;
  private List<String> indices;

  public boolean isDisableMeta() {
    return disableMeta;
  }

  public CollectionOptions setDisableMeta(boolean disableMeta) {
    this.disableMeta = disableMeta;
    return this;
  }

  public boolean isAsyncListeners() {
    return asyncListeners;
  }

  public CollectionOptions setAsyncListeners(boolean asyncListeners) {
    this.asyncListeners = asyncListeners;
    return this;
  }

  public boolean isDisableDeltaChangesApi() {
    return disableDeltaChangesApi;
  }

  public CollectionOptions setDisableDeltaChangesApi(boolean disableDeltaChangesApi) {
    this.disableDeltaChangesApi = disableDeltaChangesApi;
    return this;
  }

  public boolean isDisableChangesApi() {
    return disableChangesApi;
  }

  public CollectionOptions setDisableChangesApi(boolean disableChangesApi) {
    this.disableChangesApi = disableChangesApi;
    return this;
  }

  public boolean isAutoupdate() {
    return autoupdate;
  }

  public CollectionOptions setAutoupdate(boolean autoupdate) {
    this.autoupdate = autoupdate;
    return this;
  }

  public boolean isClone() {
    return clone;
  }

  public CollectionOptions setClone(boolean clone) {
    this.clone = clone;
    return this;
  }

  public Long getTtl() {
    return ttl;
  }

  public CollectionOptions setTtl(Long ttl) {
    this.ttl = ttl;
    return this;
  }

  public Integer getTtlInterval() {
    return ttlInterval;
  }

  public CollectionOptions setTtlInterval(Integer ttlInterval) {
    this.ttlInterval = ttlInterval;
    return this;
  }

  public List<String> getUnique() {
    return unique;
  }

  public CollectionOptions setUnique(List<String> unique) {
    this.unique = unique;
    return this;
  }

  public List<String> getExact() {
    return exact;
  }

  public CollectionOptions setExact(List<String> exact) {
    this.exact = exact;
    return this;
  }

  public List<String> getIndices() {
    return indices;
  }

  public CollectionOptions setIndices(List<String> indices) {
    this.indices = indices;
    return this;
  }

  public boolean isDisableFreeze() {
    return disableFreeze;
  }

  public CollectionOptions setDisableFreeze(boolean disableFreeze) {
    this.disableFreeze = disableFreeze;
    return this;
  }

  public boolean isRemoveIndices() {
    return removeIndices;
  }

  public CollectionOptions setRemoveIndices(boolean removeIndices) {
    this.removeIndices = removeIndices;
    return this;
  }
}
