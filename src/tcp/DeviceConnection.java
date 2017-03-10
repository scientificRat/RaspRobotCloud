package tcp;

import com.google.gson.Gson;
import datastruct.dataobj.Request;
import exceptions.TCPServicesException;
import services.Services;
import utility.GeneralJsonBuilder;

import java.net.SocketException;

/**
 * Created by huangzhengyue on 27/09/2016.
 */
public class DeviceConnection extends SelfDefinedProtocolConnection {

    protected void parseMessage(byte[] dataHead,byte[] data){
        switch (dataHead[0]){
            case 'r':{
                this.needCloseAfterParsing = true;
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
                        sendStringData(GeneralJsonBuilder.success(true));
                        this.needCloseAfterParsing = false;
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
                try {
                    Services.getInstance().deviceToUserForwarding(this,dataHead,data);
                }catch (SocketException e){
                    e.printStackTrace();
                }
                break;
            }
            default:{
                sendStringData(GeneralJsonBuilder.error("undefined head type"));
                this.needCloseAfterParsing = true;
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

    @Override
    public void sendForwardingData(byte[] data) {
        //直接将需要转发的数据原封不动发给小车设备
        sendRawData(data);
    }
}
