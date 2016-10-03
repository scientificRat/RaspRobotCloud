package tcp;

import com.google.gson.Gson;
import datastruct.dataobj.Request;
import exceptions.TCPServicesException;
import services.Services;
import utility.GeneralJsonBuilder;

/**
 * Created by huangzhengyue on 27/09/2016.
 */
public class DeviceConnection extends TCPConnection {

    protected void parseMessage(byte[] dataHead,byte[] data){
        switch (dataHead[0]){
            case 'r':{
                String strData = new String(data);
                Gson gson = new Gson();
                Request request=gson.fromJson(strData, Request.class);
                String type =request.getRequestType();
                if(type == null || type.isEmpty()){
                    sendStringData(GeneralJsonBuilder.error("parameter requestType is required,[login/logout]"));
                    return;
                }
                if("login".equals(type)){
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
                        Services.getInstance().deviceLogin(loginName,password,this);
                    } catch (TCPServicesException e) {
                        e.printStackTrace();
                        sendStringData(GeneralJsonBuilder.error(e.toString()));
                    }

                }
                else if("logout".equals(type)){
                    try {
                        Services.getInstance().deviceLogout(this);
                    } catch (TCPServicesException e) {
                        e.printStackTrace();
                        sendStringData(GeneralJsonBuilder.error(e.toString()));
                    }
                }
                break;
            }
            case 'v':{
                Services.getInstance().deviceToUserForwarding(this,dataHead,data);
                break;
            }
            default:{
                sendStringData(GeneralJsonBuilder.error("undefined head type"));
                break;
            }
        }
    }
    @Override
    protected void doBeforeThreadEnd() {
        try {
            Services.getInstance().deviceLogout(this);
        } catch (TCPServicesException e) {
            e.printStackTrace();
            sendStringData(GeneralJsonBuilder.error(e.toString()));
        }
    }
}
