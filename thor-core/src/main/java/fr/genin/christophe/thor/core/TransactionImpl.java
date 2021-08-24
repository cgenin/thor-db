package fr.genin.christophe.thor.core;

import fr.genin.christophe.thor.core.index.Index;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vertx.core.json.JsonObject;

import java.io.Serializable;

public class TransactionImpl implements Transactional {

  private CachedCollection cached;

  public void startTransaction(Collection c) {
      this.cached = new CachedCollection(c.data(), c.getIdIndex(), c.getBinaryIndices(), c.getDirtyIds());
      c.dynamicViews().forEach(DynamicView::startTransaction);

  }



  public void rollback(Collection collection) {
      Option.of(this.cached)
        .peek(c -> {
          collection.setData(c.cachedData)
          .setIdIndex(c.cachedIndex)
          .setBinaryIndices(c.cachedBinaryIndex)
          .setDirtyIds(c.cachedDirtyIds);
          collection.dynamicViews().forEach(DynamicView::rollback);
        });
  }

  public void commit(Collection c) {
      this.cached = null;
      c.dynamicViews().forEach(DynamicView::commit);
  }

  private static class CachedCollection implements Serializable {
    private final List<JsonObject> cachedData;
    private final List<Long> cachedIndex;
    private final List<Index> cachedBinaryIndex;
    private final List<Long> cachedDirtyIds;


    private CachedCollection(List<JsonObject> data, List<Long> cachedIndex, List<Index> cachedBinaryIndex, List<Long> cachedDirtyIds) {
      this.cachedData = data.map(JsonObject::copy);
      this.cachedIndex = cachedIndex;
      this.cachedBinaryIndex = cachedBinaryIndex;
      this.cachedDirtyIds = cachedDirtyIds;
    }
  }
}
