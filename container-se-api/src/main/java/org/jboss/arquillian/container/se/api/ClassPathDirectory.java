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

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.ClassAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;

/**
 * Class path directory is represented as a {@link GenericArchive} instance.
 *
 * @author Martin Kouba
 */
public final class ClassPathDirectory {

    private static final ArchivePath MARKER_FILE_ARCHIVE_PATH = ArchivePaths.create("META-INF/arquillian.se.container.ClassPathDirectory");

    private ClassPathDirectory() {
    }

    /**
     *
     * @return a new instance of class path directory builder
     */
    static Builder builder(String name, ClassPath.Builder classPathBuilder) {
        return new Builder(name, classPathBuilder);
    }

    /**
     * A builder used to construct a virtual class path directory.
     *
     * @author Martin Kouba
     */
    public static final class Builder {

        private final ClassPath.Builder classPathBuilder;

        private final GenericArchive archive;

        private Builder(String name, ClassPath.Builder classPathBuilder) {
            this.classPathBuilder = classPathBuilder;
            this.archive = ShrinkWrap.create(GenericArchive.class, name).add(EmptyAsset.INSTANCE, MARKER_FILE_ARCHIVE_PATH);
        }

        public Builder addClass(Class<?> clazz) {
            archive.add(new ClassAsset(clazz), ClassPath.ROOT_ARCHIVE_PATH + clazz.getName());
            return this;
        }

        public Builder addResource(Asset resource, String target) {
            archive.add(resource, target);
            return this;
        }

        /**
         * Builds the virtual class path directory and returns the parent class path builder.
         *
         * @return the parent class path builder
         */
        public ClassPath.Builder buildAndUp() {
            return classPathBuilder.addArchive(archive);
        }

    }

    public static boolean isRepresentedBy(Archive<?> archive) {
        return archive.contains(MARKER_FILE_ARCHIVE_PATH);
    }

}