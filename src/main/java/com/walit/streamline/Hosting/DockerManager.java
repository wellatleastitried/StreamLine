package com.walit.streamline.Hosting;

import java.io.IOException;
import java.lang.InterruptedException;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.time.Duration;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.RestartPolicy;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;

import com.walit.streamline.Utilities.Internal.StreamLineConstants;
import com.walit.streamline.Utilities.Internal.StreamLineMessages;

public class DockerManager {

    private final DockerClient dockerClient;
    private final Logger logger;

    public DockerManager(Logger logger, boolean wantStart) {
        this.logger = logger;
        if (wantStart) {
            DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
            dockerClient = DockerClientBuilder.getInstance(config).build();
        } else {
            dockerClient = null;
        }
    }

    public String startInvidiousContainer() {
        if (!isDockerInstalled()) {
            logger.log(Level.WARNING, StreamLineMessages.DockerNotInstalledError.getMessage());
            return null;
        } else if (!isDockerRunning()) {
            logger.log(Level.WARNING, StreamLineMessages.DockerNotRunningError.getMessage());
            return null;
        }
        System.out.println("Pulling Invidious Docker image...");
        dockerClient.pullImageCmd(StreamLineConstants.INVIDIOUS_IMAGE).start();

        boolean containerExists = dockerClient.listContainersCmd()
            .withShowAll(true)
            .exec()
            .stream()
            .anyMatch(container -> container.getNames()[0].equals("/" + StreamLineConstants.CONTAINER_NAME));
        if (containerExists) {
            System.out.println("Container already exists. Starting it...");
            dockerClient.startContainerCmd(StreamLineConstants.CONTAINER_NAME).exec();
        } else {
            System.out.println("Creating and starting Invidious container...");

            ExposedPort exposedPort = ExposedPort.tcp(StreamLineConstants.INVIDIOUS_PORT);
            Ports portBindings = new Ports();
            portBindings.bind(exposedPort, Ports.Binding.bindPort(StreamLineConstants.INVIDIOUS_PORT));

            CreateContainerResponse container = dockerClient.createContainerCmd(StreamLineConstants.INVIDIOUS_IMAGE)
                .withName(StreamLineConstants.CONTAINER_NAME)
                .withHostConfig(HostConfig.newHostConfig()
                        .withPortBindings(portBindings)
                        .withRestartPolicy(RestartPolicy.alwaysRestart()))
                .exec();

            dockerClient.startContainerCmd(container.getId()).exec();
        }
        if (isContainerRunning()) {
            System.out.println("Invidious container is running!");
            return String.format("localhost:%d", StreamLineConstants.INVIDIOUS_PORT);
        }
        System.out.println("Unable to start docker instance, only offline functionality will be available.");
        return null;
    }

    private boolean runCommand(String command) {
        try {
            Process process = new ProcessBuilder(command.split(" ")).start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (InterruptedException | IOException iE) {
            System.out.println(StreamLineMessages.DockerNotInstalledError.getMessage());
            return false;
        }
    }

    public void removeContainer() {
        dockerClient.removeContainerCmd(StreamLineConstants.CONTAINER_NAME).exec();
        logger.log(Level.INFO, "Container has been removed.");
    }

    public boolean userHasPermissionsForDocker() {
        return runCommand("docker ps");
    }

    private boolean isDockerInstalled() {
        return runCommand("docker --version");
    }

    private boolean isDockerRunning() {
        return runCommand("docker info");
    }

    public void stopContainer() {
        dockerClient.stopContainerCmd(StreamLineConstants.CONTAINER_NAME).exec();
        logger.log(Level.INFO, "Container has been stopped.");
    }

    public boolean isContainerRunning() {
        InspectContainerResponse response = dockerClient.inspectContainerCmd(StreamLineConstants.CONTAINER_NAME).exec();
        return response.getState().getRunning();
    }
}
