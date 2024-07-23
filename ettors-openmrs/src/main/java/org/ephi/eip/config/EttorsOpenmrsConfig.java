package org.ephi.eip.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Getter
@Setter
@Configuration
public class EttorsOpenmrsConfig {

    @Value("${eip.ettors.openmrs.MRNPatientIdentifierType}")
    private String MRNPatientIdentifierType;

    @Value("${eip.ettors.openmrs.UANPatientIdentifierType}")
    private String UANPatientIdentifierType;

    public boolean validateFunctionalConfig() {
        if (MRNPatientIdentifierType.isEmpty() || MRNPatientIdentifierType.isBlank()) {
            throw new IllegalStateException("The OpenMRS MRN Patient Identifier Type property is not set");
        }
        if (UANPatientIdentifierType.isEmpty() || UANPatientIdentifierType.isBlank()) {
            throw new IllegalStateException("The OpenMRS UAN Patient Identifier Type property is not set");
        }
        return true;
    }
}
