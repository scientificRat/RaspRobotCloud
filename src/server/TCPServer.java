package server;

import tcp.TCPConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by huangzhengyue on 27/09/2016.
 */
public class TCPServer extends Thread {

    //the server socket
    private ServerSocket serverSocket = null;

    private int port;

    private Class<? extends TCPConnection> connectionType;

    public TCPServer(int port, Class<? extends TCPConnection> connectionType) {
        this.port = port;
        this.connectionType = connectionType;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                //use java reflection to generate new object
                TCPConnection tcpConnection = connectionType.newInstance();
                tcpConnection.initialize(socket);
                tcpConnection.start();
            }

        } catch (IOException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            //release
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void cancel() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return port;
    }
}
