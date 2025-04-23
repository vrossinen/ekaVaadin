package com.example.application;

import com.example.application.data.Event;
import com.example.application.data.Location;
import com.example.application.data.Organizer;
import com.example.application.data.Participant;
import com.example.application.data.User;
import com.example.application.data.UserRepository;
import com.example.application.services.EventService;
import com.example.application.services.LocationService;
import com.example.application.services.OrganizerService;
import com.example.application.services.ParticipantService;
import com.example.application.services.UserService;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@SpringBootApplication
@Theme(value = "my-theme", variant = Lumo.DARK)
//@PWA(name = "Event Management", shortName = "Events")
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class Application implements AppShellConfigurator {

    private final UserService userService;
    private final LocationService locationService;
    private final OrganizerService organizerService;
    private final ParticipantService participantService;
    private final EventService eventService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    // Constructor injection
    public Application(UserService userService, LocationService locationService, OrganizerService organizerService,
                       ParticipantService participantService, EventService eventService, PasswordEncoder passwordEncoder,
                       UserRepository userRepository) {
        this.userService = userService;
        this.locationService = locationService;
        this.organizerService = organizerService;
        this.participantService = participantService;
        this.eventService = eventService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    SqlDataSourceScriptDatabaseInitializer dataSourceScriptDatabaseInitializer(DataSource dataSource,
                                                                               SqlInitializationProperties properties, UserRepository repository) {
        // This bean ensures the database is only initialized when empty
        return new SqlDataSourceScriptDatabaseInitializer(dataSource, properties) {
            @Override
            public boolean initializeDatabase() {
                if (repository.count() == 0L) {
                    return super.initializeDatabase();
                }
                return false;
            }
        };
    }

    @PostConstruct
    public void seedDatabase() {
        // Only seed the database if it's empty
        if (userService.findAll().isEmpty()) {
            // Seed users with BCrypt-hashed passwords (passwords: "userpass" for user, "adminpass" for admin)
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("userpass"));
            user.setRole("ROLE_USER"); // Updated to include ROLE_ prefix
            userService.save(user);

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("adminpass"));
            admin.setRole("ROLE_ADMIN"); // Updated to include ROLE_ prefix
            userService.save(admin);

            // Rest of the seeding logic remains unchanged
            // Seed locations
            Location location1 = new Location();
            location1.setName("Conference Hall A");
            location1.setAddress("123 Main St, Helsinki");
            locationService.save(location1);

            Location location2 = new Location();
            location2.setName("Park Venue");
            location2.setAddress("456 Park Ave, Tampere");
            locationService.save(location2);

            // Seed organizers
            Organizer organizer1 = new Organizer();
            organizer1.setName("John Doe");
            organizer1.setContact("john.doe@example.com");
            organizerService.save(organizer1);

            Organizer organizer2 = new Organizer();
            organizer2.setName("Jane Smith");
            organizer2.setContact("jane.smith@example.com");
            organizerService.save(organizer2);

            // Seed participants
            Participant participant1 = new Participant();
            participant1.setName("Alice Johnson");
            participant1.setEmail("alice@example.com");
            participant1.setCategory("Attendee");
            participantService.save(participant1);

            Participant participant2 = new Participant();
            participant2.setName("Bob Brown");
            participant2.setEmail("bob@example.com");
            participant2.setCategory("Speaker");
            participantService.save(participant2);

            // Seed events
            Event event1 = new Event();
            event1.setName("Tech Conference 2025");
            event1.setDate(LocalDate.of(2025, 5, 1));
            event1.setLocation(location1);
            event1.setOrganizer(organizer1);
            Set<Participant> event1Participants = new HashSet<>();
            event1Participants.add(participant1);
            event1Participants.add(participant2);
            event1.setParticipants(event1Participants);
            eventService.save(event1);

            Event event2 = new Event();
            event2.setName("Summer Festival");
            event2.setDate(LocalDate.of(2025, 6, 15));
            event2.setLocation(location2);
            event2.setOrganizer(organizer2);
            Set<Participant> event2Participants = new HashSet<>();
            event2Participants.add(participant1);
            event2.setParticipants(event2Participants);
            eventService.save(event2);
        }
    }}
