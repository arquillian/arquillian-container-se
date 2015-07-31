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

import java.lang.management.ManagementFactory;
import java.util.logging.Logger;

import javax.management.JMException;
import javax.management.MBeanServer;

import org.jboss.arquillian.protocol.jmx.JMXTestRunner;

/**
 * @author Tomas Remes
 */
public class Main {

    public static final String TEST_SUBPROCESS_TIMEOUT_SYSTEM_PROPERTY = "testSubprocessTimeout";

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final long DEFAULT_TIMEOUT = 60000L;

    public static void main(String[] args) {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            JMXTestRunner testRunner = new JMXTestRunner(new TestClassLoader(Main.class.getClassLoader()));
            testRunner.registerMBean(mbs);
            LOGGER.info("JMXTestRunner initialized");
        } catch (JMException e) {
            throw new RuntimeException("Unable to register JMXTestRunner", e);
        }
        // Wait for ManagedSEDeployableContainer to kill this subprocess/JVM
        // This process may not be terminated before the JMX communication finishes
        try {
            Thread.sleep(Long.getLong(TEST_SUBPROCESS_TIMEOUT_SYSTEM_PROPERTY, DEFAULT_TIMEOUT));
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted waiting for undeploy signal", e);
        }
    }
}
