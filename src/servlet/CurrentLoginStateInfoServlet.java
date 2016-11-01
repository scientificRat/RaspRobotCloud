package servlet;

import com.google.gson.Gson;
import datastruct.dataobj.LoginInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by huangzhengyue on 8/13/16.
 */
@WebServlet("/servlet/loginState")
public class CurrentLoginStateInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String userName = (String) req.getSession().getAttribute("userName");

        String sessionID = (String) req.getSession().getAttribute("sessionID");

        LoginInfo loginInfo = new LoginInfo();
        Gson gson = new Gson();
        if (userName == null || userName.isEmpty() || sessionID == null || sessionID.isEmpty()) {
            req.getSession().removeAttribute("sessionID");
            req.getSession().removeAttribute("userName");
            loginInfo.setLogin(false);
        } else {
            loginInfo.setLogin(true);
            loginInfo.setUserName(userName);
            loginInfo.setSessionID(sessionID);
        }
        //return login state info
        out.println(gson.toJson(loginInfo));
    }

}
