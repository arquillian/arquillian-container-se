package org.jboss.arquillian.container.se.managed.jmx;

import java.util.Collection;

import org.jboss.arquillian.container.composite.archive.CompositeArchive;
import org.jboss.arquillian.container.test.spi.TestDeployment;
import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentPackager;
import org.jboss.arquillian.container.test.spi.client.deployment.ProtocolArchiveProcessor;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 * @author Tomas Remes
 */
public class TestDeploymentPackager implements DeploymentPackager {

    @Override
    public Archive<?> generateDeployment(TestDeployment testDeployment, Collection<ProtocolArchiveProcessor> collection) {

        final Archive<?> appDeployment = testDeployment.getApplicationArchive();
        if (appDeployment instanceof CompositeArchive) {
            CompositeArchive composite = (CompositeArchive) appDeployment;
            for (Archive<?> archive : testDeployment.getAuxiliaryArchives()) {
                composite.addItem(archive);
            }
            return appDeployment;
        } else {
            final JavaArchive archive = appDeployment.as(JavaArchive.class);
            for (final Archive<?> auxArchive : testDeployment.getAuxiliaryArchives()) {
                archive.merge(auxArchive);
            }
            return archive;
        }
    }
}
