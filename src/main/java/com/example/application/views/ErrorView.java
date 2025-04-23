package com.example.application.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("error")
@AnonymousAllowed
public class ErrorView extends VerticalLayout {

    public ErrorView() {
        add(new H1("Access Denied"));
        add(new H1("You do not have permission to access this page."));
    }
}
