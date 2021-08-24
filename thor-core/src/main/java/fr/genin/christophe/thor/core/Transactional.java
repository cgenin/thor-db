package fr.genin.christophe.thor.core;

import java.io.Serializable;

public interface Transactional extends Serializable {


  static Transactional build(boolean transactional) {
    if (transactional) {
      return new TransactionImpl();
    }
    return new Transactional() {
    };
  }

  default void startTransaction(Collection c) {
  }

  default void rollback(Collection c) {
  }

  default void commit(Collection c) {
  }


  default Transactional copy() {
    return new Transactional() {
    };
  }
}
