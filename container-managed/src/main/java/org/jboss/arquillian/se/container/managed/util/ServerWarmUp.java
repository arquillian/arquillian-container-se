package org.jboss.arquillian.se.container.managed.util;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class ServerWarmUp implements Runnable {

    CountDownLatch countDownLatch ;
    private final String host;
    private final int port;
    private boolean connectionAvailable = false;
    private int waitTime;
    

    public ServerWarmUp(String host, int port, int countDown) {
        this.host = host;
        this.port = port;
        waitTime = countDown;
    }

    public void run() {
        countDownLatch = new CountDownLatch(waitTime);
        
        while (countDownLatch.getCount() > 0 && !connectionAvailable) {
            countDownLatch.countDown();
            try (Socket ignored = new Socket(host, port)) {
                connectionAvailable = true;
            } catch (IOException e) {
                connectionAvailable = false;
            }
            try {
                Thread.sleep(waitTime*100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
