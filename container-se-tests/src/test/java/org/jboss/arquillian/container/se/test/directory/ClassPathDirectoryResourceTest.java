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
package org.jboss.arquillian.container.se.test.directory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import org.jboss.arquillian.container.se.api.ClassPath;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ClassPathDirectoryResourceTest {

    private static final String RESOURCE_NAME = "META-INF/my-super-descriptor.xml";

    @Deployment
    public static Archive<?> createTestArchive() {
        return ClassPath.builder().add(ShrinkWrap.create(JavaArchive.class).addClass(ClassPathDirectoryResourceTest.class)).addDirectory("test-dir")
                .addResource(new StringAsset("hello"), RESOURCE_NAME).buildAndUp().build();
    }

    @Test
    public void testResourceFound() throws IOException {
        URL resourceUrl = this.getClass().getClassLoader().getResource(RESOURCE_NAME);
        assertNotNull(resourceUrl);
        StringWriter writer = new StringWriter();
        try (InputStream in = resourceUrl.openStream()) {
            copy(new InputStreamReader(in), writer);
        }
        assertEquals("hello", writer.toString());
    }

    private void copy(Reader in, Writer out) throws IOException {
        final char[] buffer = new char[8192];
        int n = 0;
        while (-1 != (n = in.read(buffer))) {
            out.write(buffer, 0, n);
        }
        out.flush();
    }

}
