package pl.rufio.Jarvis;

import io.github.ollama4j.models.chat.OllamaChatMessage;
import pl.rufio.Ollama.OllamaClient;
import pl.rufio.Ollama.OllamaConfig;
import pl.rufio.chat.Chat;
import pl.rufio.chat.ChatQueue;
import pl.rufio.memory.MemoryService;

import java.util.List;
import java.util.Scanner;

public class JarvisApplication {
    OllamaConfig config = new OllamaConfig("config.properties");
    MemoryService memoryService = new MemoryService();
    ChatQueue chatQueue = new ChatQueue();
    OllamaClient client = new OllamaClient(memoryService.getChatHistory(), config, chatQueue);
    Chat chat = new Chat(client, chatQueue);
    Scanner scanner = new Scanner(System.in);

    public void start(){
        System.out.println(config.getHost());
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
                    List<OllamaChatMessage> history = chat.getChatHistory();
                    for (OllamaChatMessage message : history) {
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
