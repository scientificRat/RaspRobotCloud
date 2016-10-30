package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by huangzhengyue on 2016/10/30.
 */

/**
 * 建立和小车连接前需要先向这个servlet 请求初始化连接 ( Services 执行userLogin )
 */
@WebServlet("/servlet/deviceConnectionSetup")
public class DeviceConnectionSessionManagementServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
