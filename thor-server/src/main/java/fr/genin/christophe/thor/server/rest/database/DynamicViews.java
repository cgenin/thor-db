package fr.genin.christophe.thor.server.rest.database;


import fr.genin.christophe.thor.core.Collection;
import fr.genin.christophe.thor.core.DynamicView;
import fr.genin.christophe.thor.core.dynamicview.DynamicViewFilter;
import fr.genin.christophe.thor.core.dynamicview.DynamicViewSort;
import fr.genin.christophe.thor.server.dto.DynamicViewDto;
import fr.genin.christophe.thor.server.dto.SimpleCriteriaDto;
import fr.genin.christophe.thor.server.infrastructure.DatabasePort;
import io.quarkus.vertx.web.Body;
import io.quarkus.vertx.web.Param;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteBase;
import io.vavr.Value;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.function.Function;

import static fr.genin.christophe.thor.server.rest.OpenApis.TAG_DV;
import static fr.genin.christophe.thor.server.rest.RestUtils.notFound;
import static io.quarkus.vertx.web.Route.HttpMethod.*;

@ApplicationScoped
@RouteBase(path = "/api/databases/:databaseName/collections/:collectionName", produces = MediaType.APPLICATION_JSON)
public class DynamicViews {

    public static final Function<SimpleCriteriaDto, DynamicViewSort.SimpleCriteria> TO_SIMPLE_SORT = sc -> Option.of(sc.desc)
            .map(d -> new DynamicViewSort.SimpleCriteria(sc.propname, d))
            .getOrElse(() -> new DynamicViewSort.SimpleCriteria(sc.propname));
    @Inject
    DatabasePort databasePort;

    @Operation(
            summary = "List",
            description = "List all Dynamic view from an collection"
    )
    @Tag(name = TAG_DV)
    @Route(path = "/dynamic-views", methods = GET)
    public List<DynamicViewDto> list(@Param("databaseName") String databaseName, @Param("collectionName") String collectionName) {
        return databasePort.getCollection(databaseName, collectionName)
                .map(Collection::dynamicViews)
                .map(l -> l.map(DynamicViewDto::new)
                        .toJavaList()
                )
                .getOrElseThrow(notFound(databaseName, collectionName));
    }

    @Operation(
            summary = "List",
            description = "List all Dynamic view from an collection"
    )
    @Tag(name = TAG_DV)
    @Route(path = "/dynamic-views", methods = POST)
    public DynamicViewDto create(@Param("databaseName") String databaseName,
                                 @Param("collectionName") String collectionName,
                                 @Body @Valid DynamicViewDto dynamicViewDto) {
        final Collection collection = databasePort.getCollection(databaseName, collectionName).getOrElseThrow(notFound(databaseName, collectionName));
        final DynamicView dynamicView = Option.of(dynamicViewDto.options)
                .map(o -> collection.addDynamicView(dynamicViewDto.name, o))
                .getOrElse(() -> collection.addDynamicView(dynamicViewDto.name));
        final io.vavr.collection.List<DynamicViewFilter> fps = Option.of(dynamicViewDto.filterPipeline)
                .map(io.vavr.collection.List::ofAll)
                .getOrElse(io.vavr.collection.List.empty());

        final DynamicView dynamicViewWithFilter = fps.foldRight(dynamicView, (filter, dv) -> dv.applyFilter(filter));
        return new DynamicViewDto(Option.of(dynamicViewDto.simpleCriteria)
                .map(TO_SIMPLE_SORT)
                .map(dynamicViewWithFilter::applySimpleSort)
                .orElse(
                        () -> Option.of(dynamicViewDto.sortCriteria)
                                .map(sc -> io.vavr.collection.List.ofAll(sc).map(TO_SIMPLE_SORT))
                                .map(dynamicViewWithFilter::applySortCriteria)

                )
                .getOrElse(dynamicViewWithFilter)
        );
    }

    @Operation(
            summary = "Find by name",
            description = "get an Dynamic view from an collection"
    )
    @Tag(name = TAG_DV)
    @Route(path = "/dynamic-views/:dynamicViewName", methods = GET)
    public DynamicViewDto findByName(@Param("databaseName") String databaseName,
                                     @Param("collectionName") String collectionName,
                                     @Param("dynamicViewName") String dynamicViewName) {
        final Collection collection = databasePort.getCollection(databaseName, collectionName).getOrElseThrow(notFound(databaseName, collectionName));
        return collection.getDynamicView(dynamicViewName)
                .map(DynamicViewDto::new)
                .getOrElseThrow(notFound(databaseName, collectionName, dynamicViewName));
    }


    @Operation(
            summary = "Get datas",
            description = "Get datas from an Dynamic view from an collection"
    )
    @Tag(name = TAG_DV)
    @Route(path = "/dynamic-views/:dynamicViewName/data", methods = GET)
    public List<JsonObject> data(@Param("databaseName") String databaseName,
                                     @Param("collectionName") String collectionName,
                                     @Param("dynamicViewName") String dynamicViewName) {
        final Collection collection = databasePort.getCollection(databaseName, collectionName).getOrElseThrow(notFound(databaseName, collectionName));
        return collection.getDynamicView(dynamicViewName)
                .map(DynamicView::data)
                .map(Value::toJavaList)
                .getOrElseThrow(notFound(databaseName, collectionName, dynamicViewName));
    }

    @Operation(
            summary = "Count",
            description = "Count number of datas from an Dynamic view from an collection"
    )
    @Tag(name = TAG_DV)
    @Route(path = "/dynamic-views/:dynamicViewName/count", methods = GET)
    public Integer count(@Param("databaseName") String databaseName,
                                 @Param("collectionName") String collectionName,
                                 @Param("dynamicViewName") String dynamicViewName) {
        final Collection collection = databasePort.getCollection(databaseName, collectionName).getOrElseThrow(notFound(databaseName, collectionName));
        return collection.getDynamicView(dynamicViewName)
                .map(DynamicView::count)
                .getOrElseThrow(notFound(databaseName, collectionName, dynamicViewName));
    }

    @Operation(
            summary = "Apply find actions",
            description = "Apply find actions in an Dynamic view from an collection"
    )
    @Tag(name = TAG_DV)
    @Route(path = "/dynamic-views/:dynamicViewName/find", methods = POST)
    public DynamicViewDto applyFind(@Param("databaseName") String databaseName,
                                     @Param("collectionName") String collectionName,
                                     @Param("dynamicViewName") String dynamicViewName,
                                     @Body @NotBlank(message = "The body must be set.") JsonObject body) {

        final Collection collection = databasePort.getCollection(databaseName, collectionName).getOrElseThrow(notFound(databaseName, collectionName));
        return collection.getDynamicView(dynamicViewName)
                .map(dv-> dv.applyFind(body))
                .map(DynamicViewDto::new)
                .getOrElseThrow(notFound(databaseName, collectionName, dynamicViewName));
    }

    @Operation(
            summary = "Apply transform",
            description = "Apply transform on an Dynamic view from an collection"
    )
    @Tag(name = TAG_DV)
    @Route(path = "/dynamic-views/:dynamicViewName/transform/:transformName", methods = POST)
    public DynamicViewDto applyTransform(@Param("databaseName") String databaseName,
                                    @Param("collectionName") String collectionName,
                                    @Param("dynamicViewName") String dynamicViewName,
                                    @Param("transformName") String transformName) {

        final Collection collection = databasePort.getCollection(databaseName, collectionName).getOrElseThrow(notFound(databaseName, collectionName));
        return collection.getDynamicView(dynamicViewName)
                .map(dv-> {
                    dv.branchResultset(transformName);
                    return dv;
                })
                .map(DynamicViewDto::new)
                .getOrElseThrow(notFound(databaseName, collectionName, dynamicViewName));
    }

    @Operation(
            summary = "Apply simple sort",
            description = "Apply simple sort on an Dynamic view from an collection"
    )
    @Tag(name = TAG_DV)
    @Route(path = "/dynamic-views/:dynamicViewName/sort/:propertyName", methods = POST)
    public DynamicViewDto applySimpleSort(@Param("databaseName") String databaseName,
                                    @Param("collectionName") String collectionName,
                                    @Param("dynamicViewName") String dynamicViewName,
                                    @Param("propertyName") String propertyName) {

        final Collection collection = databasePort.getCollection(databaseName, collectionName).getOrElseThrow(notFound(databaseName, collectionName));
        return collection.getDynamicView(dynamicViewName)
                .map(dv-> dv.applySimpleSort(propertyName))
                .map(DynamicViewDto::new)
                .getOrElseThrow(notFound(databaseName, collectionName, dynamicViewName));
    }

    @Operation(
            summary = "Apply simple sort with desc",
            description = "Apply simple sort with desc on an Dynamic view from an collection"
    )
    @Tag(name = TAG_DV)
    @Route(path = "/dynamic-views/:dynamicViewName/sort/:propertyName/:desc", methods = POST)
    public DynamicViewDto applySimpleSort(@Param("databaseName") String databaseName,
                                    @Param("collectionName") String collectionName,
                                    @Param("dynamicViewName") String dynamicViewName,
                                    @Param("propertyName") String propertyName,
                                    @Param("desc") Boolean desc) {

        final Collection collection = databasePort.getCollection(databaseName, collectionName).getOrElseThrow(notFound(databaseName, collectionName));
        return collection.getDynamicView(dynamicViewName)
                .map(dv-> dv.applySimpleSort(propertyName, desc))
                .map(DynamicViewDto::new)
                .getOrElseThrow(notFound(databaseName, collectionName, dynamicViewName));
    }

    @Operation(
            summary = "Apply multiple sorts",
            description = "Apply multple sorts on an Dynamic view from an collection"
    )
    @Tag(name = TAG_DV)
    @Route(path = "/dynamic-views/:dynamicViewName/sort", methods = POST)
    public DynamicViewDto applySortCriteria(@Param("databaseName") String databaseName,
                                          @Param("collectionName") String collectionName,
                                          @Param("dynamicViewName") String dynamicViewName,
                                            @Body @NotBlank(message = "The body must be set.")  List<SimpleCriteriaDto> body) {

        final Collection collection = databasePort.getCollection(databaseName, collectionName).getOrElseThrow(notFound(databaseName, collectionName));
        return collection.getDynamicView(dynamicViewName)
                .map(dv-> dv.applySortCriteria( io.vavr.collection.List.ofAll(body).map(TO_SIMPLE_SORT)))
                .map(DynamicViewDto::new)
                .getOrElseThrow(notFound(databaseName, collectionName, dynamicViewName));
    }

    @Operation(
            summary = "Delete",
            description = "Remove an Dynamic view from an collection"
    )
    @Tag(name = TAG_DV)
    @Route(path = "/dynamic-views/:dynamicViewName", methods = DELETE)
    public JsonObject delete(@Param("databaseName") String databaseName,
                             @Param("collectionName") String collectionName,
                             @Param("dynamicViewName") String dynamicViewName) {
        final Collection collection = databasePort.getCollection(databaseName, collectionName).getOrElseThrow(notFound(databaseName, collectionName));
        collection.removeDynamicView(dynamicViewName);
        return new JsonObject();
    }

    @Operation(
            summary = "Get filters",
            description = "Get filters from an Dynamic view from an collection"
    )
    @Tag(name = TAG_DV)
    @Route(path = "/dynamic-views/:dynamicViewName/find", methods = GET)
    public List<DynamicViewFilter> getFilters(@Param("databaseName") String databaseName,
                                         @Param("collectionName") String collectionName,
                                         @Param("dynamicViewName") String dynamicViewName) {

        final Collection collection = databasePort.getCollection(databaseName, collectionName).getOrElseThrow(notFound(databaseName, collectionName));
        return collection.getDynamicView(dynamicViewName)
                .map(DynamicView::filterPipeline)
                .map(Set::toJavaList)
                .getOrElseThrow(notFound(databaseName, collectionName, dynamicViewName));
    }

    @Operation(
            summary = "Remove all filters",
            description = "Remove all filters from an Dynamic view from an collection"
    )
    @Tag(name = TAG_DV)
    @Route(path = "/dynamic-views/:dynamicViewName/find", methods = DELETE)
    public DynamicViewDto removeFilters(@Param("databaseName") String databaseName,
                                              @Param("collectionName") String collectionName,
                                              @Param("dynamicViewName") String dynamicViewName) {

        final Collection collection = databasePort.getCollection(databaseName, collectionName).getOrElseThrow(notFound(databaseName, collectionName));
        return collection.getDynamicView(dynamicViewName)
                .map(DynamicView::removeFilters)
                .map(DynamicViewDto::new)
                .getOrElseThrow(notFound(databaseName, collectionName, dynamicViewName));
    }

    @Operation(
            summary = "get an filter",
            description = "get an filter by uid from an Dynamic view from an collection"
    )
    @Tag(name = TAG_DV)
    @Route(path = "/dynamic-views/:dynamicViewName/find/:uid", methods = GET)
    public DynamicViewFilter getFilterByUid(@Param("databaseName") String databaseName,
                                        @Param("collectionName") String collectionName,
                                        @Param("dynamicViewName") String dynamicViewName,
                                        @Param("uid") String uid) {
        final Collection collection = databasePort.getCollection(databaseName, collectionName).getOrElseThrow(notFound(databaseName, collectionName));
        return collection.getDynamicView(dynamicViewName)
                .map(DynamicView::filterPipeline)
                .flatMap(fs->fs.find(dvf-> dvf.getUid().equals(uid)))
                .getOrElseThrow(notFound(databaseName, collectionName, dynamicViewName, uid));
    }

    @Operation(
            summary = "Remove an filter",
            description = "Remove an filter by uid from an Dynamic view from an collection"
    )
    @Tag(name = TAG_DV)
    @Route(path = "/dynamic-views/:dynamicViewName/find/:uid", methods = DELETE)
    public DynamicViewDto removeFilterByUid(@Param("databaseName") String databaseName,
                                            @Param("collectionName") String collectionName,
                                            @Param("dynamicViewName") String dynamicViewName,
                                            @Param("uid") String uid
                                            ) {

        final Collection collection = databasePort.getCollection(databaseName, collectionName).getOrElseThrow(notFound(databaseName, collectionName));
        return collection.getDynamicView(dynamicViewName)
                .map(dv-> dv.removeFilter(uid))
                .map(DynamicViewDto::new)
                .getOrElseThrow(notFound(databaseName, collectionName, dynamicViewName, uid));
    }
}
