package fr.genin.christophe.thor.core;

import fr.genin.christophe.thor.core.options.ThorOptions;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ThorTest {

  private Thor thor;
  private Infrastructure infrastructure;

  @BeforeEach
  public void before() {
    infrastructure = mock(Infrastructure.class);

    thor = new Thor(infrastructure, new ThorOptions());
  }

  @Test
  public void should_getCollection() {
    final Collection collection = thor.addCollection("test");
    assertThat(collection).isNotNull();
    assertThat(thor.getCollection("test").isDefined()).isTrue();
    assertThat(thor.getCollection("t").isEmpty()).isTrue();
    thor.removeCollection("test");
    assertThat(thor.getCollection("test").isEmpty()).isTrue();
  }

  @Test
  public void should_renameCollection() {
    thor.addCollection("test");
    thor.renameCollection("test", "t");

    assertThat(thor.getCollection("test").isEmpty()).isTrue();
    assertThat(thor.getCollection("t").isDefined()).isTrue();
    assertThat(thor.collections()).hasSize(1);

    thor.renameCollection("nonExistent", "ddd");
  }

  @Test
  public void should_saveDatabase() {
    when(infrastructure.save(any())).thenReturn(Future.successful(true));
    final Try<Boolean> future = thor.saveDatabase();
    assertThat(future.get()).isTrue();
  }


  @Test
  public void should_serialize() {
    thor.addCollection("test");
    assertThat(thor.serialize()).isNotEmpty().isNotNull();
  }
}
