package servlet;

import com.google.gson.Gson;
import server.TCPServer;
import tcp.DeviceConnection;
import tcp.UserNonBrowserClientConnection;
import tcp.UserVideoHttpConnection;
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
public class LaunchServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String action = req.getParameter("action");
        Gson gson = new Gson();
        if (action == null || action.isEmpty()) {
            out.print(GeneralJsonBuilder.error("parameter action is required"));
            return;
        }
        if (action.equals("start")) {
            int deviceServerPort;
            int userServerPort;
            try {
                deviceServerPort = Integer.parseInt(req.getParameter("devicePort"));
                userServerPort = Integer.parseInt(req.getParameter("userPort"));
            } catch (NumberFormatException e) {
                out.print(GeneralJsonBuilder.error("devicePort or userPort wrong!"));
                return;
            }

            /**
             * userServer是用于建立和用户持久的tcp连接（非http）, 本系统设计的时候本没有http的部分，
             *  后来以因为客户端开发遇到困难才加入了http的部分，现在系统显得有一点混乱
             */
            // find the servers in context
            TCPServer deviceServer = (TCPServer) this.getServletContext().getAttribute("DeviceServer");
            TCPServer userServer = (TCPServer) this.getServletContext().getAttribute("UserServer");
            TCPServer videoServer = (TCPServer) this.getServletContext().getAttribute("VideoHttpServer");

            if (deviceServer == null || userServer == null || videoServer == null) {
                // create new Server
                deviceServer = new TCPServer(deviceServerPort, DeviceConnection.class);
                userServer = new TCPServer(userServerPort, UserNonBrowserClientConnection.class);
                videoServer = new TCPServer(8999, UserVideoHttpConnection.class);
                deviceServer.start();
                userServer.start();
                videoServer.start();
                this.getServletContext().setAttribute("DeviceServer", deviceServer);
                this.getServletContext().setAttribute("UserServer", userServer);
                this.getServletContext().setAttribute("VideoHttpServer", videoServer);
                out.print(GeneralJsonBuilder.success(true));
            } else if (deviceServer != null && userServer != null && videoServer != null) {
                out.print(GeneralJsonBuilder.error("已经开启，不能再次开启"));
            } else {
                out.print(GeneralJsonBuilder.error("fatal error detected!! 请重启tomcat服务器"));
            }
        } else {
            out.print(GeneralJsonBuilder.error("parameter action is wrong"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
