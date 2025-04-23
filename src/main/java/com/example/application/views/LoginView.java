package com.example.application.views;

import com.example.application.security.AuthenticatedUser;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver, RouterLayout {

    private final LoginOverlay loginOverlay = new LoginOverlay();
    private final Span message = new Span();
    private final AuthenticatedUser authenticatedUser;

    public LoginView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;

        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        loginOverlay.setAction("login");
        loginOverlay.setOpened(true);
        loginOverlay.setTitle("Event Management");
        loginOverlay.setDescription("Login to access the application");

        message.addClassName("logout-message");

        add(new H1("Event Management Login"), message, loginOverlay);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            loginOverlay.setOpened(false);
            event.forwardTo("");
        } else {
            if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
                loginOverlay.setError(true);
            }
            if (event.getLocation().getQueryParameters().getParameters().containsKey("logout")) {
                message.setText("You have been logged out.");
            }
        }
    }
}
