package datastruct;

import tcp.UserConnection;
import tcp.UserNonBrowserClientConnection;

import java.util.ArrayList;

/**
 * Created by huangzhengyue on 9/16/16.
 */
public class OnlineDeviceInfo {
    public final String deviceID;
    public ArrayList<UserConnection> forwardingConnections;

    public OnlineDeviceInfo(String deviceID, ArrayList<UserConnection> forwardingConnections) {
        this.deviceID = deviceID;
        this.forwardingConnections = forwardingConnections;
    }

    public OnlineDeviceInfo(String deviceID) {
        this.deviceID = deviceID;
        this.forwardingConnections = new ArrayList<>();
    }

    public String getDeviceID() {
        return deviceID;
    }

    public ArrayList<UserConnection> getForwardingConnections() {
        return forwardingConnections;
    }

    public void setForwardingConnections(ArrayList<UserConnection> forwardingConnections) {
        this.forwardingConnections = forwardingConnections;
    }
}
