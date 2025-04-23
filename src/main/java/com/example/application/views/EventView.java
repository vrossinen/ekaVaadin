package com.example.application.views;

import com.example.application.data.Event;
import com.example.application.data.Location;
import com.example.application.data.Organizer;
import com.example.application.data.Participant;
import com.example.application.services.EventService;
import com.example.application.services.LocationService;
import com.example.application.services.OrganizerService;
import com.example.application.services.ParticipantService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BindingException;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Route(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class EventView extends VerticalLayout {
    private static final Logger logger = LoggerFactory.getLogger(EventView.class);

    private final EventService eventService;
    private final LocationService locationService;
    private final OrganizerService organizerService;
    private final ParticipantService participantService;
    private Grid<Event> grid = new Grid<>(Event.class);
    private TextField nameFilter = new TextField("Filter by Name");
    private TextField dateFilter = new TextField("Filter by Date (YYYY-MM-DD)");
    private TextField locationFilter = new TextField("Filter by Location");
    private TextField organizerFilter = new TextField("Filter by Organizer");
    private TextField participantFilter = new TextField("Filter by Participant Category");

    public EventView(EventService eventService, LocationService locationService,
                     OrganizerService organizerService, ParticipantService participantService) {
        this.eventService = eventService;
        this.locationService = locationService;
        this.organizerService = organizerService;
        this.participantService = participantService;
        addClassName("event-view");
        setSizeFull();

        configureGrid();
        configureFilters();

        Button addButton = new Button("Add Event", e -> openEditor(new Event()));
        addButton.addClassNames(LumoUtility.Margin.SMALL);
        addButton.setEnabled(isUserAuthenticatedWithRole());

        add(new H2(getTranslation("event.view.title")), addButton, nameFilter, dateFilter,
                locationFilter, organizerFilter, participantFilter, grid);
        updateList();
    }

    private boolean isUserAuthenticatedWithRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER") ||
                        grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

    private void configureGrid() {
        grid.addClassName("event-grid");
        grid.setColumns("name", "date");
        grid.addColumn(event -> event.getLocation() != null ? event.getLocation().getName() : "").setHeader(getTranslation("event.grid.location"));
        grid.addColumn(event -> event.getOrganizer() != null ? event.getOrganizer().getName() : "").setHeader(getTranslation("event.grid.organizer"));
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.addComponentColumn(event -> {
            Button edit = new Button("Edit", e -> openEditor(event));
            Button delete = new Button("Delete", e -> {
                eventService.delete(event);
                updateList();
            });
            edit.setEnabled(isUserAuthenticatedWithRole());
            delete.setEnabled(isUserAuthenticatedWithRole());
            return new HorizontalLayout(edit, delete);
        }).setHeader("Actions");
    }

    private void configureFilters() {
        nameFilter.addValueChangeListener(e -> updateList());
        nameFilter.setValueChangeMode(ValueChangeMode.LAZY);
        dateFilter.addValueChangeListener(e -> updateList());
        dateFilter.setValueChangeMode(ValueChangeMode.LAZY);
        locationFilter.addValueChangeListener(e -> updateList());
        locationFilter.setValueChangeMode(ValueChangeMode.LAZY);
        organizerFilter.addValueChangeListener(e -> updateList());
        organizerFilter.setValueChangeMode(ValueChangeMode.LAZY);
        participantFilter.addValueChangeListener(e -> updateList());
        participantFilter.setValueChangeMode(ValueChangeMode.LAZY);
    }

    private void updateList() {
        grid.setItems(eventService.findAll().stream()
                .filter(event -> nameFilter.getValue().isEmpty() || event.getName().toLowerCase().contains(nameFilter.getValue().toLowerCase()))
                .filter(event -> dateFilter.getValue().isEmpty() || event.getDate().toString().contains(dateFilter.getValue()))
                .filter(event -> locationFilter.getValue().isEmpty() || (event.getLocation() != null && event.getLocation().getName().toLowerCase().contains(locationFilter.getValue().toLowerCase())))
                .filter(event -> organizerFilter.getValue().isEmpty() || (event.getOrganizer() != null && event.getOrganizer().getName().toLowerCase().contains(organizerFilter.getValue().toLowerCase())))
                .filter(event -> participantFilter.getValue().isEmpty() || event.getParticipants().stream().anyMatch(p -> p.getCategory() != null && p.getCategory().toLowerCase().contains(participantFilter.getValue().toLowerCase())))
                .collect(Collectors.toList()));
    }

    private void openEditor(Event event) {
        try {
            logger.info("Opening editor for event: {}", event.getId() != null ? event.getId() : "new event");

            // Ensure participants is initialized to avoid null pointer exceptions
            if (event.getParticipants() == null) {
                logger.info("Participants collection was null, initializing as empty HashSet");
                event.setParticipants(new HashSet<>());
            } else {
                logger.info("Participants collection size: {}", event.getParticipants().size());
            }

            Dialog dialog = new Dialog();
            dialog.setHeaderTitle(event.getId() == null ? "New Event" : "Edit Event");

            Binder<Event> binder = new Binder<>(Event.class);
            binder.setBean(event);

            TextField nameField = new TextField("Name");
            DatePicker dateField = new DatePicker("Date");
            ComboBox<Location> locationField = new ComboBox<>("Location", locationService.findAll());
            locationField.setItemLabelGenerator(Location::getName);
            ComboBox<Organizer> organizerField = new ComboBox<>("Organizer", organizerService.findAll());
            organizerField.setItemLabelGenerator(Organizer::getName);
            MultiSelectComboBox<Participant> participantsField = new MultiSelectComboBox<>("Participants");
            participantsField.setItems(participantService.findAll());
            participantsField.setItemLabelGenerator(Participant::getName);

            binder.forField(nameField).asRequired("Name is required").bind(Event::getName, Event::setName);
            binder.forField(dateField).asRequired("Date is required").bind(Event::getDate, Event::setDate);
            binder.forField(locationField).bind(Event::getLocation, Event::setLocation);
            binder.forField(organizerField).bind(Event::getOrganizer, Event::setOrganizer);

            // Custom binding for participants to handle potential issues
            try {
                binder.forField(participantsField)
                        .bind(
                                e -> {
                                    logger.info("Binding participants getter: {}", e.getParticipants());
                                    return e.getParticipants() != null ? e.getParticipants() : new HashSet<>();
                                },
                                (e, value) -> {
                                    logger.info("Binding participants setter: {}", value);
                                    e.setParticipants(value != null ? value : new HashSet<>());
                                }
                        );
            } catch (Exception ex) {
                logger.error("Error binding participants field", ex);
                throw new BindingException("Failed to bind participants field", ex);
            }

            Button save = new Button("Save", e -> {
                try {
                    binder.writeBean(event);
                    if (binder.validate().isOk()) {
                        eventService.save(event);
                        updateList();
                        dialog.close();
                        Notification.show("Event saved successfully");
                    } else {
                        Notification.show("Please fill in all required fields");
                    }
                } catch (ValidationException ex) {
                    logger.error("Validation error while saving event", ex);
                    Notification.show("Error saving event: " + ex.getMessage());
                } catch (IllegalStateException ex) {
                    logger.error("Validation error: Location already in use", ex);
                    Notification.show(ex.getMessage());
                } catch (Exception ex) {
                    logger.error("Unexpected error while saving event", ex);
                    Notification.show("Unexpected error: " + ex.getMessage());
                }
            });
            Button cancel = new Button("Cancel", e -> dialog.close());

            dialog.add(new VerticalLayout(nameField, dateField, locationField, organizerField, participantsField));
            dialog.getFooter().add(cancel, save);
            dialog.open();
        } catch (Exception ex) {
            logger.error("Error opening editor for event", ex);
            Notification.show("Error opening editor: " + ex.getMessage());
        }
    }
}