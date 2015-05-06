package org.jboss.arquillian.server;

import java.util.logging.Logger;

import org.jboss.arquillian.protocol.jmx.JMXTestRunner;

/**
 * @author Tomas Remes
 */
public class TestClassLoader implements JMXTestRunner.TestClassLoader {
    private final ClassLoader testClassLoader;
    private final Logger log = Logger.getLogger(TestClassLoader.class.getName());

    TestClassLoader(ClassLoader classLoader) {
        testClassLoader = classLoader;
    }

    @Override
    public Class<?> loadTestClass(String className) throws ClassNotFoundException {
        log.info("Loading class " + className);
        return testClassLoader.loadClass(className);
    }
}
