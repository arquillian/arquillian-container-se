/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat, Inc., and individual contributors
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
package org.jboss.arquillian.container.se.test.client;

import java.io.File;
import java.util.concurrent.Executors;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.jboss.arquillian.container.se.api.LaunchServices;

public class TomcatLaunchService extends LaunchServices {

    private Tomcat tomcat;

    @Override
    public ClassLoader getClassLoader() {
        return TomcatLaunchService.class.getClassLoader();
    }

    @Override
    public void initialize() {

        String webappDirLocation = ".";
        tomcat = new Tomcat();
        tomcat.setPort(8080);

        StandardContext ctx = null;
        ctx = (StandardContext) tomcat.addContext("", new File(webappDirLocation).getAbsolutePath());
        Tomcat.addServlet(ctx, TestServlet.class.getSimpleName(), new TestServlet());
        ctx.addServletMapping("/test", TestServlet.class.getSimpleName());

        try {
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                tomcat.getServer().await();
            }
        });

    }

    @Override
    public void shutdown(){
        try {
            tomcat.stop();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }

    }
}
