package pl.rufio.Tools.packageWithTools.files;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.rufio.Tools.Tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CreateFileTool implements Tool {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Path workspace = Path.of(
            System.getProperty("user.home"),
            "Jarvis",
            "workspace"
    ).toAbsolutePath().normalize();

    @Override
    public String getName() {
        return "create_file";
    }

    @Override
    public String getDescription() {
        return "Creates a new file in the allowed workspace.";
    }

    @Override
    public String getParametersSchema() {
        return """
                {
                    "type": "object",
                    "properties": {
                        "path": {
                            "type": "string",
                            "description": "Relative path of the file to create"
                        },
                        "content": {
                            "type": "string",
                            "description": "Initial content of the file"
                        }
                    },
                    "required": ["path"]
                }
                """;
    }

    @Override
    public String execute(String arguments) throws Exception {
        JsonNode json = mapper.readTree(arguments);

        String relativePath = json.path("path").asText();
        String content = json.path("content").asText("");

        if (relativePath.isBlank()) {
            return "Error: File path cannot be empty.";
        }

        Path target = workspace.resolve(relativePath).normalize();

        if (!target.startsWith(workspace)) {
            return "Error: Access outside workspace is forbidden.";
        }

        try {
            Files.createDirectories(target.getParent());
            Files.createFile(target);
            Files.writeString(target, content);

            return "File created successfully: " + target;
        } catch (IOException e) {
            return "Error creating file: " + e.getMessage();
        }
    }
}