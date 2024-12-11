package com.cej.me;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpringTest {

    private static final ThreadLocal<Integer> local =new ThreadLocal<>();

    @Test
    public void test() {
        local.set(1);

        System.out.println(local.get());
    }


    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {

//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            while(true) {
                boolean isInterrupted = Thread.currentThread().isInterrupted();
                if(isInterrupted) {
                    System.out.println(123);
                    break;
                }
            }
        });

        thread.start();
        Thread.sleep(1000);

        thread.interrupt();


    }

}
