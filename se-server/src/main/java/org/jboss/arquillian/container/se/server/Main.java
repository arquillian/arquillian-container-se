package org.jboss.arquillian.container.se.server;

import java.lang.management.ManagementFactory;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import javax.management.JMException;
import javax.management.MBeanServer;

import org.jboss.arquillian.protocol.jmx.JMXTestRunner;

/**
 * @author Tomas Remes
 */
public class Main {

    public static final CountDownLatch SYNC = new CountDownLatch(1);
    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        try {
            JMXTestRunner testRunner = new JMXTestRunner(new TestClassLoader(Main.class.getClassLoader()));
            testRunner.registerMBean(mbs);
            log.info("JMXTestRunner initialized.");
        } catch (JMException e) {
            throw new RuntimeException("Unable to register JMXTestRunner", e);
        }
        try {
            SYNC.await();
        } catch (InterruptedException e) {
            Thread.interrupted();
            throw new RuntimeException("Interrupted waiting for undeploy signal", e);
        }
    }
}
