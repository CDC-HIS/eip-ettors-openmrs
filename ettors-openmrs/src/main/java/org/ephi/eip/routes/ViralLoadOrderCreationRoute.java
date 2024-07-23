package org.ephi.eip.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.ephi.eip.Constants;
import org.ephi.eip.config.OpenmrsConfig;
import org.ephi.eip.filters.ViralLoadObservationFilter;
import org.ephi.eip.processors.ViralLoadOrderProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ViralLoadOrderCreationRoute extends RouteBuilder {

    @Autowired
    private ViralLoadObservationFilter viralLoadObservationFilter;

    @Autowired
    private ViralLoadOrderProcessor viralLoadOrderProcessor;

    @Autowired
    private OpenmrsConfig openmrsConfig;

    @Override
    public void configure() {
        from("direct:fhir-obs")
            .routeId("fhir-obs-to-viral-load-service-request-router")
            .filter(viralLoadObservationFilter)
            .log(LoggingLevel.INFO, "Viral Load request detected")
            .process(viralLoadOrderProcessor)
            .setHeader(Constants.CAMEL_HTTP_METHOD, constant(Constants.POST))
            .setHeader(Constants.CONTENT_TYPE, constant(Constants.APPLICATION_JSON))
            .setHeader(Constants.AUTHORIZATION, constant(openmrsConfig.basicAuthHeader()))
            .toD(openmrsConfig.getServerUrl() + "/ws/rest/v1/order")
            .log(LoggingLevel.INFO, "Viral Load Order created with Order Number: ${body}").end();
    }
}
