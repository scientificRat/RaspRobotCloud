package server;

import tcp.TCPServerComponent;

/**
 * Created by huangzhengyue on 27/09/2016.
 */
public abstract class TCPServer {
    private int port;
    TCPServerComponent tcpServerComponent = null;

    public TCPServer(int port, Class tcpConnectionType) {
        this.port = port;
        tcpServerComponent = new TCPServerComponent(port,tcpConnectionType);
    }

    public void start(){
        new Thread(() -> tcpServerComponent.run() ).start();
    }

    public void stop(){
        tcpServerComponent.stop();
    }

    public int getPort() {
        return port;
    }
}
