package pl.rufio.chat;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatQueue {
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public void put(String message) {
        queue.offer(message);
    }
    public String get(){
        try {
            return queue.take();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return null;
    }
}
