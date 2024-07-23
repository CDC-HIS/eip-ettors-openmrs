package org.ephi.eip.config;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.apache.camel.CamelContext;
import org.apache.camel.component.fhir.FhirComponent;
import org.openmrs.eip.fhir.spring.OpenmrsFhirAppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Import the {@link OpenmrsFhirAppConfig} class to ensure that the required beans are created.
 */
@Configuration
@Import({OpenmrsFhirAppConfig.class})
public class EIPAppConfig {

    @Bean
    public IGenericClient openmrsFhirClient(@Autowired CamelContext camelContext) {
        FhirComponent fhirComponent = camelContext.getComponent("fhir", FhirComponent.class);
        return fhirComponent.getConfiguration().getClient();
    }
}
