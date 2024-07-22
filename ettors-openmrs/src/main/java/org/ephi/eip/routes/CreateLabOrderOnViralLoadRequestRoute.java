package org.ephi.eip.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.ephi.eip.processors.ViralLoadRequestProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateLabOrderOnViralLoadRequestRoute extends RouteBuilder {

    @Autowired
    private ViralLoadRequestProcessor viralLoadRequestProcessor;
    @Override
    public void configure() {
        from("direct:fhir-obs")
                .log(LoggingLevel.INFO, "Processing FHIR observation")
                .process(viralLoadRequestProcessor)
                .to("direct:create-lab-order")
                .log(LoggingLevel.INFO, "Done creating Lab Order")
                .end();
    }
}
