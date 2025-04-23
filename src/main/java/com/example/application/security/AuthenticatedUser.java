package com.example.application.security;

import com.example.application.data.User;
import com.example.application.data.UserRepository;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

@Component
public class AuthenticatedUser implements UserDetailsService {

    private final UserRepository userRepository;

    public AuthenticatedUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Optional<User> get() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(auth -> auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails)
                .map(auth -> (UserDetails) auth.getPrincipal())
                .flatMap(userDetails -> userRepository.findByUsername(userDetails.getUsername()));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public void logout() {
        // Get the current Vaadin request and response
        VaadinServletRequest request = (VaadinServletRequest) VaadinService.getCurrentRequest();
        VaadinServletResponse response = (VaadinServletResponse) VaadinService.getCurrentResponse();

        if (request != null && response != null) {
            HttpServletRequest httpRequest = request.getHttpServletRequest();
            HttpServletResponse httpResponse = response.getHttpServletResponse();

            // Use SecurityContextLogoutHandler to perform logout
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(httpRequest, httpResponse,
                    SecurityContextHolder.getContext().getAuthentication());
        } else {
            // Fallback: clear the SecurityContextHolder directly
            SecurityContextHolder.clearContext();
        }
    }
}