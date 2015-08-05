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
package org.jboss.arquillian.container.se.test.launchservices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.container.se.api.ClassPath;
import org.jboss.arquillian.container.se.api.LaunchServices;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CustomLaunchServicesTest {

    @Deployment
    public static Archive<?> createTestArchive() {
        return ClassPath.builder().add(ShrinkWrap.create(JavaArchive.class).addClasses(CustomLaunchServicesTest.class, CustomLaunchServices.class))
                .addSystemProperty(LaunchServices.SYSTEM_PROPERTY_LAUNCH_SERVICES_CLASS, CustomLaunchServices.class.getName()).build();
    }

    @Test
    public void testInitializeCalled() {
        assertTrue(CustomLaunchServices.initializeCalled.get());
    }

    @Test
    public void testGetClassLoaderCalled() {
        assertTrue(CustomLaunchServices.getClassLoaderCalled.get());
        assertEquals(CustomLaunchServices.class.getClassLoader(), CustomLaunchServicesTest.class.getClassLoader());
    }

}
