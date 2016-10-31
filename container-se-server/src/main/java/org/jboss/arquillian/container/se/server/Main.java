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
import java.security.PrivilegedActionException;
import java.util.logging.Logger;

import javax.management.JMException;
import javax.management.MBeanServer;

import org.jboss.arquillian.container.se.api.LaunchServices;
import org.jboss.arquillian.protocol.jmx.JMXTestRunner;

/**
 * @author Tomas Remes
 * @author Martin Kouba
 */
public class Main {

    public static final String SYSTEM_PROPERTY_TEST_SUBPROCESS_TIMEOUT = "testSubprocessTimeout";

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final long DEFAULT_TIMEOUT = 60000L;

    public static void main(String[] args) {

        LaunchServices launchServices = getLaunchServices();
        launchServices.initialize();

        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ClassLoader classLoader = launchServices.getClassLoader();
            if (classLoader == null) {
                classLoader = getDefaultClassLoader();
            }
            JMXTestRunner testRunner = new JMXTestRunner(new TestClassLoader(classLoader));
            testRunner.registerMBean(mbs);
            LOGGER.info("JMXTestRunner initialized using [" + classLoader + "]");
        } catch (JMException e) {
            throw new RuntimeException("Unable to register JMXTestRunner", e);
        }

        // Wait for ManagedSEDeployableContainer to kill this subprocess/JVM
        // This process may not be terminated before the JMX communication finishes
        try {
            Thread.sleep(Long.getLong(SYSTEM_PROPERTY_TEST_SUBPROCESS_TIMEOUT, DEFAULT_TIMEOUT));
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted waiting for undeploy signal", e);
        }
    }

    private static LaunchServices getLaunchServices() {
        final LaunchServices launchServices;
        String launchServicesClass = System.getProperty(LaunchServices.SYSTEM_PROPERTY_LAUNCH_SERVICES_CLASS);
        if (launchServicesClass != null) {
            try {
                launchServices = (LaunchServices) SecurityActions.newInstance(SecurityActions.getClassLoader(Main.class).loadClass(launchServicesClass));
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | PrivilegedActionException e) {
                throw new RuntimeException(e);
            }
        } else {
            launchServices = new DefaultLaunchServices();
        }
        return launchServices;
    }

    private static class DefaultLaunchServices extends LaunchServices {

        @Override
        public ClassLoader getClassLoader() {
            return getDefaultClassLoader();
        }

    }

    private static ClassLoader getDefaultClassLoader() {
        return SecurityActions.getClassLoader(Main.class);
    }

}
