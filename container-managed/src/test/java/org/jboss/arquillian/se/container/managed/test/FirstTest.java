package org.jboss.arquillian.se.container.managed.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.se.container.managed.archive.ClassPathCompositeArchiveBuilder;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FirstTest extends Arquillian {

    @Deployment
    public static Archive<?> deploy() {
        
        JavaArchive first = ShrinkWrap.create(JavaArchive.class).addClass(BasicClassPathCompositeArchiveTest.class);
        return ClassPathCompositeArchiveBuilder.create().addJavaArchive(first);
    }

    @Test
    public void firstTest() {
        Assert.assertTrue(true);
    }

}
