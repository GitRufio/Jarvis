package pl.rufio.Ollama;

import io.github.ollama4j.Ollama;
import io.github.ollama4j.exceptions.OllamaException;
import io.github.ollama4j.models.chat.*;
import io.github.ollama4j.models.generate.OllamaGenerateTokenHandler;
import io.github.ollama4j.models.request.ThinkMode;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class OllamaClient {
    OllamaConfig config = new OllamaConfig("config.properties");
    private List<OllamaChatMessage> chatHistory;
    private BlockingQueue<String> queue;
    private final Ollama ollama = new Ollama(config.getHost());

    public OllamaClient(List<OllamaChatMessage> chatHistory, BlockingQueue<String> queue) throws IOException {
        this.chatHistory = chatHistory;
        this.queue = queue;
    }

    public void ChatWithOllama(String message) {
        try {
            sendMessage(message);
        }catch(OllamaException oe){
            System.err.println("Coś się zrąbało w OllamaClient. Napraw to");
        }
    }
    private void sendMessage(String prompt) throws OllamaException {
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
        OllamaChatResult chatResult = ollama.chat(chatRequest, streamObservering());
        chatHistory = chatResult.getChatHistory();
        System.out.println();
    }
    private OllamaChatStreamObserver streamObservering() {
        OllamaChatStreamObserver streamObserver = new OllamaChatStreamObserver();

        streamObserver.setResponseStreamHandler(
                new OllamaGenerateTokenHandler() {

                    @Override
                    public void accept(String message) {
                        queue.offer(message);
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
