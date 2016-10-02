package servlet;

import com.google.gson.Gson;
import server.DeviceServer;
import server.UserServer;
import utility.GeneralJsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by huangzhengyue on 9/16/16.
 */
@WebServlet("/servlet/startServer")
public class StartServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        PrintWriter out=resp.getWriter();
        String action=req.getParameter("action");
        Gson gson = new Gson();
        if( action == null || action.isEmpty()){
            out.print(GeneralJsonBuilder.error("parameter action is required"));
            return;
        }
        if (action.equals("start")){
            int deviceServerPort;
            int userServerPort;
            try{
                deviceServerPort = Integer.parseInt(req.getParameter("devicePort"));
                userServerPort = Integer.parseInt(req.getParameter("userPort"));
            }catch (NumberFormatException e){
                out.print(GeneralJsonBuilder.error("devicePort or userPort wrong!"));
                return;
            }
            DeviceServer deviceServer = (DeviceServer) this.getServletContext().getAttribute("DeviceServer");
            UserServer userServer = (UserServer) this.getServletContext().getAttribute("UserServer");
            if(deviceServer==null || userServer ==null){
                deviceServer = new DeviceServer(deviceServerPort);
                userServer = new UserServer(userServerPort);
                deviceServer.start();
                userServer.start();
                this.getServletContext().setAttribute("DeviceServer",deviceServer);
                this.getServletContext().setAttribute("UserServer",userServer);
                out.print(GeneralJsonBuilder.succuss(true));
            }
            else if(deviceServer!=null&& userServer!=null){
                out.print(GeneralJsonBuilder.error("已经开启，不能再次开启"));
            }
            else {
                out.print(GeneralJsonBuilder.error("fatal error detected!! 请重启服务器"));
            }
        }
        else {
            out.print(GeneralJsonBuilder.error("parameter action is wrong"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }
}
