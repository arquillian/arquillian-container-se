package org.jboss.arquillian.se.container.managed.archive;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 * @author Tomas Remes
 */
public class ClassPathCompositeArchiveBuilder {

    private ClassPathCompositeArchive archive;

    private ClassPathCompositeArchiveBuilder() {
        archive = ShrinkWrap.create(ClassPathCompositeArchive.class);
    }

    public static ClassPathCompositeArchiveBuilder create() {
        return new ClassPathCompositeArchiveBuilder();

    }

    public ClassPathCompositeArchive addJavaArchive(JavaArchive archive) {
        return this.archive.add(archive);
    }

}
