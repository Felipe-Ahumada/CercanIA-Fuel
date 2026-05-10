package cl.fuelonline.security.infrastructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Devuelve 403 con cuerpo JSON cuando un user autenticado no tiene autorizacion suficiente.
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"status\":403,\"error\":\"Forbidden\",\"detail\":\""
                + safe(ex.getMessage()) + "\"}");
    }

    private String safe(String msg) {
        return msg == null ? "Access denied" : msg.replace("\"", "'");
    }
}
