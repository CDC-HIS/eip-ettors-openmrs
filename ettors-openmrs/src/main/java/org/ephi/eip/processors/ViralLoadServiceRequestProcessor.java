package org.ephi.eip.processors;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.fhir.FhirComponent;
import org.ephi.eip.config.EttorsOpenmrsConfig;
import org.ephi.ettors.model.ViralLoadRequestPayload;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class ViralLoadServiceRequestProcessor implements Processor {

    @Autowired
    private EttorsOpenmrsConfig ettorsOpenmrsConfig;

    @Override
    public void process(Exchange exchange) {
        ServiceRequest serviceRequest = exchange.getMessage().getBody(ServiceRequest.class);
        FhirComponent fhirComponent = exchange.getContext().getComponent("fhir", FhirComponent.class);
        IGenericClient openmrsFhirClient = fhirComponent.getConfiguration().getClient();

        if (serviceRequest != null) {
            String patientUuid = serviceRequest.getSubject().getReference().split("/")[1];
            String encounterUuid = serviceRequest.getEncounter().getReference().split("/")[1];
            Patient patient = openmrsFhirClient.read()
                    .resource(Patient.class)
                    .withId(patientUuid)
                    .execute();
            Encounter encounter = openmrsFhirClient.read()
                    .resource(Encounter.class)
                    .withId(encounterUuid)
                    .execute();
            Location location = openmrsFhirClient.read()
                    .resource(Location.class)
                    .withId(encounter.getLocationFirstRep().getLocation().getReference().split("/")[1])
                    .execute();

            ViralLoadRequestPayload payload = this.assembleViralLoadRequestPayload(patient, encounter, location);
            try {
                String payloadJsonString = new ObjectMapper().writeValueAsString(payload);
                log.info("Viral Load Request: {}", payloadJsonString);
                exchange.getMessage().setBody(payloadJsonString);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String getPatientIdentifier(Patient patient, String identifierTypeUuid) {
        return patient.getIdentifier().stream()
                .filter(identifier -> identifier.getType().getCoding().stream()
                        .anyMatch(coding -> coding.getCode().equals(identifierTypeUuid))
                )
                .findFirst()
                .map(Identifier::getValue)
                .orElse(null);
    }


    private ViralLoadRequestPayload assembleViralLoadRequestPayload(Patient patient, Encounter encounter, Location location) {
        ViralLoadRequestPayload viralLoadRequestPayload = new ViralLoadRequestPayload();
        // Set patient UAN & MRN Identifiers
        viralLoadRequestPayload.setUan(this.getPatientIdentifier(patient, ettorsOpenmrsConfig.getUANPatientIdentifierType()));
        viralLoadRequestPayload.setMrn(this.getPatientIdentifier(patient, ettorsOpenmrsConfig.getMRNPatientIdentifierType()));


        // Set encounter location
        viralLoadRequestPayload.setFacilityCode(location.getName());
        viralLoadRequestPayload.setRequestedBy("Nurse 1");
        Instant startInstant = encounter.getPeriod().getStart().toInstant();
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDateString = OffsetDateTime.ofInstant(startInstant, ZoneOffset.UTC).format(outputFormatter);

        viralLoadRequestPayload.setRequestedDate(formattedDateString);
        viralLoadRequestPayload.setRegimen("Regimen 1");
        viralLoadRequestPayload.setDateInitiated("2021-01-01");
        viralLoadRequestPayload.setPregnancy("No");
        viralLoadRequestPayload.setBreastfeeding("No");
        viralLoadRequestPayload.setCd4MostRecent("100");
        viralLoadRequestPayload.setCd4MostRecentDate("2021-01-01");
        viralLoadRequestPayload.setRoutineVL("Yes");
        viralLoadRequestPayload.setRoutineVLPregnantMother("No");
        viralLoadRequestPayload.setTargeted("No");
        viralLoadRequestPayload.setSpecimenCollectedDate("1900-01-01T00:00:00");
        viralLoadRequestPayload.setSpecimenType("Blood");
        viralLoadRequestPayload.setSpecimenSentToReferralDate("1900-01-01T00:00:00");
        viralLoadRequestPayload.setLabId("Lab 1");
        viralLoadRequestPayload.setLabName("Lab 1");
        viralLoadRequestPayload.setSpecimenReceivedDate("1900-01-01T00:00:00");
        viralLoadRequestPayload.setSpecimenQuality("Good");
        viralLoadRequestPayload.setReasonForRejection("N/A");
        viralLoadRequestPayload.setTestedBy("N/A");
        viralLoadRequestPayload.setTestResult("N/A");
        viralLoadRequestPayload.setTestResultDate("1900-01-01T00:00:00");
        viralLoadRequestPayload.setReviewedBy("N/A");
        viralLoadRequestPayload.setAlertSentDate("1900-01-01T00:00:00");
        viralLoadRequestPayload.setDispatchDate("1900-01-01T00:00:00");
        viralLoadRequestPayload.setResultReachedToFacDate("1900-01-01T00:00:00");
        viralLoadRequestPayload.setResultReceivedByFacility("N/A");
        viralLoadRequestPayload.setAttachedToPatientDate("1900-01-01T00:00:00");
        viralLoadRequestPayload.setCommunicatedToPatientDate("1900-01-01T00:00:00");

        viralLoadRequestPayload.setRequestID("Order 1");
        viralLoadRequestPayload.setResponseID("N/A");
        viralLoadRequestPayload.setFacilityName("Facility 1");
        viralLoadRequestPayload.setRegionName("Region 1");
        viralLoadRequestPayload.setSex("M");
        viralLoadRequestPayload.setAge("25");
        viralLoadRequestPayload.setAgeInMonths("N/A");


        return viralLoadRequestPayload;
    }
}
