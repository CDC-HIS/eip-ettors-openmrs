package org.ephi.eip.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.fhir.FhirComponent;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Observation;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ViralLoadRequestProcessor implements Processor {

    private static final String VIRAL_LOAD_CONCEPT_UUID = "856AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

    //private static final String TEST_ORDER_TYPE_UUID = "52a447d3-a64a-11e3-9aeb-50e549534c5e";

    private static final String CARE_SETTING_OUTPATIENT_UUID = "6f0c9a92-6f24-11e3-af88-005056821db0";

    @Override
    public void process(Exchange exchange) {
        Observation observation = exchange.getMessage().getBody(Observation.class);

        FhirComponent fhirComponent = exchange.getContext().getComponent("fhir", FhirComponent.class);

        if (observation != null) {
            Encounter encounter = fhirComponent.getConfiguration().getClient()
                    .read()
                    .resource(Encounter.class)
                    .withId(observation.getEncounter().getReference().split("/")[1])
                    .execute();
            // construct order post payload
            String OrderPayload = getOrderPayload(observation, encounter);
            log.info("Order payload: {}", OrderPayload);

            exchange.getMessage().setBody(OrderPayload);
        }
    }

    private static String getOrderPayload(Observation observation, Encounter encounter) {
        String encounterUuid = observation.getEncounter().getReference().split("/")[1];
        String patientUuid = observation.getSubject().getReference().split("/")[1];

        return String.format("{\"type\":\"%s\",\"action\":\"new\",\"careSetting\":\"%s\",\"encounter\":\"%s\",\"patient\":\"%s\",\"concept\":\"%s\", \"orderer\":\"%s\"}",
                "testorder",
                CARE_SETTING_OUTPATIENT_UUID,
                encounterUuid,
                patientUuid,
                VIRAL_LOAD_CONCEPT_UUID,
                encounter.getParticipantFirstRep().getIndividual().getReference().split("/")[1]);
    }
}
