package org.jboss.arquillian.server;

import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.AfterSuite;

/**
 * @author Tomas Remes
 */
public class AfterSuiteObserver {

    public void afterSuite(@Observes AfterSuite event){
        Main.SYNC.countDown();
    }
}
