package pl.rufio.Ollama;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ollama4j.tools.Tools;
import pl.rufio.Tools.Tool;

import java.util.Map;

public class Ollama4jToolAdapter {

    private final ObjectMapper mapper = new ObjectMapper();

    public Tools.Tool adapt(Tool tool) {
        Tools.ToolSpec specification = Tools.ToolSpec.builder()
                .name(tool.getName())
                .description(tool.getDescription())
                .parameters(new Tools.Parameters(
                        Map.of(),
                        new java.util.ArrayList<>()
                ))
                .build();

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
}
