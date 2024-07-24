package org.ephi.eip.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.ephi.eip.config.OpenmrsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SaveObservationRoute extends RouteBuilder {

    @Autowired
    private OpenmrsConfig openmrsConfig;

    @Override
    public void configure() {
        from("direct:save-openmrs-observation")
            .routeId("save-openmrs-observation")
            .setHeader("CamelHttpMethod", constant("POST"))
            .setHeader("Content-Type", constant("application/json"))
            .setHeader("Authorization", constant(openmrsConfig.basicAuthHeader()))
            .toD(openmrsConfig.getServerUrl() + "/ws/rest/v1/obs")
            .log(LoggingLevel.INFO, "Observation saved").end();
    }
}
