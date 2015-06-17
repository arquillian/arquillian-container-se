package org.jboss.arquillian.container.se.managed.util;

import java.io.IOException;
import java.net.Socket;

public class ServerAwait {

    private final String host;
    private final int port;
    private int waitTime;


    public ServerAwait(String host, int port, int waitTime) {
        this.host = host;
        this.port = port;
        this.waitTime = waitTime * 10;
    }

    public boolean run() {
        while (waitTime > 0) {
            waitTime--;
            try (Socket ignored = new Socket(host, port)) {
                return true;
            } catch (IOException ignored) {
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

}
