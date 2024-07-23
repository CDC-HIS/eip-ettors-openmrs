package org.ephi.eip.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * This class is used to hold the configuration properties for the OpenMRS server.
 */
@Setter
@Getter
@Configuration
public class OpenmrsConfig {

    @Value("${eip.openmrs.serverUrl}")
    private String serverUrl;

    @Value("${eip.openmrs.username}")
    private String username;

    @Value("${eip.openmrs.password}")
    private String password;

    public String basicAuthHeader() {
        // Validate that the username and password properties are set
        if (username.isEmpty() || username.isBlank()) {
            throw new IllegalStateException("The OpenMRS username property is not set");
        }
        if (password.isEmpty() || password.isBlank()) {
            throw new IllegalStateException("The OpenMRS password property is not set");
        }

        return "Basic " + java.util.Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }
}
