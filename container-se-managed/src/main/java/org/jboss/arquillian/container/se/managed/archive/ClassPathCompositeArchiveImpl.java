package org.jboss.arquillian.container.se.managed.archive;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.impl.base.spec.JavaArchiveImpl;

public class ClassPathCompositeArchiveImpl extends JavaArchiveImpl implements ClassPathCompositeArchive {

    private Archive<?> delegate;

    /**
     * Create a new JavaArchive with any type storage engine as backing.
     *
     * @param delegate The storage backing.
     */
    public ClassPathCompositeArchiveImpl(Archive<?> delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public ClassPathCompositeArchive add(JavaArchive... archive) {
        for (JavaArchive javaArchive : archive) {
            delegate.add(javaArchive, new BasicPath(), ZipExporter.class);
        }
        return this;
    }
}
