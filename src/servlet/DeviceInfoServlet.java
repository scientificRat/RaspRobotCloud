package servlet;

import com.google.gson.Gson;
import datastruct.dataobj.DeviceInfo;
import services.Services;
import utility.GeneralJsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by huangzhengyue on 2016/10/31.
 */
@WebServlet("/servlet/deviceInfo")
public class DeviceInfoServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String userName = (String) req.getSession().getAttribute("userName");
        String sessionID = (String) req.getSession().getAttribute("sessionID");

        //check login
        if (userName == null || userName.isEmpty()) {
            out.print(GeneralJsonBuilder.error("not login"));
            return;
        }
        String type = req.getParameter("type");
        if (type == null || type.isEmpty()) {
            out.print(GeneralJsonBuilder.error("parameter type is required"));
            return;
        }
        Gson gson = new Gson();
        if (type.equals("queryOnlineDeviceList")) {
            Services services = Services.getInstance();
            out.print(gson.toJson(services.queryOnlineDevices(sessionID)));
        } else if (type.equals("queryAll")) {
            //查询在线设备
            Services services = Services.getInstance();
            ArrayList<DeviceInfo> deviceInfoArrayList;
            try {
                deviceInfoArrayList = services.queryAllDevices(sessionID);
                //response
                out.print(gson.toJson(deviceInfoArrayList));
            } catch (SQLException e) {
                e.printStackTrace();
                out.print(GeneralJsonBuilder.error(e.toString()));
            }
        }

    }

}
