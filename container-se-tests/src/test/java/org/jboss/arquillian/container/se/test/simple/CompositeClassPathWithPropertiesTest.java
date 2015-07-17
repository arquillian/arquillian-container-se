package org.jboss.arquillian.container.se.test.simple;

import java.util.Properties;

import junit.framework.Assert;
import org.jboss.arquillian.container.se.api.ClassPath;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CompositeClassPathWithPropertiesTest {

    private static final String VALUE1 = "value1";
    private static final String VALUE2 = "value2";
    private static final String KEY1 = "property1";
    private static final String KEY2 = "property2";

    @Deployment
    public static Archive<?> getDeployment() {
        Properties properties = new Properties();
        properties.put(KEY1, VALUE1);
        properties.put(KEY2, VALUE2);
        final JavaArchive test = ShrinkWrap.create(JavaArchive.class, "test.jar").addClass(CompositeClassPathWithPropertiesTest.class);
        return ClassPath.builder().add(test).addSystemProperties(properties).build();
    }

    @Test
    public void testPropertiesInArchive() {
        Assert.assertEquals(VALUE1, System.getProperty(KEY1));
        Assert.assertEquals(VALUE2, System.getProperty(KEY2));
    }
}
