package org.ephi.eip.processors;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.fhir.FhirComponent;
import org.ephi.eip.Constants;
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
import java.util.Date;

@Slf4j
@Component
public class ViralLoadServiceRequestProcessor implements Processor {

    @Autowired
    private EttorsOpenmrsConfig ettorsOpenmrsConfig;

    private static final String DEFAULT_DATETIME = "1900-01-01T00:00:00";

    @Override
    public void process(Exchange exchange) {
        ServiceRequest serviceRequest = exchange.getProperty(Constants.EXCHANGE_PROPERTY_SERVICE_REQUEST, ServiceRequest.class);
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
            String OrderNumber = exchange.getProperty(Constants.EXCHANGE_PROPERTY_ORDER_NUMBER, String.class);
            payload.setRequestID(OrderNumber);

            ViralLoadRequestPayload result = exchange.getContext().createProducerTemplate()
                    .requestBodyAndHeader("direct:fetch-viral-load-result-by-request-id",
                    null, "orderNumber", OrderNumber, ViralLoadRequestPayload.class);
            if (result != null) {
                // Update Viral Load request.
                payload.setID(result.getID());
                payload.setResponseID(result.getResponseID());
                exchange.getMessage().setHeader("CamelHttpMethod", "POST");
            } else {
                // Create Viral Load request.
                exchange.getMessage().setHeader("CamelHttpMethod", "POST");
            }
            try {
                String payloadJsonString = new ObjectMapper().writeValueAsString(payload);
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
        viralLoadRequestPayload.setFacilityCode("141060012");
        viralLoadRequestPayload.setRequestedBy("Nurse 1");
        Instant startInstant = encounter.getPeriod().getStart().toInstant();
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDateString = OffsetDateTime.ofInstant(startInstant, ZoneOffset.UTC).format(outputFormatter);

        viralLoadRequestPayload.setRequestedDate(formattedDateString);
        viralLoadRequestPayload.setRegimen("1j (TDF+3TC+DTG)");
        viralLoadRequestPayload.setDateInitiated("2005-07-15T00:00:00");
        viralLoadRequestPayload.setPregnancy("No");
        viralLoadRequestPayload.setBreastfeeding("No");
        viralLoadRequestPayload.setCd4MostRecent("");
        viralLoadRequestPayload.setCd4MostRecentDate("");
        viralLoadRequestPayload.setRoutineVL("Annual VL Test");
        viralLoadRequestPayload.setRoutineVLPregnantMother("No");
        viralLoadRequestPayload.setTargeted("");
        viralLoadRequestPayload.setSpecimenCollectedDate("1900-01-01T00:00:00");
        viralLoadRequestPayload.setSpecimenType("Plasma");
        viralLoadRequestPayload.setSpecimenSentToReferralDate(DEFAULT_DATETIME);
        viralLoadRequestPayload.setLabId("");
        viralLoadRequestPayload.setLabName("Addis Ababa Regional Lab");
        viralLoadRequestPayload.setSpecimenReceivedDate(DEFAULT_DATETIME);
        viralLoadRequestPayload.setSpecimenQuality("");
//        viralLoadRequestPayload.setReasonForRejection("");
//        viralLoadRequestPayload.setTestedBy("");
//        viralLoadRequestPayload.setTestResult("");
//        viralLoadRequestPayload.setTestResultDate("1900-01-01T00:00:00");
       // viralLoadRequestPayload.setReviewedBy("");
       //viralLoadRequestPayload.setAlertSentDate("1900-01-01T00:00:00");
        //viralLoadRequestPayload.setDispatchDate("1900-01-01T00:00:00");
        // viralLoadRequestPayload.setResultReachedToFacDate("1900-01-01T00:00:00");
        //viralLoadRequestPayload.setResultReceivedByFacility("");
        viralLoadRequestPayload.setAttachedToPatientDate(DEFAULT_DATETIME);
        viralLoadRequestPayload.setCommunicatedToPatientDate(DEFAULT_DATETIME);
        // viralLoadRequestPayload.setResponseID("");
        viralLoadRequestPayload.setFacilityName(location.getName());
        viralLoadRequestPayload.setRegionName("Addis Ababa");
        viralLoadRequestPayload.setSex(patient.getGender().getDisplay());
        viralLoadRequestPayload.setAge(calculateAge(patient.getBirthDate()));
        return viralLoadRequestPayload;
    }

    private String calculateAge(Date birthDate) {
        Instant birthInstant = birthDate.toInstant();
        Instant now = Instant.now();
        long years = birthInstant.atZone(ZoneOffset.UTC).until(now.atZone(ZoneOffset.UTC), java.time.temporal.ChronoUnit.YEARS);
        return String.valueOf(years);
    }
}
