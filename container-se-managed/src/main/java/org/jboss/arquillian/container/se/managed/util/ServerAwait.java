/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
