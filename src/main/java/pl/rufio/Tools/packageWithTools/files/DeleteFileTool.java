
package pl.rufio.Tools.packageWithTools.files;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.rufio.Tools.Tool;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class DeleteFileTool implements Tool {

    private final ObjectMapper mapper = new ObjectMapper();

    private final Path workspace = Path.of(
            System.getProperty("user.home"),
            "Jarvis",
            "workspace"
    ).toAbsolutePath().normalize();

    @Override
    public String getName() {
        return "delete_file";
    }

    @Override
    public String getDescription() {
        return "Deletes a file or directory from the Jarvis workspace. "
                + "Set recursive to true to delete a directory and all its contents.";
    }

    @Override
    public String getParametersSchema() {
        return """
                {
                    "type": "object",
                    "properties": {
                        "path": {
                            "type": "string",
                            "description": "Relative path of the file or directory to delete"
                        },
                        "recursive": {
                            "type": "boolean",
                            "description": "If true, deletes a directory and all its contents. Defaults to false."
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
        boolean recursive = json.path("recursive").asBoolean(false);

        if (relativePath.isBlank()) {
            return "Error: Path cannot be empty.";
        }

        Path target = workspace.resolve(relativePath).normalize();

        if (!target.startsWith(workspace) || target.equals(workspace)) {
            return "Error: Access outside workspace is forbidden.";
        }

        if (!Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
            return "Error: File or directory does not exist.";
        }

        try {
            if (recursive && Files.isDirectory(target, LinkOption.NOFOLLOW_LINKS)) {
                Files.walkFileTree(target, new SimpleFileVisitor<>() {

                    @Override
                    public FileVisitResult visitFile(
                            Path file,
                            BasicFileAttributes attrs
                    ) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(
                            Path dir,
                            IOException exc
                    ) throws IOException {
                        if (exc != null) {
                            throw exc;
                        }

                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } else {
                Files.delete(target);
            }

            return "Deleted successfully: " + relativePath;

        } catch (IOException e) {
            return "Error deleting: " + e.getMessage();
        }
    }
}
