package pl.rufio.Jarvis;

import io.github.ollama4j.models.chat.OllamaChatMessage;
import io.github.ollama4j.tools.Tools;
import pl.rufio.Ollama.Ollama4jToolAdapter;
import pl.rufio.Ollama.OllamaClient;
import pl.rufio.Ollama.OllamaConfig;
import pl.rufio.Tools.packageWithTools.files.CreateFileTool;
import pl.rufio.Tools.packageWithTools.GetOsName;
import pl.rufio.Tools.ToolRegistry;
import pl.rufio.Tools.packageWithTools.files.DeleteFileTool;
import pl.rufio.Tools.packageWithTools.files.ReadFileTool;
import pl.rufio.chat.Chat;
import pl.rufio.chat.ChatQueue;
import pl.rufio.memory.MemoryService;

import java.util.List;
import java.util.Scanner;

public class JarvisApplication {
    OllamaConfig config = new OllamaConfig("config.properties");
    MemoryService memoryService = new MemoryService();
    ChatQueue chatQueue = new ChatQueue();
    Scanner scanner = new Scanner(System.in);
    ToolRegistry toolRegistry = new ToolRegistry();
    Ollama4jToolAdapter adapter = new Ollama4jToolAdapter();


    public void start(){
        toolRegistry.register(new GetOsName());
        toolRegistry.register(new CreateFileTool());
        toolRegistry.register(new ReadFileTool());
        toolRegistry.register(new DeleteFileTool());
        List<Tools.Tool> ollamaTools = toolRegistry.getAll().stream().map(adapter::adapt).toList();
        OllamaClient client = new OllamaClient(config, chatQueue, memoryService, ollamaTools);
        Chat chat = new Chat(client, chatQueue);

        System.out.println(config.getHost());
        System.out.println(config.isThink());
        boolean running = true;
        while (running) {
            System.out.print("Ty: ");
            String prompt = scanner.nextLine();
            switch (prompt) {
                case "stop":{
                    running = false;
                    break;
                }
                case "history":{
                    for (OllamaChatMessage message : memoryService.getChatHistory()) {
                        System.out.println(message);
                    }
                    break;
                }
                case "test":{
                    System.out.println(config.getHost());
                    break;
                }
                default:{
                    chat.prompt(prompt);
                    break;
                }
            }
        }
    }
}
