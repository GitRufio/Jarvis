package pl.rufio.Jarvis;

import io.github.ollama4j.models.chat.OllamaChatMessage;
import pl.rufio.Ollama.OllamaConfig;
import pl.rufio.chat.Chat;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class JarvisApplication {
    Scanner scanner = new Scanner(System.in);
    Chat chat = new Chat();
    OllamaConfig config = new OllamaConfig("config.properties");

    public JarvisApplication() throws IOException {

    }

    public void start(){
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
                }
                default:{
                    chat.prompt(prompt);
                    break;
                }
            }
        }
    }
}
