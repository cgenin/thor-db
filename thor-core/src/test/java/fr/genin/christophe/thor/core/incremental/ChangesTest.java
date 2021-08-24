package fr.genin.christophe.thor.core.incremental;

import fr.genin.christophe.thor.core.incremental.Change;
import fr.genin.christophe.thor.core.incremental.Changes;
import fr.genin.christophe.thor.core.incremental.Operation;
import fr.genin.christophe.thor.core.options.CollectionOptions;
import io.vavr.collection.List;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangesTest {

  private Changes changes;

  @BeforeEach
  public void before() {
    changes = new Changes(new CollectionOptions());
  }

  @Test
  public void should_test_createUpdateChange() {
    final JsonObject a = new JsonObject().put("a", 1);
    final JsonObject a2 = new JsonObject().put("a", 2);
    changes.createUpdateChange("test", a, a2);
    final List<Change> l = changes.get();
    assertThat(l).hasSize(1);
    final Change change = l.get(0);
    assertThat(change.name).isEqualTo("test");
    assertThat(change.operation).isEqualTo(Operation.Update);
    assertThat(change.obj).isEqualTo(a);
  }

  @Test
  public void should_test_createInsertChange() {
    final JsonObject a = new JsonObject().put("a", 1);
    changes.createInsertChange("test", a);
    final List<Change> l = changes.get();
    assertThat(l).hasSize(1);
    final Change change = l.get(0);
    assertThat(change.name).isEqualTo("test");
    assertThat(change.operation).isEqualTo(Operation.Insert);
    assertThat(change.obj).isEqualTo(a);

    final Change c2 = Change.from(Change.to(change));
    assertThat(c2).isEqualTo(change);
  }

  @Test
  public void should_test_delta() {
    final JsonObject a = new JsonObject().put("a", 1);
    assertThat(changes.getChangeDelta(a, null)).isEqualTo(a);
    assertThat(changes.getChangeDelta(null, a)).isEqualTo(a);
    assertThat(changes.getChangeDelta(null, null)).isEqualTo(new JsonObject());

    final JsonObject a2 = new JsonObject().put("a", 2);
    assertThat(changes.getChangeDelta(a, a2)).isEqualTo(a);
    final JsonObject b2 = new JsonObject().put("b", 2);
    assertThat(changes.getChangeDelta(a, b2)).isEqualTo(new JsonObject().put("a", 1).put("b", 2));

    final JsonObject b3 = new JsonObject().put("a", 1).put("b", 2);
    assertThat(changes.getChangeDelta(a, b3)).isEqualTo(new JsonObject().put("b", 2));

    final JsonObject new1 = new JsonObject().put("a", 2).put("c", new JsonObject().put("d", "obiwan"));
    final JsonObject old1 = new JsonObject().put("a", 2).put("c", new JsonObject().put("d", "kenodi"));

    assertThat(changes.getChangeDelta(new1, new1)).isEqualTo(new JsonObject());
    assertThat(changes.getChangeDelta(new1, old1)).isEqualTo(new JsonObject().put("c", new JsonObject().put("d", "obiwan")));

    assertThat(changes.getChangeDelta(a, old1)).isEqualTo(new JsonObject().put("a", 1).put("c", new JsonObject().put("d", "kenodi")));
  }
}
