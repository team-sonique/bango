package sonique.bango;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import sonique.bango.servlet.AgentApiServlet;
import sonique.bango.servlet.QueueApiServlet;
import sonique.bango.store.AgentStore;
import sonique.bango.store.ServiceProblemStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.*;
import static org.codehaus.jackson.annotate.JsonMethod.*;
import static org.codehaus.jackson.map.SerializationConfig.Feature.*;
import static org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS;

public class Bango {

    private final Server server;
    private final AgentStore agentStore = new AgentStore();
    private final ServiceProblemStore serviceProblemStore = new ServiceProblemStore();
    private final ObjectMapper objectMapper;

    public static void main(String[] args) throws Exception {
        new Bango().start();
    }

    private Bango() {
        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(FIELD, ANY);
//        objectMapper.configure(WRAP_ROOT_VALUE, true);

        server = new Server(8080);
        ServletContextHandler contextHandler = new ServletContextHandler(SESSIONS);
        contextHandler.setContextPath("/superman");

        agentApiHandler(contextHandler);
        queueApiHandler(contextHandler);
        loginHandler(contextHandler);
        logoutHandler(contextHandler);
        Handler extHandler = extFilesHandler();

        ContextHandlerCollection handler = new ContextHandlerCollection();
        handler.setHandlers(new Handler[]{extHandler, contextHandler});
        server.setHandler(handler);
    }

    private Handler extFilesHandler() {
        ResourceHandler extResourceHandler = new ResourceHandler();
        extResourceHandler.setResourceBase("src/main/javascript/");
        extResourceHandler.setWelcomeFiles(new String[]{"app.html", "index.html", "superman.html"});
        extResourceHandler.setDirectoriesListed(true);

        ContextHandler contextHandler = new ContextHandler("/superman");
        contextHandler.setHandler(extResourceHandler);
        return contextHandler;
    }

    private void agentApiHandler(ServletContextHandler contextHandler) {
        ServletHolder servletHolder = new ServletHolder(new AgentApiServlet(agentStore, objectMapper));
        contextHandler.addServlet(servletHolder, "/api/agent/*");
    }

    private void queueApiHandler(ServletContextHandler contextHandler) {
        ServletHolder servletHolder = new ServletHolder(new QueueApiServlet(serviceProblemStore, objectMapper));
        contextHandler.addServlet(servletHolder, "/api/queue/*");
    }

    private void loginHandler(ServletContextHandler contextHandler) {
        ServletHolder servletHolder = new ServletHolder(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                String sessionId = request.getSession().getId();
                agentStore.login(sessionId, request.getParameter("username").toUpperCase());
            }
        });
        contextHandler.addServlet(servletHolder, "/j_spring_security_check");
    }

    private void logoutHandler(ServletContextHandler contextHandler) {
        ServletHolder servletHolder = new ServletHolder(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                String sessionId = request.getSession().getId();
                agentStore.logout(sessionId);
            }
        });
        contextHandler.addServlet(servletHolder, "/j_spring_security_logout");
    }

    public void start() throws Exception {
        server.start();
        server.join();
    }
}