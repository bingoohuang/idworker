package org.n3r.idworker;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class IdWorkerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        StringBuilder answer = new StringBuilder();
        if (uri.equals("/list")) {
            String ip = req.getParameter("ip");
            if (ip != null && ip.length() > 0) {
                answer.append(WorkerIdServerLock.list(ip));
            } else {
                answer.append(WorkerIdServerLock.list());
            }
        } else if (uri.equals("/see")) {
            long current = WorkerIdServerLock.current();
            answer.append(String.format("%04d", current));
        } else if (uri.equals("/inc")) {
            String ip = req.getParameter("ip");
            if (ip != null && ip.length() > 0) {
                String increment = WorkerIdServerLock.increment(ip);
                answer.append(increment);
            } else {
                answer.append("ip is required");
            }
        } else {
            answer.append("bad request");
        }

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.print(answer.toString());
        out.close();
    }


}
