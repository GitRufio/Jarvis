package pl.rufio.Tools.packageWithTools.files;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.rufio.Tools.Tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReadFileTool implements Tool {
    private final ObjectMapper mapper = new ObjectMapper();

    private final Path workspace = Path.of(
            System.getProperty("user.home"),
            "Jarvis",
            "workspace"
    ).toAbsolutePath().normalize();

    @Override
    public String getName() {
        return "read_file";
    }

    @Override
    public String getDescription() {
        return "Reads the content of a file in the Jarvis workspace.";
    }

    @Override
    public String getParametersSchema() {
        return """
                {
                    "type": "object",
                    "properties": {
                        "path": {
                            "type": "string",
                            "description": "Relative path of the file to read"
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

        if (relativePath.isBlank()) {
            return "Error: File path cannot be empty.";
        }

        Path target = workspace.resolve(relativePath).normalize();

        if (!target.startsWith(workspace) || target.equals(workspace)) {
            return "Error: Access outside workspace is forbidden.";
        }

        if (!Files.exists(target)) {
            return "Error: File does not exist.";
        }

        if (!Files.isRegularFile(target)) {
            return "Error: The specified path is not a regular file.";
        }

        try {
            return Files.readString(target);
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }
}
