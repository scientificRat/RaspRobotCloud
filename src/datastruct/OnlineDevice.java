package datastruct;

import tcp.TCPConnection;

import java.util.ArrayList;

/**
 * Created by huangzhengyue on 9/16/16.
 */
public class OnlineDevice {
    public final String deviceID;
    public ArrayList<TCPConnection> forwardingConnections;

    public OnlineDevice(String deviceID, ArrayList<TCPConnection> forwardingConnections) {
        this.deviceID = deviceID;
        this.forwardingConnections = forwardingConnections;
    }

    public OnlineDevice(String deviceID) {
        this.deviceID = deviceID;
        this.forwardingConnections = new ArrayList<>();
    }


}
