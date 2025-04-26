package com.example.application.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        VaadinSession session = VaadinSession.getCurrent();

        if (session != null && UI.getCurrent() != null) {
            session.access(() -> {
                UI.getCurrent().navigate("error");
            });
        } else {
            // Fallback: perinteinen redirect
            response.sendRedirect("/error");
        }
    }
}
