package com.min.kvserver;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.junit.Assert.*;

public class KVServerTest {

    private static KVServer server;
    private static final int CLIENT_COUNT = 12;
    private static final int OPERATIONS_PER_CLIENT = 1000;
    private static final int SERVER_PORT = 10002;

    @BeforeClass
    public static void setUp() {
        // 启动服务器
        server = new KVServer(SERVER_PORT);
        server.start();

        // 确保服务器已经启动
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConcurrentOperations() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(CLIENT_COUNT);
        ExecutorService executorService = Executors.newFixedThreadPool(CLIENT_COUNT);

        for (int i = 0; i < CLIENT_COUNT; i++) {
            executorService.execute(() -> {
                KVClient KVClient = new KVClient(SERVER_PORT);
                for (int j = 0; j < OPERATIONS_PER_CLIENT; j++) {
                    String key = "key";

                    KVClient.append(key, "k");
                }
                latch.countDown();
            });
        }

        latch.await();
        executorService.shutdown();
        KVClient client = new KVClient(SERVER_PORT);
        String res = client.get("key");
        assertEquals(res.length(),CLIENT_COUNT*OPERATIONS_PER_CLIENT);
        System.out.println("All clients have finished their operations.");
    }

    @Test
    public void testOne(){
        KVClient client = new KVClient(SERVER_PORT);
        client.put("wangmin","handsome");
        System.out.println(client.get("wangmin"));
        client.append("wangmin","-append");
        System.out.println(client.get("wangmin"));
    }
}