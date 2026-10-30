package pl.rufio.Ollama;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ollama4j.tools.Tools;
import pl.rufio.Tools.Tool;

import java.util.*;

public class Ollama4jToolAdapter {
    private final ObjectMapper mapper = new ObjectMapper();

    public Tools.Tool adapt(Tool tool){
        Tools.ToolSpec specification = null;
        try{
             specification = Tools.ToolSpec.builder()
                    .name(tool.getName())
                    .description(tool.getDescription())
                    .parameters(convertParameters(tool.getParametersSchema()))
                    .build();
        } catch (Exception e) {
            System.err.println("Nie mam pojęcia co się stało");
            System.exit(7348743);
        }

        return Tools.Tool.builder()
                .toolSpec(specification)
                .toolFunction(arguments -> {
                    try {
                        String json = mapper.writeValueAsString(arguments);
                        return tool.execute(json);
                    } catch (Exception e) {
                        return "Tool execution failed: " + e.getMessage();
                    }
                })
                .build();
    }
    private Tools.Parameters convertParameters(String schema) throws Exception {
        JsonNode root = mapper.readTree(schema);

        JsonNode properties = root.get("properties");
        JsonNode required = root.get("required");

        Map<String, Tools.Property> propertiesMap = new LinkedHashMap<>();
        List<String> requiredList = new ArrayList<>();

        if (properties != null && properties.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields =
                    properties.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();

                String name = field.getKey();
                JsonNode definition = field.getValue();

                String type = definition.path("type").asText("string");
                String description = definition.path("description").asText("");

                Tools.Property property = Tools.Property.builder()
                        .type(type)
                        .description(description)
                        .build();

                propertiesMap.put(name, property);
            }
        }

        if (required != null && required.isArray()) {
            for (JsonNode item : required) {
                if (item.isTextual()) {
                    requiredList.add(item.asText());
                }
            }
        }

        return new Tools.Parameters(propertiesMap, requiredList);
    }
}
