package org.ephi.eip.processors;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.fhir.FhirComponent;
import org.ephi.ettors.model.ViralLoadRequestPayload;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Task;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class PendingTaskProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
        FhirComponent fhir = exchange.getContext().getComponent("fhir", FhirComponent.class);
        IGenericClient client = fhir.getConfiguration().getClient();
        // Read pending tasks
        Bundle pendingTasks = client.search()
                .forResource("Task")
                .where(Task.STATUS.exactly().code("requested"))
                .returnBundle(Bundle.class)
                .execute();

        log.info("{} pending order tasks found", pendingTasks.getEntry().size());

        try (ProducerTemplate producerTemplate = exchange.getContext().createProducerTemplate()) {
            pendingTasks.getEntry().forEach(entry -> {
                Task task = (Task) entry.getResource();
                String orderNumber = fetchOrderNumber(exchange, task);
                log.info("Order number fetched: {}", orderNumber);

                ViralLoadRequestPayload result = producerTemplate.requestBodyAndHeader("direct:fetch-viral-load-result-by-request-id",
                        task, "orderNumber", orderNumber, ViralLoadRequestPayload.class);
                if (result != null) {
                    String viralResult = result.getTestResult();
                    // Validate viral load result
                    if (viralResult != null && !viralResult.equals("N/A") && viralResult.matches("\\d+")) {
                        // Attached viral load result to Encounter
                        this.saveViralResults(exchange, result, task);

                        // Mark task as completed
                        task.setStatus(Task.TaskStatus.COMPLETED);
                        client.update().resource(task).execute();
                    }
                } else {
                    log.info("No viral load result found for order number: {}", orderNumber);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String fetchOrderNumber(Exchange exchange, Task task) {
        try (ProducerTemplate producerTemplate = exchange.getContext().createProducerTemplate()) {
            return producerTemplate.requestBodyAndHeader("direct:fetch-order-number",
                    task, "orderNumber", task.getBasedOnFirstRep().getReference().split("/")[1], String.class);
        } catch (IOException e) {
            throw new RuntimeException("Error fetching Order Number", e);
        }
    }

    private void saveOpenmrsObservation(Exchange exchange, String payload) {
        try (ProducerTemplate producerTemplate = exchange.getContext().createProducerTemplate()) {
            producerTemplate.sendBody("direct:save-openmrs-observation", payload);
        } catch (IOException e) {
            throw new RuntimeException("Error saving OpenMRS observation", e);
        }
    }

    private void saveViralResults(Exchange  exchange, ViralLoadRequestPayload payload, Task task) {
        // Save viral load count as an observation
        String viralLoadCountStringPayload = "{\n" +
                "  \"person\": \"" + task.getFor().getReference().split("/")[1] + "\",\n" +
                "  \"encounter\": \"" + task.getEncounter().getReference().split("/")[1] + "\",\n" +
                "  \"concept\": \"856AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "  \"obsDatetime\": \"" + payload.getTestResultDate() + "\",\n" +
                "  \"value\": " + payload.getTestResult() + "\n" +
                "}";
        this.saveOpenmrsObservation(exchange, viralLoadCountStringPayload);
    }
}
