package org.ephi.eip.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.ephi.eip.config.EttorsConfig;
import org.ephi.ettors.model.ViralLoadRequestPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FetchViralLoadResultByRequestIDRoute extends RouteBuilder {

    @Autowired
    private EttorsConfig ettorsConfig;

    @Override
    public void configure() {
        from("direct:fetch-viral-load-result-by-request-id")
            .routeId("get-viral-load-result-by-request-id")
            .setHeader("CamelHttpMethod", constant("GET"))
            .setHeader("Authorization", constant(ettorsConfig.basicAuthHeader()))
            .toD(ettorsConfig.getEttorsServerUrl() + "/api/ViralLoadModel/GetViralLoadByRequestID?requestID=${header.orderNumber}")
            .unmarshal()
            .json(JsonLibrary.Jackson, ViralLoadRequestPayload.class).end();
    }
}
