package org.crbt.champyapp.Service;

public interface NodeMainExecutorServiceListener {
    /**
     * @param nodeMainExecutorService the {@link NodeMainExecutorService} that was shut down
     */
    void onShutdown(NodeMainExecutorService nodeMainExecutorService);
}
