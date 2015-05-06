/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.se.container.managed;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.JMXContext;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.arquillian.se.container.managed.jmx.SimpleJMXProtocol;
import org.jboss.arquillian.se.container.managed.util.ServerWarmUp;
import org.jboss.arquillian.server.Main;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;
import org.jboss.shrinkwrap.impl.base.exporter.zip.ZipExporterImpl;

public class ManagedSEDeployableContainer implements DeployableContainer<ManagedSEContainerConfiguration> {

    private static final Logger log = Logger.getLogger(ManagedSEDeployableContainer.class.getName());
    private static final String SYSPROP_KEY_JAVA_HOME = "java.home";
    private static final String X_DEBUG = "-Xdebug";
    private static final String DEBUG_AGENT_STRING = "-Xrunjdwp:server=y,transport=dt_socket,address=5005,suspend=y";
    private static final String TARGET = "target";
    private static final String SERVER_JAR_NAME = "server.jar";

    private boolean debugModeEnabled;
    private Process process;
    private File materializedDeployment;
    private String host;
    private int port;

    @Override
    public Class getConfigurationClass() {
        return ManagedSEContainerConfiguration.class;
    }

    public void setup(ManagedSEContainerConfiguration configuration) {
        debugModeEnabled = configuration.isDebug();
        host = configuration.getHost();
        port = configuration.getPort();
    }

    @Override
    public void start() throws LifecycleException {
    }

    @Override
    public void stop() throws LifecycleException {
    }

    @Override
    public ProtocolDescription getDefaultProtocol() {
        return new ProtocolDescription(SimpleJMXProtocol.NAME);
    }

    @Override
    public void deploy(Descriptor descriptor) throws DeploymentException {

    }

    @Override
    public void undeploy(Descriptor descriptor) throws DeploymentException {

    }

    @Override
    public void undeploy(Archive archive) throws DeploymentException {
        log.info("Undeploying " + archive.getName());
        materializedDeployment.delete();
        //destroy process
        final Runnable shutdownServerRunnable = new Runnable() {
            @Override
            public void run() {
                if (process != null) {
                    process.destroy();
                    try {
                        process.waitFor();
                    } catch (final InterruptedException e) {
                        Thread.interrupted();
                        throw new RuntimeException("Interrupted while awaiting server daemon process termination", e);
                    }
                }
            }
        };
        shutdownServerRunnable.run();
    }

    @Override
    public ProtocolMetaData deploy(final Archive archive) throws DeploymentException {

        log.info("Deploying " + archive.getName());
        materializeArchive(archive);

        List<String> processCommand = buildProcessCommand(archive.getName());
        // Launch the process
        final ProcessBuilder processBuilder = new ProcessBuilder(processCommand);
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);

        try {
            process = processBuilder.start();
        } catch (final IOException e) {
            throw new DeploymentException("Could not start process", e);
        }

        serverWarmUp(host, port, 5);
        ProtocolMetaData protocolMetaData = new ProtocolMetaData();
        protocolMetaData.addContext(new JMXContext(host, port));
        return protocolMetaData;
    }

    private void serverWarmUp(String host, int port, int countDownLatch) {

        ServerWarmUp socketTimeoutCheck = new ServerWarmUp(host, port, countDownLatch);
        socketTimeoutCheck.run();
    }

    private void materializeArchive(Archive<?> archive) {
        ZipExporterImpl expt = new ZipExporterImpl(archive);
        materializedDeployment = new File(TARGET.concat(File.separator).concat(archive.getName()));
        expt.exportTo(materializedDeployment);
    }

    private List<String> buildProcessCommand(String archiveName) {
        final List<String> command = new ArrayList<String>();
        final File javaHome = new File(System.getProperty(SYSPROP_KEY_JAVA_HOME));
        command.add(javaHome.getAbsolutePath() + File.separator + "bin" + File.separator + "java");
        command.add("-cp");
        command.add(TARGET + File.separator + SERVER_JAR_NAME + File.pathSeparator + TARGET + File.separator + archiveName);
        command.add("-Dcom.sun.management.jmxremote");
        command.add("-Dcom.sun.management.jmxremote.port=" + port);
        command.add("-Dcom.sun.management.jmxremote.authenticate=false");
        command.add("-Dcom.sun.management.jmxremote.ssl=false");
        if (debugModeEnabled) {
            command.add(X_DEBUG);
            command.add(DEBUG_AGENT_STRING);
        }
        command.add(Main.class.getName());
        return command;
    }

}
