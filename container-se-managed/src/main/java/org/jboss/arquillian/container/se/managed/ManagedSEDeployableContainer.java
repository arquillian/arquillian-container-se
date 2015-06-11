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
package org.jboss.arquillian.container.se.managed;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jboss.arquillian.container.se.managed.jmx.CustomJMXProtocol;
import org.jboss.arquillian.container.se.managed.util.ServerAwait;
import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.JMXContext;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;
import org.jboss.shrinkwrap.impl.base.filter.IncludeRegExpPaths;

public class ManagedSEDeployableContainer implements DeployableContainer<ManagedSEContainerConfiguration> {

    private static final Logger log = Logger.getLogger(ManagedSEDeployableContainer.class.getName());
    private static final String SYSPROP_KEY_JAVA_HOME = "java.home";
    private static final String X_DEBUG = "-Xdebug";
    private static final String DEBUG_AGENT_STRING = "-Xrunjdwp:server=y,transport=dt_socket,address=8787,suspend=y";
    private static final String TARGET = "target";
    private static final String SERVER_JAR_NAME = "server.jar";
    private static final String REGEX_FOR_JAR_FILE = "([^\\s]+(\\.(?i)jar)$)";
    private static final String SERVER_MAIN_CLASS_FQN = "org.jboss.arquillian.container.se.server.Main";

    private boolean debugModeEnabled;
    private Process process;
    private List<File> materializedTestDeployments;
    private List<File> dependenciesJars;
    private String host;
    private int port;
    private String librariesPath;

    @Override
    public Class getConfigurationClass() {
        return ManagedSEContainerConfiguration.class;
    }

    public void setup(ManagedSEContainerConfiguration configuration) {
        debugModeEnabled = configuration.isDebug();
        host = configuration.getHost();
        port = configuration.getPort();
        materializedTestDeployments = new ArrayList<>();
        dependenciesJars = new ArrayList<>();
        librariesPath = configuration.getLibrariesPath();
    }

    @Override
    public void start() throws LifecycleException {
    }

    @Override
    public void stop() throws LifecycleException {
    }

    @Override
    public ProtocolDescription getDefaultProtocol() {
        return new ProtocolDescription(CustomJMXProtocol.NAME);
    }

    @Override
    public void deploy(Descriptor descriptor) throws DeploymentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void undeploy(Descriptor descriptor) throws DeploymentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void undeploy(Archive archive) throws DeploymentException {
        log.info("Undeploying " + archive.getName());
        for (File materializedDeployment : materializedTestDeployments) {
            materializedDeployment.delete();
        }
        materializedTestDeployments = new ArrayList<>();
        //destroy process
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

    @Override
    public ProtocolMetaData deploy(final Archive archive) throws DeploymentException {

        log.info("Deploying " + archive.getName());
        Map<ArchivePath, Node> content = archive.getContent(new IncludeRegExpPaths(REGEX_FOR_JAR_FILE));

        // if size is greater than 0 then we get composite archive
        if (content.size() > 0) {
            for (Map.Entry<ArchivePath, Node> entry : content.entrySet()) {
                JavaArchive arch = ShrinkWrap.create(JavaArchive.class, entry.getValue().toString());
                arch.as(ZipImporter.class).importFrom(entry.getValue().getAsset().openStream());
                materializeArchive(arch);
            }
        } else {
            materializeArchive(archive);
        }
        readJarFilesFromDirectory();

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

        if (debugModeEnabled) {
            serverAwait(host, port, 15);
        } else {
            serverAwait(host, port, 5);
        }
        ProtocolMetaData protocolMetaData = new ProtocolMetaData();
        protocolMetaData.addContext(new JMXContext(host, port));
        return protocolMetaData;
    }

    private void serverAwait(String host, int port, int countDownLatch) {

        ServerAwait serverAwait = new ServerAwait(host, port, countDownLatch);
        serverAwait.run();
    }

    private void materializeArchive(Archive<?> archive) {
        File deploymentFile = new File(TARGET.concat(File.separator).concat(archive.getName()));
        archive.as(ZipExporter.class).exportTo(deploymentFile);
        materializedTestDeployments.add(deploymentFile);
    }

    private List<String> buildProcessCommand(String archiveName) {
        final List<String> command = new ArrayList<String>();
        final File javaHome = new File(System.getProperty(SYSPROP_KEY_JAVA_HOME));
        command.add(javaHome.getAbsolutePath() + File.separator + "bin" + File.separator + "java");
        command.add("-cp");
        StringBuilder builder = new StringBuilder(TARGET + File.separator + SERVER_JAR_NAME);
        for (File materializedDeployment : materializedTestDeployments) {
            builder.append(File.pathSeparator + TARGET + File.separator + materializedDeployment.getName());
        }
        for (File dependencyJar : dependenciesJars) {
            builder.append(File.pathSeparator + dependencyJar.getPath());
        }
        command.add(builder.toString());
        command.add("-Dcom.sun.management.jmxremote");
        command.add("-Dcom.sun.management.jmxremote.port=" + port);
        command.add("-Dcom.sun.management.jmxremote.authenticate=false");
        command.add("-Dcom.sun.management.jmxremote.ssl=false");
        if (debugModeEnabled) {
            command.add(X_DEBUG);
            command.add(DEBUG_AGENT_STRING);
        }
        command.add(SERVER_MAIN_CLASS_FQN);
        return command;
    }

    private void readJarFilesFromDirectory() throws DeploymentException {
        File lib = new File(librariesPath);
        if (!lib.exists() || lib.isFile()) {
            throw new DeploymentException("Cannot read files from " + librariesPath);
        }

        File[] dep = lib.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });
        dependenciesJars.addAll(Arrays.asList(dep));

    }

}
