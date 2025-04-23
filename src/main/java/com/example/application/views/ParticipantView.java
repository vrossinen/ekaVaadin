package com.example.application.views;

import com.example.application.data.Participant;
import com.example.application.services.ParticipantService;
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
@Route(value = "participants", layout = MainLayout.class)
public class ParticipantView extends VerticalLayout {
    private final ParticipantService participantService;
    private Grid<Participant> grid = new Grid<>(Participant.class);
    private TextField nameFilter = new TextField("Filter by Name");
    private TextField categoryFilter = new TextField("Filter by Category");

    public ParticipantView(ParticipantService participantService) {
        this.participantService = participantService;
        addClassName("event-view");
        setSizeFull();

        configureGrid();
        configureFilters();

        Button addButton = new Button("Add Participant", e -> openEditor(new Participant()));
        addButton.addClassNames(LumoUtility.Margin.SMALL);

        add(new H2("Participant Management"), addButton, nameFilter, categoryFilter, grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassName("event-grid");
        grid.setColumns("name", "email", "category");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.addComponentColumn(participant -> {
            Button edit = new Button("Edit", e -> openEditor(participant));
            Button delete = new Button("Delete", e -> {
                participantService.delete(participant);
                updateList();
            });
            return new HorizontalLayout(edit, delete);
        }).setHeader("Actions");
    }

    private void configureFilters() {
        nameFilter.addValueChangeListener(e -> updateList());
        nameFilter.setValueChangeMode(ValueChangeMode.LAZY);
        categoryFilter.addValueChangeListener(e -> updateList());
        categoryFilter.setValueChangeMode(ValueChangeMode.LAZY);
    }

    private void updateList() {
        grid.setItems(participantService.findAll().stream()
                .filter(participant -> nameFilter.getValue().isEmpty() || participant.getName().toLowerCase().contains(nameFilter.getValue().toLowerCase()))
                .filter(participant -> categoryFilter.getValue().isEmpty() || (participant.getCategory() != null && participant.getCategory().toLowerCase().contains(categoryFilter.getValue().toLowerCase())))
                .toList());
    }

    private void openEditor(Participant participant) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(participant.getId() == null ? "New Participant" : "Edit Participant");

        Binder<Participant> binder = new Binder<>(Participant.class);
        binder.setBean(participant);

        TextField nameField = new TextField("Name");
        TextField emailField = new TextField("Email");
        TextField categoryField = new TextField("Category");

        binder.forField(nameField).asRequired().bind(Participant::getName, Participant::setName);
        binder.forField(emailField).bind(Participant::getEmail, Participant::setEmail);
        binder.forField(categoryField).bind(Participant::getCategory, Participant::setCategory);

        Button save = new Button("Save", e -> {
            if (binder.validate().isOk()) {
                participantService.save(participant);
                updateList();
                dialog.close();
            }
        });
        Button cancel = new Button("Cancel", e -> dialog.close());

        dialog.add(new VerticalLayout(nameField, emailField, categoryField));
        dialog.getFooter().add(cancel, save);
        dialog.open();
    }
}
