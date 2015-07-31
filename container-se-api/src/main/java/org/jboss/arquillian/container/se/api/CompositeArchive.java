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
