package org.crbt.champyapp.Connection;

import android.os.AsyncTask;
import android.util.Log;

import org.crbt.champyapp.MasterEntity;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

public class ConnectionCheckTask extends AsyncTask<MasterEntity, Void, Boolean> {

    private static final int TIMEOUT_TIME = 2 * 1000;

    private final ConnectionListener listener;

    public ConnectionCheckTask(ConnectionListener listener) {
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(MasterEntity... masterEnts) {
        MasterEntity masterEnt = masterEnts[0];
        return isHostAvailable(masterEnt.ip, masterEnt.port, TIMEOUT_TIME);
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success)
            listener.onSuccess();
        else
            listener.onFailed();
    }

    private boolean isHostAvailable(final String host, final int port, final int timeout) {
        try (final Socket socket = new Socket()) {
            final InetAddress inetAddress = InetAddress.getByName(host);
            final InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, port);

            socket.connect(inetSocketAddress, timeout);
            return true;

        } catch (ConnectException e) {
            Log.e("Connection", "Failed do to unavailable network.");

        }catch (SocketTimeoutException e) {
            Log.e("Connection", "Failed do to reach host in time.");

        } catch (UnknownHostException e) {
            Log.e("Connection", "Unknown host.");

        } catch (IOException e) {
            Log.e("Connection", "IO Exception.");
        }

        return false;
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4   true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getAndroidDeviceIp(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());

            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());

                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();

                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;

                        } else {

                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }
}
