package org.jboss.arquillian.container.se.managed.archive;

import java.util.Collection;

import org.jboss.arquillian.container.test.spi.TestDeployment;
import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentPackager;
import org.jboss.arquillian.container.test.spi.client.deployment.ProtocolArchiveProcessor;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;

/**
 * @author Tomas Remes
 */
public class SimpleDeploymentPackager implements DeploymentPackager {

    @Override
    public Archive<?> generateDeployment(TestDeployment testDeployment, Collection<ProtocolArchiveProcessor> collection) {

        Archive<?> appDeployment = testDeployment.getApplicationArchive();

        if (appDeployment instanceof ClassPathCompositeArchive) {
            for (Archive<?> archive : testDeployment.getAuxiliaryArchives()) {

                ((ClassPathCompositeArchive) appDeployment).add(archive, new BasicPath(), ZipExporter.class);
            }
            return appDeployment;
        } else {

            final JavaArchive archive = testDeployment.getApplicationArchive().as(JavaArchive.class);
            for (final Archive<?> auxArchive : testDeployment.getAuxiliaryArchives()) {
                archive.merge(auxArchive);
            }
            return archive;
        }
    }
}
