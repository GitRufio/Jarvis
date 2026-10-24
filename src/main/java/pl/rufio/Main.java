package  pl.rufio;


import io.github.ollama4j.exceptions.OllamaException;
import pl.rufio.chat.Chat;

import java.util.Scanner;

public class Main {

    static Chat chat = new Chat();

    public static void main(String[] args) throws OllamaException {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.print("Ty: ");
            String prompt = scanner.nextLine();
            if(!prompt.equalsIgnoreCase("stop")) {
                System.out.print("AI: ");
                chat.prompt(prompt);
            }else{
                running = false;
            }
        }

    }
}