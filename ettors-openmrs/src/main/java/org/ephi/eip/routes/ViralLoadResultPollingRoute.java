package org.ephi.eip.routes;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.ephi.eip.processors.PendingTaskProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ViralLoadResultPollingRoute extends RouteBuilder {

    @Autowired
    private PendingTaskProcessor pendingTaskProcessor;

    @Override
    public void configure() {
        from("timer://fetch-viral-load-results?fixedRate=true&period=30000")
            .routeId("fetch-viral-load-results")
            .log(LoggingLevel.INFO, "Fetching viral load results started.")
            .process(pendingTaskProcessor)
            .log(LoggingLevel.INFO, "Fetching viral load results complete").end();
    }
}
