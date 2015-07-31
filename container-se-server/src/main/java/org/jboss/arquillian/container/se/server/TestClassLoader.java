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
package org.jboss.arquillian.container.se.server;

import java.util.logging.Logger;

import org.jboss.arquillian.protocol.jmx.JMXTestRunner;

/**
 * @author Tomas Remes
 */
public class TestClassLoader implements JMXTestRunner.TestClassLoader {
    private final ClassLoader testClassLoader;
    private final static Logger log = Logger.getLogger(TestClassLoader.class.getName());

    TestClassLoader(ClassLoader classLoader) {
        testClassLoader = classLoader;
    }

    @Override
    public Class<?> loadTestClass(String className) throws ClassNotFoundException {
        log.info("Loading test class: " + className);
        return testClassLoader.loadClass(className);
    }
}
