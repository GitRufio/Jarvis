package  pl.rufio;

import pl.rufio.Jarvis.JarvisApplication;
import pl.rufio.Ollama.OllamaConfig;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        OllamaConfig config = new OllamaConfig("config.properties");
        System.out.println(config.getHost());
        JarvisApplication app = new JarvisApplication();
        app.start();

    }
}