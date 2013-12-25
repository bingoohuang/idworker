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
        String ip = req.getParameter("ip");
        if (uri.equals("/list")) {
            if (ip != null && ip.length() > 0) {
                answer.append(WorkerIdServerLock.list(ip));
            } else {
                answer.append(WorkerIdServerLock.list());
            }
        } else if (uri.equals("/see")) {
            answer.append(WorkerIdServerLock.current());
        } else if (uri.equals("/inc")) {
            if (ip != null && ip.length() > 0) {
                String increment = WorkerIdServerLock.increment(ip);
                answer.append(increment);
            } else {
                answer.append("ip is required");
            }
        } else if (uri.equals("/sync")) {
            String workerIds = req.getParameter("ids");
            if (ip != null && ip.length() > 0 && workerIds != null && workerIds.length() > 0)
                answer.append(WorkerIdServerLock.sync(ip, workerIds));
        } else {
            answer.append("bad request");
        }

        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.print(answer.toString());
        out.close();
    }
}
