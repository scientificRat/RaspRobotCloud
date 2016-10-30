package tcp;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import datastruct.dataobj.DeviceInfo;
import datastruct.dataobj.Request;
import exceptions.TCPServicesException;
import services.Services;
import utility.GeneralJsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by huangzhengyue on 27/09/2016.
 */
public class UserNonBrowserClientConnection extends SelfDefinedProtocolConnection implements UserConnection {

    private String mSessionID=null;

    public String getSessionID() {
        return this.mSessionID;
    }

    @Override
    protected void parseMessage(byte[] dataHead, byte[] data) {
        Services services = Services.getInstance();
        switch (dataHead[0]) {
            case 'r': {
                String strData = new String(data);
                Gson gson = new Gson();
                Request request;
                try {
                    request = gson.fromJson(strData, Request.class);
                } catch (JsonSyntaxException e) {
                    sendStringData(GeneralJsonBuilder.error("json data syntax error"));
                    return;
                }

                String type = request.getRequestType();
                if (type == null || type.isEmpty()) {
                    sendStringData(GeneralJsonBuilder.error("parameter requestType is required,[login/logout/detach/connect/query]"));
                    return;
                }
                // 默认返回信息后关闭连接
                this.needCloseAfterParsing = true;
                if ("logout".equals(type)) {
                    String sessionID = request.getSessionID();
                    this.mSessionID = sessionID;
                    if (sessionID == null || sessionID.isEmpty()) {
                        sendStringData(GeneralJsonBuilder.error("parameter sessionID is required"));
                        return;
                    }
                    try {
                        services.userLogout(sessionID);
                    } catch (TCPServicesException e) {
                        e.printStackTrace();
                        sendStringData(GeneralJsonBuilder.error(e.toString()));
                    }
                } else if ("login".equals(type)) {
                    String loginName = request.getLoginName();
                    String password = request.getPassword();
                    //check null
                    if (loginName == null || loginName.isEmpty()) {
                        sendStringData(GeneralJsonBuilder.error("parameter loginName is required"));
                        return;
                    }
                    if (password == null || password.isEmpty()) {
                        sendStringData(GeneralJsonBuilder.error("parameter password is required"));
                        return;
                    }
                    //do login
                    try {
                        String sessionID = services.userLogin(loginName, password);
                        sendStringData("{\"sessionID\":\"" + sessionID + "\"}");

                    } catch (TCPServicesException e) {
                        e.printStackTrace();
                        sendStringData(GeneralJsonBuilder.error(e.toString()));
                    }

                } else if ("connect".equals((type))) {
                    String sessionID = request.getSessionID();
                    this.mSessionID = sessionID;
                    if (sessionID == null || sessionID.isEmpty()) {
                        sendStringData(GeneralJsonBuilder.error("parameter sessionID is required"));
                        return;
                    }
                    String deviceID = request.getConnectingID();
                    if (deviceID == null || deviceID.isEmpty()) {
                        sendStringData(GeneralJsonBuilder.error("parameter connectingID is required"));
                        return;
                    }
                    //debug only:
                    if (deviceID.equals("MJW")) {
                        sendStringData(GeneralJsonBuilder.succuss(true));
                        //发送测试图片
                        try {
                            FileInputStream fileInputStream = new FileInputStream("test.jpg");
                            byte[] imageData = new byte[1024 * 1024];
                            int length = fileInputStream.read(imageData);
                            sendDebugImageData(imageData,length);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    try {
                        services.connectDevice(sessionID, deviceID, this);
                        sendStringData(GeneralJsonBuilder.succuss(true));
                        this.needCloseAfterParsing = false;
                    } catch (TCPServicesException e) {
                        e.printStackTrace();
                        sendStringData(GeneralJsonBuilder.error(e.toString()));
                    }
                } else if ("detach".equals(type)) {
                    String sessionID = request.getSessionID();
                    this.mSessionID =sessionID;
                    if (sessionID == null || sessionID.isEmpty()) {
                        sendStringData(GeneralJsonBuilder.error("parameter sessionID is required"));
                        return;
                    }
                    String deviceID = request.getConnectingID();
                    if (deviceID == null || deviceID.isEmpty()) {
                        sendStringData(GeneralJsonBuilder.error("parameter connectingID is required"));
                        return;
                    }
                    try {
                        services.detachDevice(sessionID, deviceID, this);
                    } catch (TCPServicesException e) {
                        e.printStackTrace();
                        sendStringData(GeneralJsonBuilder.error(e.toString()));
                    }
                } else if ("query".equals(type)) {
                    String sessionID = request.getSessionID();
                    this.mSessionID =sessionID;
                    if (sessionID == null || sessionID.isEmpty()) {
                        sendStringData(GeneralJsonBuilder.error("parameter sessionID is required"));
                        return;
                    }
                    //debug only:
                    String debug = request.getDebug();
                    if("debug".equals(debug)){
                        ArrayList<DeviceInfo> deviceInfoArrayList = new ArrayList<>();
                        DeviceInfo deviceInfo = new DeviceInfo();
                        deviceInfo.setDeviceID("MJW");
                        deviceInfo.setHardwareDescription("测试用树莓派小车");
                        deviceInfoArrayList.add(deviceInfo);
                        deviceInfo.setDeviceID("fucker");
                        deviceInfo.setHardwareDescription("测试");
                        deviceInfoArrayList.add(deviceInfo);
                        sendStringData(gson.toJson(deviceInfoArrayList));
                        return;
                    }
                    //返回在线设备列表
                    sendStringData(gson.toJson(Services.getInstance().queryOnlineDevices(sessionID)));
                } else {
                    sendStringData(GeneralJsonBuilder.error("You are silly B, parameter requestType is required,[login/logout/detach/connect/query]"));
                }
                break;
            }
            case 'c': {
                // 转发命令
                services.userToDeviceForwarding(this, dataHead, data);
                break;
            }
            default: {
                sendStringData(GeneralJsonBuilder.error("undefined head type"));
                this.needCloseAfterParsing = true;
                break;
            }
        }
    }

    //debug only
    public void sendDebugImageData(byte[] image,int len){
        sendVideoStream(image,0,len);
    }

    @Override
    protected void doBeforeThreadEnd() {
        if (needCloseAfterParsing) {
            this.closeConnection();
        }
    }

    @Override
    public void sendVideoStream(byte[] data,int imageOffset, int length) {
        byte[] sendData = new byte[5 + length];
        sendData[0] = 'v';
        intToByteArray(length, sendData, 1);
        System.arraycopy(data,imageOffset,sendData,5,length);
        sendRawData(sendData);
    }

    @Override
    public void sendForwardingData(byte[] data) {
        //原封不动转发
        sendRawData(data);
    }

}
