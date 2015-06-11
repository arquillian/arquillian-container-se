package org.jboss.arquillian.container.composite.archive;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

public interface ClassPathCompositeArchive extends Archive<JavaArchive> {

    ClassPathCompositeArchive add(JavaArchive archive);
    
    ClassPathCompositeArchive add(JavaArchive... archive);

}
