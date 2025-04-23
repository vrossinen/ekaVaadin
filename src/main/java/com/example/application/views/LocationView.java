package com.example.application.views;

import com.example.application.data.Location;
import com.example.application.services.LocationService;
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
@Route(value = "locations", layout = MainLayout.class)
public class LocationView extends VerticalLayout {
    private final LocationService locationService;
    private Grid<Location> grid = new Grid<>(Location.class);
    private TextField nameFilter = new TextField("Filter by Name");

    public LocationView(LocationService locationService) {
        this.locationService = locationService;
        addClassName("event-view");
        setSizeFull();

        configureGrid();
        configureFilters();

        Button addButton = new Button("Add Location", e -> openEditor(new Location()));
        addButton.addClassNames(LumoUtility.Margin.SMALL);

        add(new H2("Location Management"), addButton, nameFilter, grid);
        updateList();
    }

    private void configureGrid() {
        grid.addClassName("event-grid");
        grid.setColumns("name", "address");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.addComponentColumn(location -> {
            Button edit = new Button("Edit", e -> openEditor(location));
            Button delete = new Button("Delete", e -> {
                locationService.delete(location);
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
        grid.setItems(locationService.findAll().stream()
                .filter(location -> nameFilter.getValue().isEmpty() || location.getName().toLowerCase().contains(nameFilter.getValue().toLowerCase()))
                .toList());
    }

    private void openEditor(Location location) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(location.getId() == null ? "New Location" : "Edit Location");

        Binder<Location> binder = new Binder<>(Location.class);
        binder.setBean(location);

        TextField nameField = new TextField("Name");
        TextField addressField = new TextField("Address");

        binder.forField(nameField).asRequired().bind(Location::getName, Location::setName);
        binder.forField(addressField).bind(Location::getAddress, Location::setAddress);

        Button save = new Button("Save", e -> {
            if (binder.validate().isOk()) {
                locationService.save(location);
                updateList();
                dialog.close();
            }
        });
        Button cancel = new Button("Cancel", e -> dialog.close());

        dialog.add(new VerticalLayout(nameField, addressField));
        dialog.getFooter().add(cancel, save);
        dialog.open();
    }
}
