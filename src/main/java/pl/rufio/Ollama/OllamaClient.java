package pl.rufio.Ollama;

import io.github.ollama4j.Ollama;
import io.github.ollama4j.exceptions.OllamaException;
import io.github.ollama4j.models.chat.*;
import io.github.ollama4j.models.generate.OllamaGenerateTokenHandler;
import io.github.ollama4j.models.request.ThinkMode;
import pl.rufio.chat.ChatQueue;

import java.util.List;

public class OllamaClient {
    OllamaConfig config;
    private List<OllamaChatMessage> chatHistory;
    ChatQueue chatQueue;
    private final Ollama ollama;

    public OllamaClient(
            List<OllamaChatMessage> chatHistory,
            OllamaConfig config,
            ChatQueue chatQueue
    ){
        this.chatHistory = chatHistory;
        this.config = config;
        this.chatQueue = chatQueue;
        this.ollama = new Ollama(config.getHost());
    }

    public void ChatWithOllama(String message) {
        sendMessage(message);
    }
    private void sendMessage(String prompt){
        String model = config.getModel();
        ollama.setRequestTimeoutSeconds(config.getTimeout());
        OllamaChatRequest chatRequest;
        if(chatHistory.isEmpty()){
            chatRequest = OllamaChatRequest.builder()
                    .withModel(model)
                    .withMessages(chatHistory)
                    .withMessage(OllamaChatMessageRole.USER,"Jesteś agentem Jarvis, zaprojektowanym do pomocy użytkownikowi." +
                            " A oto piersze pytanie użytkownika: " +  prompt).build();
        } else{
            chatRequest = OllamaChatRequest.builder()
                    .withModel(model)
                    .withMessages(chatHistory)
                    .withMessage(OllamaChatMessageRole.USER, prompt).build();
        }

        if(config.isThink() == true){
            chatRequest.setThink(ThinkMode.ENABLED);
        }else{
            chatRequest.setThink(ThinkMode.DISABLED);
        }
        try{
            OllamaChatResult chatResult = ollama.chat(chatRequest, streamObservering());
            chatHistory = chatResult.getChatHistory();
        }catch(OllamaException oe){
            oe.printStackTrace();
        }
        System.out.println();
    }
    private OllamaChatStreamObserver streamObservering() {
        OllamaChatStreamObserver streamObserver = new OllamaChatStreamObserver();

        streamObserver.setResponseStreamHandler(
                new OllamaGenerateTokenHandler() {

                    @Override
                    public void accept(String message) {
                        chatQueue.put(message);
                    }
                });
        return streamObserver;
    }
    public boolean checkForAvailability(){
        try {
            return ollama.ping();
        }  catch (OllamaException e) {
            System.err.println("Wystąpił błąd!!!");
            System.err.println(e.getMessage());
            return false;
        }
    }
}
