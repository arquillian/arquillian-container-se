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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.arquillian.container.se.api.LaunchServices;

public class CustomLaunchServices extends LaunchServices {

    static final String MARKER_FILE_PATH = "target/launch-services-marker.txt";

    static AtomicBoolean initializeCalled = new AtomicBoolean(false);

    static AtomicBoolean getClassLoaderCalled = new AtomicBoolean(false);

    private Path markerFile;

    @Override
    public ClassLoader getClassLoader() {
        getClassLoaderCalled.set(true);
        return CustomLaunchServices.class.getClassLoader();
    }

    @Override
    public void initialize() {
        initializeCalled.set(true);
        try {
            markerFile = new File(MARKER_FILE_PATH).toPath();
            Files.write(markerFile, "ok".getBytes());
        } catch (IOException ignored) {
        }
    }

    @Override
    public void shutdown() {
        if (markerFile != null) {
            try {
                Files.delete(markerFile);
            } catch (IOException e) {
                throw new RuntimeException("Unable to delete test marker file " + markerFile, e);
            }
        }
    }

}
