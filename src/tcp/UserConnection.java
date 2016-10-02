package tcp;

import com.google.gson.Gson;
import datastruct.dataobj.Request;
import exceptions.TCPServicesException;
import services.Services;
import utility.GeneralJsonBuilder;

/**
 * Created by huangzhengyue on 27/09/2016.
 */
public class UserConnection extends TCPConnection {
    
    private boolean needCloseAfterParsing = false;

    @Override
    protected void parseMessage(byte[] dataHead, byte[] data) {
        Services services = Services.getInstance();
        switch (dataHead[0]){
            case 'r':{
                String strData = new String(data);
                Gson gson = new Gson();
                Request request=gson.fromJson(strData, Request.class);
                String type =request.getRequestType();
                if(type == null || type.isEmpty()){
                    sendStringData(GeneralJsonBuilder.error("parameter type is required"));
                    return;
                }
                if("logout".equals(type)){
                    String sessionID= request.getSessionID();
                    if(sessionID==null||sessionID.isEmpty()){
                        sendStringData(GeneralJsonBuilder.error("parameter sessionID is required"));
                        return;
                    }
                    try {
                        services.userLogout(sessionID);
                    } catch (TCPServicesException e) {
                        e.printStackTrace();
                        sendStringData(GeneralJsonBuilder.error(e.toString()));
                    }
                    this.needCloseAfterParsing = true;
                }
                else if("login".equals(type)){
                    String loginName = request.getLoginName();
                    String password = request.getPassword();
                    //check null
                    if(loginName==null || loginName.isEmpty()){
                        sendStringData(GeneralJsonBuilder.error("parameter loginName is required"));
                        return;
                    }
                    if(password ==null ||password.isEmpty()){
                        sendStringData(GeneralJsonBuilder.error("parameter password is required"));
                        return;
                    }
                    //do login
                    try {
                        String sessionID=services.userLogin(loginName,password);
                        sendStringData("{\"sessionID\":\""+sessionID+"\"}");

                    } catch (TCPServicesException e) {
                        e.printStackTrace();
                        sendStringData(GeneralJsonBuilder.error(e.toString()));
                    }
                    this.needCloseAfterParsing = true;
                }
                else if("connection".equals((type))){
                    String sessionID= request.getSessionID();
                    if(sessionID==null||sessionID.isEmpty()){
                        sendStringData(GeneralJsonBuilder.error("parameter sessionID is required"));
                        return;
                    }
                    String deviceID = request.getConnectingID();
                    if(deviceID==null||deviceID.isEmpty()){
                        sendStringData(GeneralJsonBuilder.error("parameter connectingID is required"));
                        return;
                    }
                    try {
                        services.connectDevice(sessionID,deviceID,this);
                    } catch (TCPServicesException e) {
                        e.printStackTrace();
                        sendStringData(GeneralJsonBuilder.error(e.toString()));
                    }

                }
                else if("detach".equals(type)){
                    String sessionID= request.getSessionID();
                    if(sessionID==null||sessionID.isEmpty()){
                        sendStringData(GeneralJsonBuilder.error("parameter sessionID is required"));
                        return;
                    }
                    String deviceID = request.getConnectingID();
                    if(deviceID==null||deviceID.isEmpty()){
                        sendStringData(GeneralJsonBuilder.error("parameter connectingID is required"));
                        return;
                    }
                    try {
                        services.detachDevice(sessionID,deviceID,this);
                    } catch (TCPServicesException e) {
                        e.printStackTrace();
                        sendStringData(GeneralJsonBuilder.error(e.toString()));
                    }
                    this.needCloseAfterParsing = true;
                }
                break;
            }
            case 'c':{
                // 转发命令
                services.userToDeviceForwarding(this,dataHead,data);
                break;
            }
        }
    }


    @Override
    protected void doBeforeThreadEnd() {
        if(needCloseAfterParsing){
            this.closeConnection();
        }
    }
}
