package org.ephi.eip.routes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.ephi.eip.Constants;
import org.ephi.eip.config.EttorsConfig;
import org.ephi.eip.config.OpenmrsConfig;
import org.ephi.eip.filters.ViralLoadServiceRequestFilter;
import org.ephi.eip.processors.TaskProcessor;
import org.ephi.eip.processors.ViralLoadServiceRequestProcessor;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ViralLoadRequestPostingRoute extends RouteBuilder {

    @Autowired
    private ViralLoadServiceRequestFilter viralLoadServiceRequestFilter;

    @Autowired
    private ViralLoadServiceRequestProcessor viralLoadServiceRequestProcessor;

    @Autowired
    private EttorsConfig ettorsConfig;

    @Autowired
    private OpenmrsConfig openmrsConfig;

    @Autowired
    private TaskProcessor taskProcessor;

    @Override
    public void configure() {
        from("direct:fhir-servicerequest")
            .routeId("post-viral-load-request")
            .filter(body().isNotNull())
            .filter(exchange -> exchange.getMessage().getBody() instanceof ServiceRequest)
            .filter(viralLoadServiceRequestFilter)
            .log(LoggingLevel.INFO, "Viral Load ServiceRequest detected")
            .process(exchange -> {
                    ServiceRequest serviceRequest = exchange.getMessage().getBody(ServiceRequest.class);
                    exchange.setProperty(Constants.EXCHANGE_PROPERTY_SERVICE_REQUEST, serviceRequest);
                    exchange.setProperty(Constants.EXCHANGE_PROPERTY_SERVICE_REQUEST_ID, serviceRequest.getIdPart());
                })
            .setHeader("CamelHttpMethod", constant("GET"))
            .setHeader(Constants.CONTENT_TYPE, constant(Constants.APPLICATION_JSON))
            .setHeader("Authorization", constant(openmrsConfig.basicAuthHeader()))
            .toD(openmrsConfig.getServerUrl() + "/ws/rest/v1/order/${exchangeProperty." + Constants.EXCHANGE_PROPERTY_SERVICE_REQUEST_ID + "}")
            .process(exchange -> {
                    String orderStringBody = exchange.getMessage().getBody(String.class);
                    exchange.setProperty(Constants.EXCHANGE_PROPERTY_ORDER_NUMBER, readOrderNumberFromJson(orderStringBody));
                })
            .setHeader(Constants.CAMEL_HTTP_METHOD, constant(Constants.POST))
            .process(viralLoadServiceRequestProcessor)
            .setHeader(Constants.CAMEL_HTTP_METHOD, constant(Constants.POST))
            .setHeader(Constants.CONTENT_TYPE, constant(Constants.APPLICATION_JSON))
            .setHeader(Constants.AUTHORIZATION, constant(ettorsConfig.basicAuthHeader()))
            .toD(ettorsConfig.getEttorsServerUrl() + "/api/ViralLoadModel/InsertViralLoad")
            .log(LoggingLevel.INFO, "Viral Load Request posted to ETTORS")
            .process(taskProcessor)
            .to("fhir://create/resource?inBody=resource")
            .log(LoggingLevel.INFO, "Task created").end();
    }

    private String readOrderNumberFromJson(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        return root.path("orderNumber").asText();
    }
}
