package org.jboss.arquillian.container.se.server;

import org.jboss.arquillian.container.spi.event.container.AfterUnDeploy;
import org.jboss.arquillian.core.api.annotation.Observes;

/**
 * @author Tomas Remes
 */
public class AfterUndeployObserver {

    public void afterUndeploy(@Observes AfterUnDeploy event){
        Main.SYNC.countDown();
    }
}
