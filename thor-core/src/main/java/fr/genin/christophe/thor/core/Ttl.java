package fr.genin.christophe.thor.core;

import java.util.Objects;

public class Ttl {
  private Long age;
  private Integer ttlInterval;
  private Long daemon;

  public Ttl copy() {
    final Ttl ttl = new Ttl();
    ttl.age = age;
    ttl.ttlInterval = ttlInterval;
    ttl.daemon = daemon;
    return ttl;
  }

  public Long getAge() {
    return age;
  }

  public Ttl setAge(Long age) {
    this.age = age;
    return this;
  }

  public Integer getTtlInterval() {
    return ttlInterval;
  }

  public Ttl setTtlInterval(Integer ttlInterval) {
    this.ttlInterval = ttlInterval;
    return this;
  }

  public Long getDaemon() {
    return daemon;
  }

  public Ttl setDaemon(Long daemon) {
    this.daemon = daemon;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Ttl ttl = (Ttl) o;
    return Objects.equals(age, ttl.age) && Objects.equals(ttlInterval, ttl.ttlInterval) && Objects.equals(daemon, ttl.daemon);
  }

  @Override
  public int hashCode() {
    return Objects.hash(age, ttlInterval, daemon);
  }
}
