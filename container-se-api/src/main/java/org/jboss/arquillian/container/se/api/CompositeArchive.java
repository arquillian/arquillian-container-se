package org.jboss.arquillian.container.se.api;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.spec.JavaArchiveImpl;

public final class CompositeArchive extends JavaArchiveImpl {

    private final List<Archive<?>> items;
    private Properties systemProperties;

    public CompositeArchive() {
        super(ShrinkWrap.create(JavaArchive.class));
        this.items = new LinkedList<>();

    }

    public CompositeArchive addItem(Archive<?> archive) {
        items.add(archive);
        return this;
    }

    public CompositeArchive setSystemProperties(Properties systemProperties) {
        this.systemProperties = systemProperties;
        return this;
    }

    public Properties getSystemProperties() {
        return systemProperties;
    }

    public List<Archive<?>> getItems() {
        return Collections.unmodifiableList(items);
    }

}
