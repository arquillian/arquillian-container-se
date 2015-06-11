package org.jboss.arquillian.container.se.managed.jmx;

import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentPackager;
import org.jboss.arquillian.protocol.jmx.AbstractJMXProtocol;

/**
 * @author Tomas Remes
 */
public class CustomJMXProtocol extends AbstractJMXProtocol {
    
    public static final String NAME = "simple-jmx";
    
    @Override
    public String getProtocolName() {
        return NAME;
    }

    @Override
    public DeploymentPackager getPackager() {
        return new TestDeploymentPackager();
    }

}
