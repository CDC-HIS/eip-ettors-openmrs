package org.ephi.eip.converters;

import ca.uhn.fhir.context.FhirContext;
import org.apache.camel.Converter;
import org.hl7.fhir.r4.model.DomainResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Component
@Converter
public class FhirResourceConverter {

    @Converter
    private InputStream convertToInputStream(DomainResource resource) {
        String json = FhirContext.forR4().newJsonParser().encodeResourceToString(resource);
        return new ByteArrayInputStream(json.getBytes());
    }
}
