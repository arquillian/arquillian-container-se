package org.jboss.arquillian.container.se.server;

import java.lang.management.ManagementFactory;
import java.util.logging.Logger;

import javax.management.JMException;
import javax.management.MBeanServer;

import org.jboss.arquillian.protocol.jmx.JMXTestRunner;

/**
 * @author Tomas Remes
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private static final long DEFAULT_TIMEOUT = 60000L;

    public static void main(String[] args) {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            JMXTestRunner testRunner = new JMXTestRunner(new TestClassLoader(Main.class.getClassLoader()));
            testRunner.registerMBean(mbs);
            LOGGER.info("JMXTestRunner initialized");
        } catch (JMException e) {
            throw new RuntimeException("Unable to register JMXTestRunner", e);
        }
        // Wait for ManagedSEDeployableContainer to kill the forked JVM
        // This process may not be terminated before with the client test finishes
        try {
            // TODO the timeout should be configurable
            Thread.sleep(DEFAULT_TIMEOUT);
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted waiting for undeploy signal", e);
        }
    }
}
