package tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by huangzhengyue on 25/09/2016.
 */
public class TCPServerComponent {
    //the server socket
    private ServerSocket serverSocket =null;

    private int port;

    private Class connectionType = null;

    boolean started = false;

    public TCPServerComponent(int port, Class connectionType) {
        this.port = port;
        this.connectionType = connectionType;
    }

    //阻塞方法
    public void run(){
        started = true;
        try {
            serverSocket =new ServerSocket(port);
            while (started){
                Socket socket=serverSocket.accept();
                //use java reflection to generate new object
                TCPConnection tcpConnection = (TCPConnection) connectionType.newInstance();
                tcpConnection.setSocket(socket);
                tcpConnection.start();
            }

        } catch (IOException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            //release
            started = false;
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop(){
        started =false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TCPServerComponent tcpServerComponent = new TCPServerComponent(8902,UserConnection.class);
    }
}
