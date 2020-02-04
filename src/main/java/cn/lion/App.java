package cn.lion;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ){
        System.out.println(Runtime.getRuntime().availableProcessors());
    }
}

class Consumer implements Runnable{

    private ArrayBlockingQueue<Integer> queue;

    Consumer(ArrayBlockingQueue queue){
        this.queue = queue;
    }
    @Override
    public void run() {
        for(int i=0;i<10;i++){
            try {
                consume();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void consume() throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + ":"+queue.take());
    }
}

class Provider implements Runnable{

    private ArrayBlockingQueue<Integer> queue;

    public Provider(ArrayBlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        for(int i=0; i<1000; i++){
            try {
                queue.put(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}