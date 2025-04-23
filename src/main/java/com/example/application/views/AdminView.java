package com.example.application.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "admin", layout = MainLayout.class)
@RolesAllowed("ADMIN") //OOnly admin allowed
public class AdminView extends VerticalLayout {
    public AdminView() {
        add(new H1("Admin Dashboard"));
        add(new H1("This page is only accessible to admins."));
    }
}
