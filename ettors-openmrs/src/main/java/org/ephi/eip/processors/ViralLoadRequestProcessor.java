package org.ephi.eip.processors;

import ca.uhn.fhir.context.FhirContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ViralLoadRequestProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Message message = exchange.getMessage();
        // Perform processing on the viral load request
        Observation observation = message.getBody(Observation.class);

        if (observation != null) {
            observation.getCode().getCoding().forEach(coding -> {
                log.info("Coding system: {}", coding.getSystem());
                log.info("Code: {}", coding.getCode());
                log.info("Display: {}", coding.getDisplay());
                log.info("Version: {}", coding.getVersion());
                log.info("Extension: {}", coding.getExtension());
                log.info("Coding: {}", coding);
            });

            ServiceRequest serviceRequest = new ServiceRequest();
            // One-one mapping between ServiceRequest and Observations
            serviceRequest.setId(observation.getId());
            serviceRequest.setIntent(ServiceRequest.ServiceRequestIntent.ORDER);
            serviceRequest.setStatus(ServiceRequest.ServiceRequestStatus.ACTIVE);
            serviceRequest.setEncounter(observation.getEncounter());
            serviceRequest.setCode(observation.getCode());
            serviceRequest.setSubject(observation.getSubject());
            serviceRequest.setRequester(observation.getPerformerFirstRep());
            serviceRequest.setPerformer(observation.getPerformer());
            message.setBody(FhirContext.forR4().newJsonParser().encodeResourceToString(serviceRequest));
        }
    }
}
