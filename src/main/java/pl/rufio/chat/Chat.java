package pl.rufio.chat;


import io.github.ollama4j.Ollama;
import io.github.ollama4j.exceptions.OllamaException;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.models.chat.OllamaChatStreamObserver;
import io.github.ollama4j.models.generate.OllamaGenerateTokenHandler;

public class Chat {
    Ollama ollama = new Ollama();
    public void prompt(String prompt) throws OllamaException {
        ollama.setRequestTimeoutSeconds(180);
        if (!checkForAvailability()) {
            System.err.println("Ollama jest niedostępna!");
        }
        sendMessage(prompt);
    }
    private boolean checkForAvailability(){
        try {
            return ollama.ping();
        }  catch (OllamaException e) {
            System.err.println("Wystąpił błąd!!!");
            System.err.println(e.getMessage());
            return false;
        }
    }
    private void sendMessage(String prompt) throws OllamaException {
        String model = "qwen3:8b";
        //ollama.pullModel(model);

        OllamaChatRequest builder = OllamaChatRequest.builder().withModel(model);

        OllamaChatRequest chatRequest = builder.withMessage(OllamaChatMessageRole.USER, prompt).build();

        OllamaChatStreamObserver streamObserver = new OllamaChatStreamObserver();

        /*streamObserver.setThinkingStreamHandler(
                new OllamaGenerateTokenHandler() {
                    @Override
                    public void accept(String message) {
                        System.out.print(message.toUpperCase());
                    }
                });*/
        // Response tokens to be printed in lowercase
        streamObserver.setResponseStreamHandler(
                new OllamaGenerateTokenHandler() {
                    @Override
                    public void accept(String message) {
                        System.out.print(message);
                    }
                });

        ollama.chat(chatRequest, streamObserver);
        System.out.println("");
    }
}
