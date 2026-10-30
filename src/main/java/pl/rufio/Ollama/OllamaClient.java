package pl.rufio.Ollama;

import io.github.ollama4j.Ollama;
import io.github.ollama4j.exceptions.OllamaException;
import io.github.ollama4j.models.chat.*;
import io.github.ollama4j.models.generate.OllamaGenerateTokenHandler;
import io.github.ollama4j.models.request.ThinkMode;
import io.github.ollama4j.tools.Tools;
import pl.rufio.chat.ChatQueue;
import pl.rufio.memory.MemoryService;

import java.util.List;

public class OllamaClient {
    private final OllamaConfig config;
    private final MemoryService memoryService;
    private final ChatQueue chatQueue;
    private final Ollama ollama;
    private final List<Tools.Tool> ollamaTools;

    public OllamaClient(
            OllamaConfig config,
            ChatQueue chatQueue,
            MemoryService memoryService,
            List<Tools.Tool> ollamaTools
    ){
        this.config = config;
        this.chatQueue = chatQueue;
        this.memoryService = memoryService;
        this.ollamaTools = ollamaTools;
        this.ollama = new Ollama(config.getHost());
    }

    public void chatWithOllama(String message) {
        sendMessage(message);
    }
    private void sendMessage(String prompt){
        String model = config.getModel();
        ollama.setRequestTimeoutSeconds(config.getTimeout());
        OllamaChatRequest chatRequest;
        if(memoryService.getChatHistory().isEmpty()){
            chatRequest = OllamaChatRequest.builder()
                    .withModel(model)
                    .withMessage(OllamaChatMessageRole.SYSTEM,"Jesteś agentem Jarvis, zaprojektowanym do pomocy użytkownikowi.")
                    .withMessage(OllamaChatMessageRole.USER, prompt).withTools(ollamaTools).withUseTools(false).build();
        } else{
            chatRequest = OllamaChatRequest.builder()
                    .withModel(model)
                    .withMessages(memoryService.getChatHistory())
                    .withMessage(OllamaChatMessageRole.USER, prompt).withTools(ollamaTools).withUseTools(false).build();
        }

        if(config.isThink()){
            chatRequest.setThink(ThinkMode.ENABLED);
        }else{
            chatRequest.setThink(ThinkMode.DISABLED);
        }
        try{
            OllamaChatResult chatResult = ollama.chat(chatRequest, streamObservering());
            memoryService.changeList(chatResult.getChatHistory());
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
