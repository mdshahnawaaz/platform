package com.logistic.platform.Configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.logistic.platform.models.Driver;
import com.logistic.platform.models.User;
import com.logistic.platform.repository.UserRepository;
import com.logistic.platform.services.DriverService;

@Component
public class PortalAuthenticationProvider implements AuthenticationProvider {

    private final UserRepository userRepository;
    private final DriverService driverService;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    public PortalAuthenticationProvider(UserRepository userRepository, DriverService driverService) {
        this.userRepository = userRepository;
        this.driverService = driverService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String principalValue = String.valueOf(authentication.getPrincipal());
        String credentials = authentication.getCredentials() == null ? "" : authentication.getCredentials().toString().trim();

        String[] parts = principalValue.split(":", 2);
        if (parts.length != 2) {
            throw new BadCredentialsException("Unsupported authentication request.");
        }

        String loginType = parts[0];
        String identifier = parts[1];

        return switch (loginType) {
            case "admin" -> authenticateAdmin(identifier, credentials);
            case "user" -> authenticateUser(identifier, credentials);
            case "driver" -> authenticateDriver(identifier, credentials);
            default -> throw new BadCredentialsException("Unsupported login type.");
        };
    }

    private Authentication authenticateAdmin(String username, String password) {
        if (!adminUsername.equals(username) || !adminPassword.equals(password)) {
            throw new BadCredentialsException("Invalid admin username or password.");
        }

        PortalPrincipal principal = new PortalPrincipal("admin", null, username);
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    private Authentication authenticateUser(String userIdValue, String email) {
        int userId = parseNumericId(userIdValue, "user");
        User user = userRepository.findById(userId)
                .filter(item -> item.getEmail() != null)
                .filter(item -> item.getEmail().equalsIgnoreCase(email))
                .orElseThrow(() -> new BadCredentialsException("Invalid user ID or email."));

        PortalPrincipal principal = new PortalPrincipal("user", user.getId(), user.getUsername());
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private Authentication authenticateDriver(String driverIdValue, String licenseNumber) {
        int driverId = parseNumericId(driverIdValue, "driver");
        Driver driver = driverService.authenticateDriver(driverId, licenseNumber)
                .orElseThrow(() -> new BadCredentialsException("Invalid driver ID or license number."));

        PortalPrincipal principal = new PortalPrincipal("driver", driver.getId(), driver.getName());
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_DRIVER")));
    }

    private int parseNumericId(String value, String loginType) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new BadCredentialsException("Invalid " + loginType + " identifier.", exception);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
