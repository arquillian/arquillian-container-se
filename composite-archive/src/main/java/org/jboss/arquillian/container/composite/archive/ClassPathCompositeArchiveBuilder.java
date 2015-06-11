package org.jboss.arquillian.container.composite.archive;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 * @author Tomas Remes
 */
public class ClassPathCompositeArchiveBuilder {

    private ClassPathCompositeArchive archive;

    private ClassPathCompositeArchiveBuilder() {
        archive = new ClassPathCompositeArchiveImpl(ShrinkWrap.create(JavaArchive.class));
    }

    public static ClassPathCompositeArchiveBuilder create() {
        return new ClassPathCompositeArchiveBuilder();
    }

    public ClassPathCompositeArchive addJavaArchive(JavaArchive...
            archive) {
        return this.archive.add(archive);
    }

    public ClassPathCompositeArchive addJavaArchive(JavaArchive
            archive) {
        return this.archive.add(archive);
    }


}
