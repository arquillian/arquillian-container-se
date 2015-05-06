package org.jboss.arquillian.server;

import java.lang.management.ManagementFactory;
import java.util.logging.Logger;

import javax.management.JMException;
import javax.management.MBeanServer;

import org.jboss.arquillian.protocol.jmx.JMXTestRunner;

/**
 * @author Tomas Remes
 */
public class Main {

    private JMXTestRunner testRunner;
    private final Logger log = Logger.getLogger(Main.class.getName());

    public Main(ClassLoader classLoader) {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        try {
            testRunner = new JMXTestRunner(new TestClassLoader(classLoader));
            testRunner.registerMBean(mbs);
            log.info("JMXTestRunner initialized.");
        } catch (JMException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
       new Main(Thread.currentThread().getContextClassLoader());
        //some wait
        int i = 0;
         while (!AfterSuiteObserver.END.get()) {
        // while(i < 10000){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
