package org.ephi.eip.processors;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ViralLoadServiceRequestProcessor implements Processor {

    @Autowired
    private IGenericClient openmrsFhirClient;

    @Override
    public void process(Exchange exchange) {
        ServiceRequest serviceRequest = exchange.getMessage().getBody(ServiceRequest.class);
        if (serviceRequest != null) {
            String patientUuid = serviceRequest.getSubject().getReference().split("/")[1];
            String encounterUuid = serviceRequest.getEncounter().getReference().split("/")[1];

            Patient patient = openmrsFhirClient.read()
                    .resource(Patient.class)
                    .withId(patientUuid)
                    .execute();
        }
    }


    private void assembleViralLoadRequestPayload() {

    }
}
