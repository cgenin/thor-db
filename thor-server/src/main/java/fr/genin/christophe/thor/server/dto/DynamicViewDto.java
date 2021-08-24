package fr.genin.christophe.thor.server.dto;

import fr.genin.christophe.thor.core.DynamicView;
import fr.genin.christophe.thor.core.dynamicview.DynamicViewFilter;
import fr.genin.christophe.thor.core.dynamicview.DynamicViewSort;
import fr.genin.christophe.thor.core.options.DynamicViewOption;
import io.vertx.core.json.JsonObject;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.function.Function;

public class DynamicViewDto {

    public static final Function<DynamicViewSort.SimpleCriteria, SimpleCriteriaDto> TO_SIMPLE_CRITERIA = sc -> {
        final SimpleCriteriaDto simpleCriteriaDto = new SimpleCriteriaDto();
        simpleCriteriaDto.propname = sc.propname;
        simpleCriteriaDto.desc = sc.desc;
        return simpleCriteriaDto;
    };
    @NotBlank(message = "The name of the view is required.")
    public String name;
    public DynamicViewOption options;
    public List<JsonObject> resultdata;
    public List<DynamicViewFilter> filterPipeline;
    public SimpleCriteriaDto simpleCriteria;
    public List<SimpleCriteriaDto> sortCriteria;

    public DynamicViewDto() {
    }

    public DynamicViewDto(DynamicView dv) {

        this.name = dv.name;
        this.options = dv.options();
        this.resultdata = dv.data().toJavaList();
        this.filterPipeline = dv.filterPipeline().toJavaList();
        this.simpleCriteria = dv.sorting()
                .simpleCriteria()
                .map(TO_SIMPLE_CRITERIA)
                .getOrNull();
        this.sortCriteria = dv.sorting()
                .sortCriteria()
                .map(s-> s.criterias
                        .map(TO_SIMPLE_CRITERIA)
                        .toJavaList()
                )
                .getOrNull();

    }
}
