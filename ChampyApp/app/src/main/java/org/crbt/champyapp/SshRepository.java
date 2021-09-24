package org.crbt.champyapp;

import androidx.lifecycle.LiveData;

public interface SshRepository {

    void startSession();

    void stopSession();

    LiveData<Boolean> isConnected();

    void sendMessage(String message);

    LiveData<String> getOutputData();

//    void updateSSH(SSHEntity ssh);

//    LiveData<SSHEntity> getCurrentSSH();
}
