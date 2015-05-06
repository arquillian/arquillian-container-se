package org.jboss.arquillian.server;

import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.AfterSuite;

/**
 * @author Tomas Remes
 */
public class AfterSuiteObserver {

    public static AtomicBoolean END = new AtomicBoolean(false);

    public void afterSuite(@Observes AfterSuite event){
        END.set(true);
    }
}
