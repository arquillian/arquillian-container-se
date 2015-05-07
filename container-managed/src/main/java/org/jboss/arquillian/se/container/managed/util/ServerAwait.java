package org.jboss.arquillian.se.container.managed.util;

import java.io.IOException;
import java.net.Socket;

public class ServerAwait implements Runnable {

    private final String host;
    private final int port;
    private boolean connectionAvailable = false;
    private int waitTime;
    

    public ServerAwait(String host, int port, int waitTime) {
        this.host = host;
        this.port = port;
        this.waitTime = waitTime;
    }

    public void run() {

        while (waitTime > 0 && !connectionAvailable) {
            waitTime--;
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
