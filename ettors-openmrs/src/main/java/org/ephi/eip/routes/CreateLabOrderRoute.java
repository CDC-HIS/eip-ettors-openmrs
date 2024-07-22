package org.ephi.eip.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CreateLabOrderRoute extends RouteBuilder {
    @Override
    public void configure() {
        from("direct:create-lab-order")
                .routeId("create-lab-order")
                .to("fhir:create/resource?resource=${body}")
                .end();
    }
}
