package org.jboss.arquillian.se.container.managed.archive;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

interface ClassPathCompositeArchive extends Archive<ClassPathCompositeArchive> {

    ClassPathCompositeArchive add(JavaArchive archive);
}
