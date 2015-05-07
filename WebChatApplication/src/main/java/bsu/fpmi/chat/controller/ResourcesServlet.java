package bsu.fpmi.chat.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class ResourcesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        InputStream in = getServletContext().getResourceAsStream("/WEB-INF/" + req.getRequestURI());

        byte[] buf = new byte[1024];
        int read = -1;
        while ((read = in.read(buf)) != -1) {
            resp.getOutputStream().write(buf, 0, read);
        }
    }
}
