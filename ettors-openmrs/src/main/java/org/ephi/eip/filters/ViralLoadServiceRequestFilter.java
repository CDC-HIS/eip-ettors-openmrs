package org.ephi.eip.filters;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ViralLoadServiceRequestFilter implements Predicate {

    private static final String VIRAL_LOAD_CONCEPT_UUID = "856AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

    @Override
    public boolean matches(Exchange exchange) {
        ServiceRequest serviceRequest = exchange.getMessage().getBody(ServiceRequest.class);
        return serviceRequest.getCode().getCoding().stream()
                .anyMatch(coding -> VIRAL_LOAD_CONCEPT_UUID.equals(coding.getCode()));
    }
}
