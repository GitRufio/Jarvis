package pl.rufio.Ollama;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class OllamaConfig {

    private final String host;
    private final String model;
    private final String keepAlive;
    private int timeout = 180;
    private int numPredict = 2048;
    private double temperature = 0.7;
    private double topP = 0.9;
    private final String thinkString;
    private boolean think = false;

    public OllamaConfig(String filePath){
        Properties properties = new Properties();

        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(filePath)) {

            if (input == null) {
                throw new IOException("Configuration file not found: " + filePath);
            }

            properties.load(input);
        }catch (IOException e) {
            e.printStackTrace();
        }

        host = properties.getProperty("ollama.host");
        model = properties.getProperty("ollama.model");
        keepAlive = properties.getProperty("ollama.keep_alive");
        try {
            timeout = Integer.parseInt(properties.getProperty("ollama.timeout"));
        } catch (NumberFormatException e) {
            System.err.println("Zapis w config ollama.timeout jest nieprawidłowy! Wprowadzam default: 180");
        }
        try{
            numPredict = Integer.parseInt(properties.getProperty("ollama.num_predict"));
        } catch (NumberFormatException e) {
            System.err.println("Zapis w config ollama.num_predict jest nieprawidłowy! Wprowadzam default: 2048");
        }
        try{
            temperature = Double.parseDouble(properties.getProperty("ollama.temperature"));
        }catch (NumberFormatException e) {
            System.err.println("Zapis w config ollama.temperature jest nieprawidłowy! Wprowadzam default: 0.7");
        }
        try{
            topP = Double.parseDouble(properties.getProperty("ollama.top_p"));
        }catch (NumberFormatException e) {
            System.err.println("Zapis w config ollama.top_p jest nieprawidłowy! Wprowadzam default: 0.9");
        }

        thinkString = properties.getProperty("ollama.think");
        if ("true".equalsIgnoreCase(thinkString)) {
            think = true;
        } else if ("false".equalsIgnoreCase(thinkString)) {
            think = false;
        } else {
            System.err.println(
                    "Zapis w config ollama.think jest nieprawidłowy! Wprowadzam default: false"
            );
        }
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
