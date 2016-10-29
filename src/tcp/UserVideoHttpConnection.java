package tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by huangzhengyue on 2016/10/15.
 */
public class UserVideoHttpConnection extends TCPConnection implements UserConnection {

    private final String BOUNDARY = "boundarydonotcross";
    private final String STD_HEADER = "Connection: close\r\n" +
            "Server: MJPG-Streamer/0.2\r\n" +
            "Cache-Control: no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\r\n" +
            "Pragma: no-cache\r\n" +
            "Expires: Mon, 3 Jan 2000 12:34:56 GMT\r\n";

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            //deal input
            byte[] input = new byte[1024];
            int c = 0;
            for (int i = 0; i < input.length && c != '\n'; i++) {
                c = inputStream.read();
                if (c == -1) {
                    return;
                }
                input[i] = (byte) c;
            }
            String request = new String(input);
            String sub= request.substring(request.indexOf("GET /?id="));

            //deal output
            startSendStream(outputStream);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void startSendStream(OutputStream outputStream) throws IOException{
        String resp= "HTTP/1.0 200 OK\r\n" +
                STD_HEADER +
                "Content-Type: multipart/x-mixed-replace;boundary=" + BOUNDARY + "\r\n" +
                "\r\n" +
                "--" + BOUNDARY + "\r\n";
        outputStream.write(resp.getBytes());
    }

    @Override
    public void sendVideoStream(byte[] image, int length) {

    }

    @Override
    public void sendForwardingData(byte[] data) {

    }
}
