package org.ephi.eip.routes;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.ephi.eip.Constants;
import org.ephi.eip.config.EttorsConfig;
import org.ephi.eip.filters.ViralLoadServiceRequestFilter;
import org.ephi.eip.processors.ViralLoadServiceRequestProcessor;
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

    @Override
    public void configure() {
        from("direct:fhir-servicerequest")
            .routeId("post-viral-load-request")
            .filter(viralLoadServiceRequestFilter)
            .log(LoggingLevel.INFO, "Viral Load Service Request detected")
            .process(viralLoadServiceRequestProcessor)
            .setHeader(Constants.CAMEL_HTTP_METHOD, constant(Constants.POST))
            .setHeader(Constants.CONTENT_TYPE, constant(Constants.APPLICATION_JSON))
            .setHeader(Constants.AUTHORIZATION, constant(ettorsConfig.basicAuthHeader()))
            .toD(ettorsConfig.getEttorsServerUrl() + "/Help/Api/POST-api-ViralLoadModel-InsertViralLoad")
            .log(LoggingLevel.INFO, "Viral posted to ETTORS successfully").end();
    }
}
