package com.streamline.backend.jobs;

import com.streamline.backend.DockerManager;
import com.streamline.backend.InvidiousHandle;
import com.streamline.utilities.internal.Config;
import com.streamline.utilities.internal.StreamLineConstants;

import org.tinylog.Logger;

public class ConnectionMonitorJob extends StreamLineJob {

    public ConnectionMonitorJob(Config config) {
        super(config);
    }

    public void execute() {

        /* If the audio source is not Docker, there is no reason to be testing the connection. */
        if (config.getAudioSource() != 'd') {
            return;
        }

        try {
            String reachableHost = InvidiousHandle.getWorkingHostnameFromApiOrDocker();
            boolean dockerIsResponding = canReachDocker();
            while (reachableHost == null && !dockerIsResponding) {
                reachableHost = InvidiousHandle.getWorkingHostnameFromApiOrDocker();
                if (reachableHost == null) {
                    dockerIsResponding = canReachDocker();
                }
                Thread.sleep(1000);
            }
            if (reachableHost != null) {
                config.setHost(reachableHost);
                config.setIsOnline(true);
            } else if (dockerIsResponding) {
                config.setHost(StreamLineConstants.INVIDIOUS_INSTANCE_ADDRESS);
                config.setIsOnline(true);
            }
            finish();
        } catch (InterruptedException iE) {
            Logger.warn("[!] Encountered an error while attempting to establish a network connection in the background.");
            cancel();
        }
    }

    private boolean canReachDocker() throws InterruptedException {
        return DockerManager.containerIsAlive() && DockerManager.canConnectToContainer();
    }
}
