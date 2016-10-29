package tcp;

import utility.GeneralJsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by huangzhengyue on 16/9/4.
 */
public abstract class TCPConnection extends Thread {

    protected Socket socket = null;

    protected boolean connected = true;

    protected InputStream inputStream = null;

    protected OutputStream outputStream = null;

    public TCPConnection() {

    }

    public void initialize(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }


    public void closeConnection() {
        this.connected = false;
        //force stop
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return connected;
    }

    protected synchronized void sendRawData(byte[] data) {
        try {
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected synchronized void sendRawData(byte[] data, int offset, int length) {
        try {
            outputStream.write(data, offset, length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void intToByteArray(int integer, byte[] buffer, int offset) {
        //(小端序)
        buffer[offset] = (byte) (integer & 0xFF);
        buffer[offset + 1] = (byte) ((integer >> 8) & 0xFF);
        buffer[offset + 2] = (byte) ((integer >> 16) & 0xFF);
        buffer[offset + 3] = (byte) ((integer >> 24) & 0xFF);
    }

    protected int byteToInt(byte[] b, int offset) {
        //小端序
        int temp;
        int n = 0;
        for (int i = offset + 3; i >= offset; i--) {
            n <<= 8;
            temp = b[i] & 0xff;
            n |= temp;
        }
        return n;
    }
    // 子类必须实现转发信息的接口
    public abstract void sendForwardingData(byte[] data);

}