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

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;

import com.gargoylesoftware.htmlunit.WebClient;
import org.jboss.arquillian.container.se.api.ClassPath;
import org.jboss.arquillian.container.se.api.LaunchServices;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tomas Remes
 */
@RunWith(Arquillian.class)
public class EmbeddedTomcatTest {

    @Deployment
    public static Archive<?> getDeployment() {
        final JavaArchive testArchive = ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addClasses(EmbeddedTomcatTest.class, TomcatLaunchService.class, TestServlet.class);
        return ClassPath.builder().addSystemProperty(LaunchServices.SYSTEM_PROPERTY_LAUNCH_SERVICES_CLASS, TomcatLaunchService.class.getName())
                .add(Maven.resolver().loadPomFromFile("pom.xml").importTestDependencies().resolve().withTransitivity().asFile())
                .add(testArchive)
                .build();
    }

    @Test
    @RunAsClient
    public void test() throws IOException {
        WebClient client = new WebClient();
        String servletResponse = client.getPage(new URL("http://localhost:8080/test")).getWebResponse().getContentAsString();
        assertEquals("hello from servlet", servletResponse);
    }

}
