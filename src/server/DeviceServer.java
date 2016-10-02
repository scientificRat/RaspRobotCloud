package server;

import tcp.DeviceConnection;

/**
 * Created by huangzhengyue on 27/09/2016.
 */
public class DeviceServer extends TCPServer {
    public DeviceServer(int port) {
        super(port, DeviceConnection.class);
    }
}
