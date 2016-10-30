package servlet;

import com.google.gson.Gson;
import dao.UsersRepository;
import utility.DBHelper;
import utility.GeneralJsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by huangzhengyue on 2016/10/30.
 */
@WebServlet("/servlet/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String loginName = req.getParameter("loginName");
        String password = req.getParameter("password");
        String quit = req.getParameter("logout");
        String verifyingCode = req.getParameter("verifyingCode");

        //退出登录 即清除session
        if (quit != null && quit.equals("logout")) {
            req.getSession().removeAttribute("userName");
            out.print(GeneralJsonBuilder.succuss(true));
            return;
        }

        // 参数空串检查
        if (loginName == null || loginName.isEmpty()) {
            out.println(GeneralJsonBuilder.error("用户名空"));
            return;
        }
        if (password == null || password.isEmpty()) {
            out.println(GeneralJsonBuilder.error("密码空"));
            return;
        }
        if (verifyingCode == null || verifyingCode.isEmpty()) {
            out.println(GeneralJsonBuilder.error("验证码空"));
            return;
        }
        // 验证码检查
        String codeInServer = (String) req.getSession().getAttribute("loginVerify");
        if (codeInServer == null || codeInServer.isEmpty()) {
            out.println(GeneralJsonBuilder.error("session time out"));
            return;
        }
        if (!codeInServer.equals(verifyingCode.toLowerCase())) {
            out.println(GeneralJsonBuilder.error("验证码不对，请重新输入"));
            return;
        }
        //清除服务器中的验证码，避免被攻击者利用
        req.getSession().removeAttribute("loginVerify");
        Connection dbConnection = DBHelper.getDBConnection();
        UsersRepository usersRepository = new UsersRepository(dbConnection);
        //清除当前session
        req.getSession().removeAttribute("userName");
        try {
            // 查询数据库
            if (usersRepository.queryExist(loginName, password)) {
                out.println(GeneralJsonBuilder.succuss(true));
            } else {
                out.println(GeneralJsonBuilder.error("用户名或密码错误"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            out.println(GeneralJsonBuilder.error(e.toString()));
        } finally {
            //关闭链接
            try {
                dbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
