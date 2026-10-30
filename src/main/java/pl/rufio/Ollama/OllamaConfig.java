package pl.rufio.Ollama;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class OllamaConfig {

    private final String host;
    private final String model;
    private final String keepAlive;
    private final int timeout;
    private final int numPredict;
    private final double temperature;
    private final double topP;
    private final boolean think;

    public OllamaConfig(String filePath) throws IOException {
        Properties properties = new Properties();

        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(filePath)) {

            if (input == null) {
                throw new IOException("Configuration file not found: " + filePath);
            }

            properties.load(input);
        }

        host = properties.getProperty("ollama.host");
        model = properties.getProperty("ollama.model");
        keepAlive = properties.getProperty("ollama.keep_alive");

        timeout = Integer.parseInt(
                properties.getProperty("ollama.timeout"));

        numPredict = Integer.parseInt(
                properties.getProperty("ollama.num_predict"));

        temperature = Double.parseDouble(
                properties.getProperty("ollama.temperature"));

        topP = Double.parseDouble(
                properties.getProperty("ollama.top_p"));

        think = Boolean.parseBoolean(
                properties.getProperty("ollama.think"));
    }

    public String getHost() { return host; }
    public String getModel() { return model; }
    public String getKeepAlive() { return keepAlive; }
    public int getTimeout() { return timeout; }
    public int getNumPredict() { return numPredict; }
    public double getTemperature() { return temperature; }
    public double getTopP() { return topP; }
    public boolean isThink() { return think; }
}
