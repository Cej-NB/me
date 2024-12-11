package com.cej.shardingspherejdbcdemo;

public class MainTest {
    private static int count = 0;

    public static void main(String[] args) throws InterruptedException {


        Thread t1 = new Thread(()->{
            synchronized (MainTest.class) {
                System.out.println("t1,获取到锁....");
                for (int i = 0; i < 3000; i++) {
                    count++;
                }
            }
            System.out.println("释放锁");
        });

        Thread t2 = new Thread(()->{

                System.out.println("t2,获取到锁....");

                for (int i = 0; i < 3000; i++) {
                    count--;
                }
        });


        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println(count);
    }



    public static void sleep(int time){
        try{
            Thread.sleep(time);

        }catch (Exception e){}
    }
}
