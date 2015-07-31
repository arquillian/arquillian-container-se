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

import java.util.Properties;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 * Represents a collection of archives that are available on classpath to the child JVM process when running using the SE container.
 * Use the builder to collect all the archives that should be made available on classpath.
 *
 * @author Tomas Remes
 * @author Jozef Hartinger
 */
public final class ClassPath {

    private ClassPath() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final CompositeArchive archive;

        private Builder() {
            archive = new CompositeArchive();
        }

        public Builder add(JavaArchive archive) {
            this.archive.addItem(archive);
            return this;
        }

        public Builder add(JavaArchive... archives) {
            for (JavaArchive archive : archives) {
                this.archive.addItem(archive);
            }
            return this;
        }

        public Builder addSystemProperties(Properties properties) {
            archive.setSystemProperties(properties);
            return this;
        }

        public Archive<?> build() {
            return archive;
        }
    }
}
