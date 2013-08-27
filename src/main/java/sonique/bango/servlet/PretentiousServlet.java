package sonique.bango.servlet;

import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class PretentiousServlet extends HttpServlet {

    protected final ObjectMapper objectMapper;

    protected PretentiousServlet(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected abstract String getResponse(HttpServletRequest request);

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            String content = getResponse(request);
            writeToResponse(response, content, 200);
        } catch (GTFOException e) {
            writeToResponse(response, e.message(), e.status());
        }
    }

    private void writeToResponse(HttpServletResponse response, String content, int status) throws IOException {
        response.setStatus(status);
        response.setContentLength(content.length());
        response.setContentType("application/json");
        response.getWriter().write(content);
    }

    protected String writeJson(Object object)  {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}