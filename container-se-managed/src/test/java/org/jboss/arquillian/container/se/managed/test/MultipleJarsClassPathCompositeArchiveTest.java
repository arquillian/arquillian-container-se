package org.jboss.arquillian.container.se.managed.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.se.managed.archive.ClassPathCompositeArchiveBuilder;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Tomas Remes
 */
public class MultipleJarsClassPathCompositeArchiveTest extends Arquillian {

    @Deployment
    public static Archive<?> deploy() {

        
        JavaArchive first = ShrinkWrap.create(JavaArchive.class).addClass(MultipleJarsClassPathCompositeArchiveTest.class);
        JavaArchive second = ShrinkWrap.create(JavaArchive.class).addClass(TestBean.class);
        return ClassPathCompositeArchiveBuilder.create().addJavaArchive(first, second);
    }

    @Test
    public void multipleJarsTest() {
        TestBean test = new TestBean();
        Assert.assertNotNull(test);

    }
}
