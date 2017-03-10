package servlet;

import dao.RaspDevicesRepository;
import utility.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by huangzhengyue on 2016/11/5.
 */
@WebServlet("/servlet/shortMessageVerifying")
public class ShortMessageVerifyingServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String type = req.getParameter("type");
        if (type == null || type.isEmpty()) {
            out.print(GeneralJsonBuilder.error("parameter type is required"));
            return;
        }
        if ("sendMessage".equals(type)) {
            String phoneNumber = req.getParameter("phoneNumber");
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                out.print(GeneralJsonBuilder.error("parameter phoneNumber is required"));
                return;
            }
            String code = RandomCodeGenerator.getRandomString(5);
            req.getSession().setAttribute("shortMessageVerifyingCodeInServer", code);
            req.getSession().setAttribute("verified", false);
            ShortMessageSender.sendVerifyingCode(phoneNumber, code);
            out.print(GeneralJsonBuilder.success(true));


        } else if ("verifying".equals(type)) {
            String shortMessageVerifyingCode = req.getParameter("verifyingCode");

            if (shortMessageVerifyingCode == null || shortMessageVerifyingCode.isEmpty()) {
                out.print(GeneralJsonBuilder.error("parameter verifyingCode is required"));
                return;
            }

            String codeInServer = (String) req.getSession().getAttribute("shortMessageVerifyingCodeInServer");
            if (shortMessageVerifyingCode.equals(codeInServer)) {
                req.getSession().setAttribute("verified", true);
                out.print(GeneralJsonBuilder.success(true));
            }
            else {
                out.print(GeneralJsonBuilder.error("wrong code"));
            }

        } else if ("newDevice".equals(type)) {
            boolean verified = (boolean) req.getSession().getAttribute("verified");
            if (!verified) {
                out.print(GeneralJsonBuilder.error("not verified"));
                return;
            }
            String deviceID = req.getParameter("newDeviceID");
            String password = req.getParameter("password");

            Connection dbConnection = DBHelper.getDBConnection();
            RaspDevicesRepository raspDevicesRepository = new RaspDevicesRepository(dbConnection);
            try {
                raspDevicesRepository.add(deviceID,password);
            } catch (SQLException e) {
                e.printStackTrace();
                out.print(GeneralJsonBuilder.error(e.toString()));
            }
            req.getSession().setAttribute("verified", false);
            req.getSession().removeAttribute("shortMessageVerifyingCodeInServer");
            out.print(GeneralJsonBuilder.success(true));
        }
        else {
            out.print(GeneralJsonBuilder.error("type not defined"));
            return;
        }
    }
}
