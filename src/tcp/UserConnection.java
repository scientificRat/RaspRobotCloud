package tcp;

/**
 * Created by huangzhengyue on 2016/10/29.
 */
public interface UserConnection {
    void sendVideoStream(byte[] image, int length);
    void sendForwardingData(byte[] data);
    void closeConnection();
}
