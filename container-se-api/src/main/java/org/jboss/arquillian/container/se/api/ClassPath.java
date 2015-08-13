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

import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 * Represents a collection of archives that are available on classpath to the child JVM process when running using the SE container. Use the builder to collect
 * all the archives that should be made available on classpath.
 *
 * @author Tomas Remes
 * @author Jozef Hartinger
 */
public final class ClassPath {

    public static final String ROOT_ARCHIVE_PATH = "/";

    public static final ArchivePath SYSTEM_PROPERTIES_ARCHIVE_PATH = ArchivePaths.create(ROOT_ARCHIVE_PATH + "system.properties");

    private static final ArchivePath MARKER_FILE_ARCHIVE_PATH = ArchivePaths.create("META-INF/arquillian.se.container.ClassPath");

    private ClassPath() {
    }

    /**
     *
     * @return a new instance of class path builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * The builder should not be reused.
     */
    public static final class Builder {

        private final GenericArchive archive;

        private Properties systemProperties;

        private Builder() {
            this.archive = ShrinkWrap.create(GenericArchive.class).add(EmptyAsset.INSTANCE, MARKER_FILE_ARCHIVE_PATH);
            this.systemProperties = new Properties();
        }

        public Builder add(JavaArchive archive) {
            return addArchive(archive);
        }

        public Builder add(JavaArchive... archives) {
            for (JavaArchive archive : archives) {
                addArchive(archive);
            }
            return this;
        }

        public Builder setSystemProperties(Properties systemProperties) {
            this.systemProperties = systemProperties;
            return this;
        }

        public Builder addSystemProperty(String key, String value) {
            this.systemProperties.setProperty(key, value);
            return this;
        }

        /**
         *
         * @param name
         * @return the builder used to construct a virtual class path directory
         * @see ClassPathDirectory.Builder#buildAndUp()
         */
        public ClassPathDirectory.Builder addDirectory(String name) {
            return ClassPathDirectory.builder(name, this);
        }

        Builder addArchive(Archive<?> archive) {
            this.archive.add(archive, ROOT_ARCHIVE_PATH, ZipExporter.class);
            return this;
        }

        public Archive<?> build() {
            if (systemProperties != null && !systemProperties.isEmpty()) {
                StringWriter writer = new StringWriter();
                try {
                    systemProperties.store(writer, null);
                } catch (IOException e) {
                    throw new RuntimeException("Cannot add system properties", e);
                }
                this.archive.add(new StringAsset(writer.toString()), SYSTEM_PROPERTIES_ARCHIVE_PATH);
            }
            return archive;
        }
    }

    /**
     *
     * @param archive
     * @return <code>true</code> if the given archive represents a class path, <code>false</code> otherwise
     */
    public static boolean isRepresentedBy(Archive<?> archive) {
        return archive.contains(MARKER_FILE_ARCHIVE_PATH);
    }
}
