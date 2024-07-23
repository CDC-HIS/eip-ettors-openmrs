package org.ephi.eip.routes;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.ephi.eip.filters.ViralLoadServiceRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ViralLoadRequestPostingRoute extends RouteBuilder {

    @Autowired
    private ViralLoadServiceRequestFilter viralLoadServiceRequestFilter;

    @Override
    public void configure() {
        from("direct:fhir-serviceRequest")
            .routeId("post-viral-load-request")
            .filter(viralLoadServiceRequestFilter)
            .log(LoggingLevel.INFO, "Viral Load Service Request detected")
                .process()
                .end();
    }
}
