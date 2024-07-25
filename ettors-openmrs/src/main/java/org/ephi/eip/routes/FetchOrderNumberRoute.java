package org.ephi.eip.routes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.apache.camel.builder.RouteBuilder;
import org.ephi.eip.Constants;
import org.ephi.eip.config.OpenmrsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Setter
@Component
public class FetchOrderNumberRoute extends RouteBuilder {

    @Autowired
    private OpenmrsConfig openmrsConfig;

    @Override
    public void configure() {
        from("direct:fetch-order-number")
            .routeId("fetch-order-number")
            .setHeader("CamelHttpMethod", constant("GET"))
            .setHeader(Constants.CONTENT_TYPE, constant(Constants.APPLICATION_JSON))
            .setHeader("Authorization", constant(openmrsConfig.basicAuthHeader()))
            .toD(openmrsConfig.getServerUrl() + "/ws/rest/v1/order/${header.orderNumber}")
            .process(exchange -> {
            String orderStringBody = exchange.getMessage().getBody(String.class);
            exchange.setProperty(Constants.EXCHANGE_PROPERTY_ORDER_NUMBER, readOrderNumberFromJson(orderStringBody));
            exchange.getMessage().setBody(readOrderNumberFromJson(orderStringBody));
        }).end();
    }

    private String readOrderNumberFromJson(String orderStringBody) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(orderStringBody);
        return root.path("orderNumber").asText();
    }
}
