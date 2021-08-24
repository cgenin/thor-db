package fr.genin.christophe.thor.server.dto;

import fr.genin.christophe.thor.core.Transform;
import io.vertx.core.json.JsonObject;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

public class TransformDto {
  //  @NotBlank(message = "Name is required")
    public  String name;
  //  @NotBlank(message = "One operation is required")
    public List<Map<String, Object>> operations;

    public TransformDto() {
    }

    public TransformDto(Transform transform){
        name = transform.name;
        operations = transform.operations
                .map(JsonObject::getMap)
                .toJavaList();
    }
}
