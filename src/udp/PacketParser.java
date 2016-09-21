package udp;

import com.google.gson.Gson;
import datastruct.dto.DeviceInfo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by huangzhengyue on 9/16/16.
 */
public class PacketParser extends Thread {
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private Services services;

    public PacketParser(DatagramSocket datagramSocket, DatagramPacket datagramPacket) {
        this.datagramSocket = datagramSocket;
        this.datagramPacket = datagramPacket;
        this.services = Services.getInstance();
    }

    //thread run
    @Override
    public void run() {
        byte[] message = datagramPacket.getData();
        byte type = message[0];
        //'v' video frame
        //'c' command
        //'i' login
        //'o' logout
        //'~' connect
        //'d' detach
        //'q' query online device
        //'m' response json
        try {
            switch (type) {
                //video frame
                case 'v': {
                    int deviceSessionID = message[1] + message[2] + message[3] + message[4];
                    services.deviceToUserForwarding(deviceSessionID, datagramSocket, datagramPacket);
                    break;
                }
                //command
                case 'c': {
                    int userSessionID = message[1] + message[2] + message[3] + message[4];
                    services.userToDeviceForwarding(userSessionID, datagramSocket, datagramPacket);
                    break;
                }
                case 'i':{
                    byte from = message[1];
                    int ID = message[2] + message[3] + message[4] + message[5];
                    String password = new String(message,6,4);
                    String info = new String(message,10,4);
                    //0  client
                    //1  raspberry pi
                    if(from == 0){
                        services.userLogin(ID,password,datagramPacket.getAddress(),datagramPacket.getPort(),info);
                    }
                    else if(from ==1){
                        services.deviceLogin(ID,password,datagramPacket.getAddress(),datagramPacket.getPort(),info);
                    }
                    break;
                }
                case 'o':{
                    byte from = message[1];
                    int sessionID = message[2] + message[3] + message[4] + message[5];
                    //0  client
                    //1  raspberry pi
                    if(from == 0){
                        services.userLogout(sessionID);
                    }
                    else if(from ==1){
                        services.deviceLogout(sessionID);
                    }
                    break;
                }
                case '~':{
                    int userSessionID = message[1] + message[2] + message[3] + message[4];
                    int deviceID =  message[5] + message[6] + message[7] + message[8];
                    StringBuilder stringBuilder= new StringBuilder();
                    stringBuilder.append("{DeviceSessionID:");
                    stringBuilder.append(services.connectDevice(userSessionID,deviceID));
                    stringBuilder.append('}');
                    String sendJson = stringBuilder.toString();
                    byte head='m';
                    sendJsonDataBack(head,sendJson);
                    break;
                }
                case 'd':{
                    int userSessionID = message[1] + message[2] + message[3] + message[4];
                    int deviceID =  message[5] + message[6] + message[7] + message[8];
                    services.detachDevice(userSessionID,deviceID);
                    break;
                }
                case 'q': {
                    int userSessionID = message[1] + message[2] + message[3] + message[4];
                    ArrayList<DeviceInfo> deviceInfoArrayList = services.queryOnlineDevices(userSessionID);
                    Gson gson = new Gson();
                    String sendJson=gson.toJson(deviceInfoArrayList);
                    byte head='m';
                    sendJsonDataBack(head,sendJson);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendJsonDataBack(byte head, String sendJson) throws IOException{
        ByteBuffer byteBuffer = ByteBuffer.allocate(sendJson.length()+1);
        byteBuffer.put(head);
        byteBuffer.put(sendJson.getBytes());
        byte[] sendBuffer = byteBuffer.array();
        DatagramPacket sendDatagramPacket = new DatagramPacket(sendBuffer,sendBuffer.length,this.datagramPacket.getAddress(),this.datagramPacket.getPort());
        this.datagramSocket.send(sendDatagramPacket);
    }
}