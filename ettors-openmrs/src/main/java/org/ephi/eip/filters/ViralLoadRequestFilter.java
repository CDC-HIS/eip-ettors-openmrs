package org.ephi.eip.filters;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This class is used to filter out viral load requests from the list of observations.
 */
@Slf4j
@Component
public class ViralLoadRequestFilter implements Predicate {

    // The UUID of the concept that represents the viral load indicator in OpenMRS
    // If not set, the application won't start as this is a required property.
    @Value("${eip.ettors.openmrs.viralLoadIndicatorConceptUuid}")
    private String viralLoadIndicatorConceptUuid;

    @Override
    public boolean matches(Exchange exchange) {
        if (viralLoadIndicatorConceptUuid.isEmpty() || viralLoadIndicatorConceptUuid.isBlank()) {
            throw new IllegalStateException("The viralLoadIndicatorConceptUuid property is not set");
        }
        Observation observation = exchange.getMessage().getBody(Observation.class);
        return observation.getCode().getCoding().stream()
                .anyMatch(coding -> viralLoadIndicatorConceptUuid.equals(coding.getCode()));
    }
}
