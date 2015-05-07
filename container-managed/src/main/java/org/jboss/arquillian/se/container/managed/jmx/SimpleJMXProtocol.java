package org.jboss.arquillian.se.container.managed.jmx;

import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentPackager;
import org.jboss.arquillian.protocol.jmx.AbstractJMXProtocol;
import org.jboss.arquillian.protocol.jmx.JMXProtocolConfiguration;
import org.jboss.arquillian.se.container.managed.SimpleDeploymentPackager;

/**
 * @author Tomas Remes
 */
public class SimpleJMXProtocol<T extends JMXProtocolConfiguration> extends AbstractJMXProtocol<JMXProtocolConfiguration> {
    
    public static final String NAME = "simple-jmx";
    
    @Override
    public String getProtocolName() {
        return NAME;
    }

    @Override
    public DeploymentPackager getPackager() {
        return new SimpleDeploymentPackager();
    }

}
