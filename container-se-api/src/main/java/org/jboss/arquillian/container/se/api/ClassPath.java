package org.jboss.arquillian.container.se.api;

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

        public Archive<?> build() {
            return archive;
        }
    }
}
