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
package org.jboss.arquillian.container.se.managed.jmx;

import java.util.Collection;

import org.jboss.arquillian.container.se.api.ClassPath;
import org.jboss.arquillian.container.test.spi.TestDeployment;
import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentPackager;
import org.jboss.arquillian.container.test.spi.client.deployment.ProtocolArchiveProcessor;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;

/**
 * @author Tomas Remes
 */
public class TestDeploymentPackager implements DeploymentPackager {

    @Override
    public Archive<?> generateDeployment(TestDeployment testDeployment, Collection<ProtocolArchiveProcessor> collection) {
        Archive<?> applicationArchive = testDeployment.getApplicationArchive();
        boolean isClassPath = ClassPath.isRepresentedBy(applicationArchive);
        for (Archive<?> auxiliaryArchive : testDeployment.getAuxiliaryArchives()) {
            if (isClassPath) {
                applicationArchive.add(auxiliaryArchive, ClassPath.ROOT_ARCHIVE_PATH, ZipExporter.class);
            } else {
                applicationArchive.merge(auxiliaryArchive);
            }
        }
        return applicationArchive;
    }
}
