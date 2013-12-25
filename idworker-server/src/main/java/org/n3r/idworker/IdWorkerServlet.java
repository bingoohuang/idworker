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
        String ipu = req.getParameter("ipu");
        if (uri.equals("/list")) {
            if (ipu != null && ipu.length() > 0) {
                answer.append(WorkerIdServerLock.list(ipu));
            } else {
                answer.append(WorkerIdServerLock.list());
            }
        } else if (uri.equals("/see")) {
            answer.append(WorkerIdServerLock.current());
        } else if (uri.equals("/inc")) {
            if (ipu != null && ipu.length() > 0) {
                String increment = WorkerIdServerLock.incr(ipu);
                answer.append(increment);
            } else {
                answer.append("ipu is required");
            }
        } else if (uri.equals("/sync")) {
            String workerIds = req.getParameter("ids");
            if (ipu != null && ipu.length() > 0 && workerIds != null && workerIds.length() > 0)
                answer.append(WorkerIdServerLock.sync(ipu, workerIds));
        } else {
            answer.append("bad request");
        }

        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.print(answer.toString());
        out.close();
    }
}
