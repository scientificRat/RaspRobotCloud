package tcp;

import exceptions.TCPServicesException;
import services.Services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Timestamp;
import java.util.Date;

/**
 * Created by huangzhengyue on 2016/10/15.
 */
// 注意，本http连接只负责传视频，其它的用tomcat的http服务
public class UserVideoHttpConnection extends TCPConnection implements UserConnection {

    private String mSessionID = null;


    private final String BOUNDARY = "boundarydonotcross";
    private final String STD_HEADER = "Connection: Keep-Alive\r\n" +
            "Server: MJPG-Streamer/0.2\r\n" +
            "Cache-Control: no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\r\n" +
            "Pragma: no-cache\r\n" +
            "Expires: Mon, 3 Jan 2000 12:34:56 GMT\r\n";

    private final String NORMAL_HTTP_HEADER = "HTTP/1.1 200\r\n" +
            "Accept-Ranges: bytes\r\n" +
            "Last-Modified: Tue, 20 Sep 2016 12:04:40 GMT \n" +
            "Content-Type: text/html\r\n";

    public String getSessionID() {
        return this.mSessionID;
    }

    @Override
    public void run() {
        try {
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

            int reqStart=request.indexOf("GET /?mj=");

            if(reqStart<0){
                printError("parameter mj is required");
                return;
            }

            String sub = request.substring(reqStart);
            if (sub.length() < 10) {
                printError("parameter cannot be none");
            }
            char[] strArray = sub.toCharArray();
            int i;
            for (i = 9; i < strArray.length; i++) {
                if (strArray[i] == ' ') {
                    break;
                }
            }
            String parameters = sub.substring(9, i);
            String[] parameterArray = parameters.split(",");
            if (parameterArray.length != 2) {
                printError("parameter is separate by comma, the first one is the sessionID, the second one is the deviceID");
                return;
            }
            //设置session id
            this.mSessionID = parameterArray[0];
            //debug
            System.out.println(parameters);
            //准备发送视频
            startSendStream();
            Services services = Services.getInstance();
            services.connectDevice(parameterArray[0], parameterArray[1], this);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void startSendStream() throws IOException {
        String resp = "HTTP/1.0 200 OK\r\n" +
                STD_HEADER +
                "Content-Type: multipart/x-mixed-replace;boundary=" + BOUNDARY + "\r\n" +
                "\r\n" +
                "--" + BOUNDARY + "\r\n";
        outputStream.write(resp.getBytes());
    }

    @Override
    public void sendVideoStream(byte[] data, int off, int length) {
        try {
            StringBuilder stringBuilder =new StringBuilder();
            stringBuilder.append("Content-Type: image/jpeg\r\nContent-Length: ");
            stringBuilder.append(length);
            stringBuilder.append("\r\n\r\n");
            // send header
            outputStream.write(stringBuilder.toString().getBytes());
            //send image
            outputStream.write(data,off,length);
            //send end
            stringBuilder = new StringBuilder();
            stringBuilder.append("\r\n--");
            stringBuilder.append(BOUNDARY);
            stringBuilder.append("\r\n");
            outputStream.write(stringBuilder.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            Services services =Services.getInstance();
            try {
                services.userLogout("http");
            } catch (TCPServicesException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void sendForwardingData(byte[] data) {
        sendVideoStream(data,5,data.length-5);
    }

    public void printError(String content) {
        Date date = new Date();
        String send = NORMAL_HTTP_HEADER + "Content-Length: " + content.length() + "\r\n" + "Date: " + date.toString() + "\r\n\r\n" + content;
        try {
            outputStream.write(send.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
