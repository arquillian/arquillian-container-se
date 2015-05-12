package org.jboss.arquillian.se.container.managed.archive;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.container.ContainerBase;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;

public class ClassPathCompositeArchiveImpl<T extends ClassPathCompositeArchive> extends ContainerBase<ClassPathCompositeArchive> implements ClassPathCompositeArchive {
    
    protected ClassPathCompositeArchiveImpl(Class<ClassPathCompositeArchive> actualType, Archive<?> archive) {
        super(actualType, archive);
    }
    
    public ClassPathCompositeArchiveImpl(Archive<?> delegate){
        super(ClassPathCompositeArchive.class, delegate);
    }

    @Override
    protected ArchivePath getManifestPath() {
        return null;
    }

    @Override
    protected ArchivePath getResourcePath() {
        return null;
    }

    @Override
    protected ArchivePath getClassesPath() {
        return new BasicPath();
    }

    @Override
    protected ArchivePath getLibraryPath() {
        return null;
    }

    @Override
    public ClassPathCompositeArchive add(JavaArchive archive) {

        return add(archive, new BasicPath(), ZipExporter.class);
    }
}
