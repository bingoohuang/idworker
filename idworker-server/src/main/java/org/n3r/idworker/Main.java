package org.n3r.idworker;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.n3r.idworker.IdWorkerServlet;

import java.net.URL;
import java.security.ProtectionDomain;

public class Main {
    public static void main(String[] args) throws Exception {
        Server server = new Server();

        SelectChannelConnector connector = new SelectChannelConnector();
        int port = Integer.parseInt(System.getProperty("port", "9223"));
        connector.setPort(port);
        server.addConnector(connector);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(new IdWorkerServlet()),"/*");
        server.setHandler(context);

        server.start();
        server.join();

    }
}
