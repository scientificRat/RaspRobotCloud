package server;

import tcp.DeviceConnection;

/**
 * Created by huangzhengyue on 27/09/2016.
 */
public class UserServer extends TCPServer{
    public UserServer(int port) {
        super(port, DeviceConnection.class);
    }
}
