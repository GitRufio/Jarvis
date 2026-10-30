package pl.rufio.chat;

import io.github.ollama4j.models.chat.*;
import pl.rufio.Ollama.OllamaClient;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Chat {
    PrintWriter out = new PrintWriter(System.out, true);
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private List<OllamaChatMessage> chatHistory = new ArrayList<OllamaChatMessage>();
    private Thread printerThread;
    OllamaClient client = new OllamaClient(chatHistory, queue);

    public Chat() throws IOException {
        startPrinter();
    }

    public void prompt(String prompt) {
        if (!client.checkForAvailability()) {
            System.err.println("Ollama jest niedostępna!");
            return;
        }
        client.ChatWithOllama(prompt);

    }

    private void startPrinter() {
        printerThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String message = queue.take();
                    for (char c : message.toCharArray()) {
                        out.print(c);
                        out.flush();
                        Thread.sleep(10);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        printerThread.setDaemon(true);
        printerThread.start();
    }
    public List<OllamaChatMessage> getChatHistory() {
        return chatHistory;
    }
}
