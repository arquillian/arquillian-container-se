package org.jboss.arquillian.se.container.managed;

import java.util.Collection;

import org.jboss.arquillian.container.test.spi.TestDeployment;
import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentPackager;
import org.jboss.arquillian.container.test.spi.client.deployment.ProtocolArchiveProcessor;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 * @author Tomas Remes
 */
public class SimpleDeploymentPackager implements DeploymentPackager {
    
    @Override
    public Archive<?> generateDeployment(TestDeployment testDeployment, Collection<ProtocolArchiveProcessor> collection) {

        final JavaArchive archive = testDeployment.getApplicationArchive().as(JavaArchive.class);
        for (final Archive<?> auxArchive : testDeployment.getAuxiliaryArchives()) {
            archive.merge(auxArchive);
        }
        return archive;
    }
}
