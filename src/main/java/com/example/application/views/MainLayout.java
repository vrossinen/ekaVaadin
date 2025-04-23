package com.example.application.views;

import com.example.application.security.AuthenticatedUser;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Layout
@AnonymousAllowed
@PageTitle("Event Management")
public class MainLayout extends AppLayout {
    private final AuthenticatedUser authenticatedUser;

    public MainLayout(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;

        DrawerToggle toggle = new DrawerToggle();
        H1 logo = new H1("Event Management");
        logo.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE, "custom-component");

        Header header = new Header(toggle, logo);
        header.addClassNames(LumoUtility.Background.PRIMARY, LumoUtility.Padding.MEDIUM);

        SideNav nav = new SideNav();
        nav.addItem(
                new SideNavItem("Events", EventView.class),
                new SideNavItem("Locations", LocationView.class),
                new SideNavItem("Organizers", OrganizerView.class),
                new SideNavItem("Participants", ParticipantView.class),
                new SideNavItem("Admin", AdminView.class)
        );

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal());

        Scroller scroller = new Scroller(nav);

        Footer footer = new Footer();
        H1 copyright = new H1("© 2025 Event Management");
        copyright.addClassNames(LumoUtility.FontSize.SMALL);

        HorizontalLayout footerLayout = new HorizontalLayout(copyright);
        footerLayout.setWidthFull();
        footerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        footerLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        if (isAuthenticated) {
            Button logoutButton = new Button("Logout", e -> {
                authenticatedUser.logout(); // Perform logout
                getUI().ifPresent(ui -> ui.getPage().setLocation("/login?logout")); // Use setLocation instead of navigate
            });
            logoutButton.addClassName("logout-button");
            footerLayout.add(logoutButton);
        }

        footer.add(footerLayout);
        footer.addClassNames(LumoUtility.TextAlignment.CENTER, LumoUtility.Padding.MEDIUM);

        addToDrawer(scroller, footer);
        addToNavbar(header);
    }
}