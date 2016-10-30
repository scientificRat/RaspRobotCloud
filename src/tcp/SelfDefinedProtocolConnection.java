package tcp;

import utility.GeneralJsonBuilder;

import java.io.IOException;

/**
 * Created by huangzhengyue on 2016/10/29.
 */
public abstract class SelfDefinedProtocolConnection extends TCPConnection {

    protected boolean needCloseAfterParsing = false;

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
                //读到头部，开始设定超时，每个byte必须在0.5s内传完
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
                if (needCloseAfterParsing) {
                    this.connected = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            sendStringData(GeneralJsonBuilder.error("error! " + e.toString()));
        } finally {
            doBeforeThreadEnd();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendStringData(String data) {
        byte[] strDataBytes = data.getBytes();
        byte[] sendData = new byte[5 + strDataBytes.length];
        sendData[0] = 'm';
        intToByteArray(strDataBytes.length, sendData, 1);
        for (int i = 0; i < strDataBytes.length; i++) {
            sendData[i + 5] = strDataBytes[i];
        }
        sendRawData(sendData);
    }

}
