package org.ephi.ettors.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ViralLoadRequestPayload {

    @JsonProperty("ID")
    private String ID;

    @JsonProperty("MRN")
    private String mrn;

    @JsonProperty("UAN")
    private String uan;

    @JsonProperty("FacilityCode")
    private String facilityCode;

    @JsonProperty("Requestedby")
    private String requestedBy;

    @JsonProperty("RequestedDate")
    private String requestedDate;

    @JsonProperty("Regimen")
    private String regimen;

    @JsonProperty("DateInitiated")
    private String dateInitiated;

    @JsonProperty("Pregnancy")
    private String pregnancy;

    @JsonProperty("Breastfeeding")
    private String breastfeeding;

    @JsonProperty("CD4MostRecent")
    private String cd4MostRecent;

    @JsonProperty("CD4MostRecentDate")
    private String cd4MostRecentDate;

    @JsonProperty("RoutineVL")
    private String routineVL;

    @JsonProperty("RoutineVLPregnantMother")
    private String RoutineVLPregnantMother;

    @JsonProperty("Targeted")
    private String targeted;

    @JsonProperty("SpecimentCollectedDate")
    private String specimenCollectedDate;

    @JsonProperty("SpecimenType")
    private String specimenType;

    @JsonProperty("SpecimenSentToReferalDate")
    private String specimenSentToReferralDate;

    @JsonProperty("LabId")
    private String labId;

    @JsonProperty("LabName")
    private String labName;

    @JsonProperty("SpecimenRecievedDate")
    private String specimenReceivedDate;

    @JsonProperty("SpecimenQuality")
    private String specimenQuality;

    @JsonProperty("ReasonForRejection")
    private String reasonForRejection;

    @JsonProperty("TestResultDate")
    private String testResultDate;

    @JsonProperty("TestResult")
    private String testResult;

    @JsonProperty("TestedBy")
    private String testedBy;

    @JsonProperty("ReviewedBy")
    private String reviewedBy;

    @JsonProperty("AletSentDate")
    private String alertSentDate;

    @JsonProperty("DispatchDate")
    private String dispatchDate;

    @JsonProperty("ResultReachedToFacDate")
    private String resultReachedToFacDate;

    @JsonProperty("ResultReceivedByFacility")
    private String resultReceivedByFacility;

    @JsonProperty("AttachedToPatientDate")
    private String attachedToPatientDate;

    @JsonProperty("CommunicatedToPatientDate")
    private String communicatedToPatientDate;

    @JsonProperty("RequestID")
    private String requestID;

    @JsonProperty("ResponseID")
    private String responseID;

    @JsonProperty("FacilityName")
    private String facilityName;

    @JsonProperty("RegionName")
    private String regionName;

    @JsonProperty("Sex")
    private String sex;

    @JsonProperty("Age")
    private String age;

    @JsonProperty("AgeInMonths")
    private String ageInMonths;
}
