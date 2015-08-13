/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.arquillian.container.se.api.ClassPath;
import org.jboss.arquillian.container.se.managed.jmx.CustomJMXProtocol;
import org.jboss.arquillian.container.se.managed.util.ServerAwait;
import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.JMXContext;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.ArchiveAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

public class ManagedSEDeployableContainer implements DeployableContainer<ManagedSEContainerConfiguration> {

    private static final Logger LOGGER = Logger.getLogger(ManagedSEDeployableContainer.class.getName());
    private static final String SYSPROP_KEY_JAVA_HOME = "java.home";
    private static final String DEBUG_AGENT_STRING = "-agentlib:jdwp=transport=dt_socket,address=8787,server=y,suspend=y";
    private static final String TARGET = "target";
    private static final String SERVER_MAIN_CLASS_FQN = "org.jboss.arquillian.container.se.server.Main";
    private static final String SYSTEM_PROPERTY_SWITCH = "-D";
    private static final String EQUALS = "=";

    private boolean debugModeEnabled;
    private boolean keepDeploymentArchives;
    private Process process;
    private List<File> materializedTestDeployments;
    private List<File> dependenciesJars;
    private String host;
    private int port;
    private String librariesPath;
    private List<String> additionalJavaOpts;

    @Override
    public Class<ManagedSEContainerConfiguration> getConfigurationClass() {
        return ManagedSEContainerConfiguration.class;
    }

    public void setup(ManagedSEContainerConfiguration configuration) {
        debugModeEnabled = configuration.isDebug();
        host = configuration.getHost();
        port = configuration.getPort();
        materializedTestDeployments = new ArrayList<>();
        dependenciesJars = new ArrayList<>();
        librariesPath = configuration.getLibrariesPath();
        keepDeploymentArchives = configuration.isKeepDeploymentArchives();
        additionalJavaOpts = initAdditionalJavaOpts(configuration.getAdditionalJavaOpts());
        configureLogging(configuration);
    }

    private List<String> initAdditionalJavaOpts(String opts) {
        if (opts == null || opts.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> additionalOpts = new ArrayList<>();
        // TODO It may make sense to validate each option
        for (String option : opts.split("\\s+")) {
            additionalOpts.add(option);
        }
        return additionalOpts;
    }

    private void configureLogging(ManagedSEContainerConfiguration configuration) {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(configuration.getLogLevel());
        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(consoleHandler);
        LOGGER.setLevel(configuration.getLogLevel());
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
    public void undeploy(Archive<?> archive) throws DeploymentException {
        LOGGER.info("Undeploying " + archive.getName());
        if (!keepDeploymentArchives) {
            for (File materializedDeployment : materializedTestDeployments) {
                materializedDeployment.delete();
            }
        }
        // Kill the subprocess (test JVM)
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
    public ProtocolMetaData deploy(final Archive<?> archive) throws DeploymentException {
        LOGGER.info("Deploying " + archive.getName());

        // First of all clear the list of previously materialized deployments - otherwise the class path would grow indefinitely
        materializedTestDeployments.clear();

        if (ClassPath.isRepresentedBy(archive)) {
            for (Node child : archive.get(ClassPath.ROOT_ARCHIVE_PATH).getChildren()) {
                if (child.getAsset() instanceof ArchiveAsset) {
                    ArchiveAsset archiveAsset = (ArchiveAsset) child.getAsset();
                    materializeArchive(archiveAsset.getArchive());
                }
            }
        } else {
            materializeArchive(archive);
        }

        Properties systemProperties = getSystemProperties(archive);
        readJarFilesFromDirectory();

        List<String> processCommand = buildProcessCommand(systemProperties);
        logExecutedCommand(processCommand);
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

        int waitTime = debugModeEnabled ? 15 : 5;
        boolean connected = serverAwait(host, port, waitTime);
        if (!connected) {
            throw new DeploymentException("Child JVM process failed to start within " + waitTime + " seconds.");
        }

        ProtocolMetaData protocolMetaData = new ProtocolMetaData();
        protocolMetaData.addContext(new JMXContext(host, port));
        return protocolMetaData;
    }

    private Properties getSystemProperties(final Archive<?> archive) throws DeploymentException {
        Node systemPropertiesNode = archive.get(ClassPath.SYSTEM_PROPERTIES_ARCHIVE_PATH);
        if (systemPropertiesNode != null) {
            try (InputStream in = systemPropertiesNode.getAsset().openStream()) {
                Properties systemProperties = new Properties();
                systemProperties.load(in);
                return systemProperties;
            } catch (IOException e) {
                throw new DeploymentException("Could not load system properties", e);
            }
        }
        return null;
    }

    private boolean serverAwait(String host, int port, int waitTime) {
        ServerAwait serverAwait = new ServerAwait(host, port, waitTime);
        return serverAwait.run();
    }

    private void materializeArchive(Archive<?> archive) {
        File deploymentFile = new File(TARGET.concat(File.separator).concat(archive.getName()));
        // deployment archive can already exist if it wasn't deleted within undeployment
        if (!deploymentFile.exists()) {
            archive.as(ZipExporter.class).exportTo(deploymentFile);
        }
        materializedTestDeployments.add(deploymentFile);

    }

    private List<String> buildProcessCommand(Properties properties) {
        final List<String> command = new ArrayList<String>();
        final File javaHome = new File(System.getProperty(SYSPROP_KEY_JAVA_HOME));
        command.add(javaHome.getAbsolutePath() + File.separator + "bin" + File.separator + "java");
        command.add("-cp");
        StringBuilder builder = new StringBuilder();
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
            command.add(DEBUG_AGENT_STRING);
        }
        for (String option : additionalJavaOpts) {
            command.add(option);
        }
        if (properties != null) {
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                addSystemProperty(command, entry.getKey().toString(), entry.getValue().toString());
            }
        }
        command.add(SERVER_MAIN_CLASS_FQN);
        return command;
    }

    private void addSystemProperty(List<String> command, String key, String value) {
        command.add(SYSTEM_PROPERTY_SWITCH + key + EQUALS + value);
    }

    private void readJarFilesFromDirectory() throws DeploymentException {
        if (librariesPath == null) {
            return;
        }
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

    private void logExecutedCommand(List<String> processCommand) {
        if (LOGGER.isLoggable(Level.FINE)) {
            StringBuilder builder = new StringBuilder();
            for (String s : processCommand) {
                builder.append(s);
                builder.append(" ");
            }
            LOGGER.log(Level.FINE, "Executing command: " + builder);
        }
    }

}
