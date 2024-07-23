package org.ephi.eip.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Setter
@Getter
@Configuration
public class EttorsConfig {

    @Value("${eip.ettors.server-url}")
    private String ettorsServerUrl;

    @Value("${eip.ettors.username}")
    private String username;

    @Value("${eip.ettors.password}")
    private String password;

    public String basicAuthHeader() {
        // Validate that the username and password properties are set
        if (username.isEmpty() || username.isBlank()) {
            throw new IllegalStateException("The Ettors username property is not set");
        }
        if (password.isEmpty() || password.isBlank()) {
            throw new IllegalStateException("The Ettors password property is not set");
        }

        return "Basic " + java.util.Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }
}
