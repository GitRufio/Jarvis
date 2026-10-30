package pl.rufio.memory;

import io.github.ollama4j.models.chat.OllamaChatMessage;

import java.util.ArrayList;
import java.util.List;

public class MemoryService {

    List<OllamaChatMessage> chatHistory = new ArrayList<>();

    public void changeList(List<OllamaChatMessage> chatHistory) {
        this.chatHistory = chatHistory;
    }
    public List<OllamaChatMessage> getChatHistory() {
        return chatHistory;
    }
}
