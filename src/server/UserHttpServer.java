package server;

import tcp.UserHttpConnection;

/**
 * Created by huangzhengyue on 2016/10/15.
 */
public class UserHttpServer extends TCPServer {
    public UserHttpServer(int port) {
        super(port, UserHttpConnection.class);
    }
}
