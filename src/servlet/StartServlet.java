package servlet;

import udp.UDPServer;
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
@WebServlet("/servlet/startUDPServer")
public class StartServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        PrintWriter out=resp.getWriter();
        String action=req.getParameter("action");
        if( action == null || action.isEmpty()){
            out.print(GeneralJsonBuilder.error("parameter action is required"));
            return;
        }
        if (action.equals("start")){
            UDPServer.getInstance().start();
            out.print(GeneralJsonBuilder.succuss(true));
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
