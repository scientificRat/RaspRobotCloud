package servlet;

import exceptions.TCPServicesException;
import services.Services;
import utility.GeneralJsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by huangzhengyue on 2016/11/1.
 * 本servlet用于向设备直接发送运动控制指令
 */
@WebServlet("/servlet/deviceControl")
public class DeviceControlServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String userName = (String) req.getSession().getAttribute("userName");
        String sessionID = (String) req.getSession().getAttribute("sessionID");
        String requestedDeviceID = req.getParameter("requestedDeviceID");

        if(requestedDeviceID==null||requestedDeviceID.isEmpty()){
            out.print(GeneralJsonBuilder.error("parameter requestedDeviceID is required"));
            return;
        }
        // x y偏移量
        float offsetX = 0;
        float offsetY = 0;
        try {
            offsetX = Float.parseFloat(req.getParameter("offsetX"));
            offsetY = Float.parseFloat(req.getParameter("offsetY"));
        }catch (NumberFormatException ne){
            ne.printStackTrace();
            out.print(GeneralJsonBuilder.error("parameter offsetX or offsetY is wrong or missed\n"+ne.toString()));
            return;
        }
        //check login
        if(userName==null || userName.isEmpty()){
            out.print(GeneralJsonBuilder.error("not login"));
            return;
        }

        Services services = Services.getInstance();
        try{
            services.sendDeviceMovementCommand(requestedDeviceID,offsetX,offsetY);
            out.print(GeneralJsonBuilder.succuss(true));
        } catch (TCPServicesException e){
            e.printStackTrace();
            out.print(GeneralJsonBuilder.error(e.toString()));
            return;
        }

    }
}
