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

    private boolean connected = true;

    private InputStream inputStream = null;

    private OutputStream outputStream = null;

    protected boolean needCloseAfterParsing = false;

    public TCPConnection() {
    }

    public TCPConnection(Socket socket) {
        this.socket = socket;
    }

    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    //处理信息
    protected abstract void parseMessage(byte[] dataHead, byte[] data);

    //连接异常时调用
    protected abstract void doBeforeThreadEnd();

    @Override
    public void run() {
        try {
            while (this.connected) {
                byte[] head = new byte[5];
                for (int i = 0; i < 5; i++) {
                    int c = inputStream.read();
                    if (c == -1) {
                        //头部不完整，直接断开连接
                        return;
                    }
                    head[i] = (byte) c;
                }
                //读到头部，开始设定超时，每个byte必须在0。5s内传完
                this.socket.setSoTimeout(500);
                int dataLength = byteToInt(head, 1);
                byte[] data = new byte[dataLength];
                for (int i = 0; i < dataLength; i++) {
                    int c = inputStream.read();
                    if (c == -1) {
                        //数据不完整
                        return;
                    }
                    data[i] = (byte) c;
                }
                this.socket.setSoTimeout(0);
                parseMessage(head, data);
                if(needCloseAfterParsing){
                    this.connected = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            sendStringData(GeneralJsonBuilder.error("error! "+e.toString()));
        } finally {
            doBeforeThreadEnd();
            try {
                inputStream.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    protected synchronized void sendRawData(byte[] data) {
        try {
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected synchronized void sendRawData(byte[] data,int offset,int length) {
        try {
            outputStream.write(data,offset,length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //不同连接应用层协议可能不同，因此需要实现此方法
    public abstract void sendMessage(byte[] data);


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

    public void sendStringData(String data) {
        byte[] strDataBytes = data.getBytes();
        byte[] sendData = new byte[5 + strDataBytes.length];
        sendData[0] = 'm';
        intToByteArray(strDataBytes.length, sendData, 1);
        for (int i = 0; i < strDataBytes.length; i++) {
            sendData[i + 5] = strDataBytes[i];
        }
        sendMessage(sendData);
    }

}