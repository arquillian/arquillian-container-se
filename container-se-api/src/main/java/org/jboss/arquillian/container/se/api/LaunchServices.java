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
package org.jboss.arquillian.container.se.api;

/**
 * Sometimes it might be useful to supply a custom logic to initialize the test environment before the test is executed (even before the test class is loaded,
 * e.g. to specify a class loader used to load the test class) and to shut down the test environment properly.
 * <p>
 * Implementation must have a public constructor with no parameters.
 * <p>
 * Exactly one instance is created per each test class/deployment.
 *
 * @author Martin Kouba
 */
public abstract class LaunchServices {

    public static final String SYSTEM_PROPERTY_LAUNCH_SERVICES_CLASS = LaunchServices.class.getName();

    /**
     *
     * @return the class loader used to load the test class, or <code>null</code> if the default should be used
     */
    public ClassLoader getClassLoader() {
        return null;
    }

    /**
     * This method is invoked before the test runner is initialized and before the test class is loaded (and before any test method is executed).
     */
    public void initialize() {
        // No-op
    }

    /**
     * This method is invoked by a virtual-machine shutdown hook when the test subprocess/JVM is terminated.
     *
     * @see Runtime#addShutdownHook(Thread)
     */
    public void shutdown() {
        // No-op
    }

}
