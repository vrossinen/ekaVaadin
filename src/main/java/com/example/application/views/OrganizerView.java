package com.example.application.views;

import com.example.application.data.Organizer;
import com.example.application.services.OrganizerService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed({"USER", "ADMIN"}) //Only roles admin ja user allowed
@Route(value = "organizers", layout = MainLayout.class)
public class OrganizerView extends VerticalLayout {
    private final OrganizerService organizerService;
    private Grid<Organizer> grid = new Grid<>(Organizer.class);
    private TextField nameFilter = new TextField("Filter by Name");

    public OrganizerView(OrganizerService organizerService) {
        this.organizerService = organizerService;
        addClassName("event-view");
        setSizeFull();

        configureGrid();
        configureFilters();

        Button addButton = new Button("Add Organizer", e -> openEditor(new Organizer()));
        addButton.addClassNames(LumoUtility.Margin.SMALL);

        add(new H2("Organizer Management"), addButton, nameFilter, grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassName("event-grid");
        grid.setColumns("name", "contact");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.addComponentColumn(organizer -> {
            Button edit = new Button("Edit", e -> openEditor(organizer));
            Button delete = new Button("Delete", e -> {
                organizerService.delete(organizer);
                updateList();
            });
            return new HorizontalLayout(edit, delete);
        }).setHeader("Actions");
    }

    private void configureFilters() {
        nameFilter.addValueChangeListener(e -> updateList());
        nameFilter.setValueChangeMode(ValueChangeMode.LAZY);
    }

    private void updateList() {
        grid.setItems(organizerService.findAll().stream()
                .filter(organizer -> nameFilter.getValue().isEmpty() || organizer.getName().toLowerCase().contains(nameFilter.getValue().toLowerCase()))
                .toList());
    }

    private void openEditor(Organizer organizer) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(organizer.getId() == null ? "New Organizer" : "Edit Organizer");

        Binder<Organizer> binder = new Binder<>(Organizer.class);
        binder.setBean(organizer);

        TextField nameField = new TextField("Name");
        TextField contactField = new TextField("Contact");

        binder.forField(nameField).asRequired().bind(Organizer::getName, Organizer::setName);
        binder.forField(contactField).bind(Organizer::getContact, Organizer::setContact);

        Button save = new Button("Save", e -> {
            if (binder.validate().isOk()) {
                organizerService.save(organizer);
                updateList();
                dialog.close();
            }
        });
        Button cancel = new Button("Cancel", e -> dialog.close());

        dialog.add(new VerticalLayout(nameField, contactField));
        dialog.getFooter().add(cancel, save);
        dialog.open();
    }
}
