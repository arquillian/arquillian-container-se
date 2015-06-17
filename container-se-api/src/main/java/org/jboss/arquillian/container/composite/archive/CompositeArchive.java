package org.jboss.arquillian.container.composite.archive;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.spec.JavaArchiveImpl;

public final class CompositeArchive extends JavaArchiveImpl {

    private final List<Archive<?>> items;

    public CompositeArchive() {
        super(ShrinkWrap.create(JavaArchive.class));
        this.items = new LinkedList<>();
    }

    public CompositeArchive addItem(Archive<?> archive) {
        items.add(archive);
        return this;
    }

    public List<Archive<?>> getItems() {
        return Collections.unmodifiableList(items);
    }

}
