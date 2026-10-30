package pl.rufio.chat;

import pl.rufio.Ollama.OllamaClient;

public class Chat {
    ChatQueue chatQueue;
    private Thread printerThread;
    OllamaClient client;

    public Chat(
            OllamaClient client,
            ChatQueue chatQueue
    ){
        this.client = client;
        this.chatQueue = chatQueue;
        startPrinter();
    }

    public void prompt(String prompt) {
        if (!client.checkForAvailability()) {
            System.err.println("Ollama jest niedostępna!");
            return;
        }
        client.chatWithOllama(prompt);

    }

    private void startPrinter() {
        printerThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String message = chatQueue.get();
                    for (char c : message.toCharArray()) {
                        System.out.print(c);
                        System.out.flush();
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
}
