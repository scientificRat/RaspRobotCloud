package servlet;

import utility.ImageVerifyCodeGenerator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by huangzhengyue on 8/12/16.
 */
@WebServlet("/servlet/authImage")
//图片验证码
public class AuthImageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Pragma", "No-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", 0);
        resp.setContentType("image/jpeg");
        //generate code
        String verifyCode = ImageVerifyCodeGenerator.generateVerifyCode(4);
        //set session
        req.getSession().setAttribute("loginVerify",verifyCode.toLowerCase());
        //output
        int width =200;
        int height =80;
        ImageVerifyCodeGenerator.outputImage(width,height,resp.getOutputStream(),verifyCode);
    }
}
