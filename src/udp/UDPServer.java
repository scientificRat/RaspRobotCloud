package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by huangzhengyue on 9/16/16.
 */
public class UDPServer {
    //singleton
    private static UDPServer ourInstance = new UDPServer();

    private Thread thread;

    public static UDPServer getInstance() {
        return ourInstance;
    }

    private UDPServer() {
        thread = new Thread(UDPServer::run);
    }

    public void start(){
        thread.start();
    }

    private static void run(){
        try {
            DatagramSocket datagramSocket = new DatagramSocket(8902);
            while (true){
                byte[] buffer = new byte[3*1024*1024];
                DatagramPacket datagramPacket = new DatagramPacket(buffer,buffer.length);
                datagramSocket.receive(datagramPacket);
                new PacketParser(datagramSocket,datagramPacket).start();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
