package org.ephi.eip.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.ephi.eip.Constants;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskProcessor implements Processor {

    @Override
    public void process(Exchange exchange) {
        ServiceRequest serviceRequest = exchange.getProperty(Constants.EXCHANGE_PROPERTY_SERVICE_REQUEST, ServiceRequest.class);
        String orderNumber = exchange.getProperty(Constants.EXCHANGE_PROPERTY_ORDER_NUMBER, String.class);
        Task task = new Task();
        // Add order number as an identifier to the task
        task.addIdentifier().setSystem(Constants.IDENTIFIER_SYSTEM_ORDER_NUMBER).setValue(orderNumber);
        task.setStatus(Task.TaskStatus.REQUESTED);
        task.setIntent(Task.TaskIntent.ORDER);
        task.setPriority(Task.TaskPriority.ROUTINE);
        task.setEncounter(serviceRequest.getEncounter());
        task.setFor(serviceRequest.getSubject());
        task.setAuthoredOn(serviceRequest.getAuthoredOn());
        task.setRequester(serviceRequest.getRequester());
        task.setCode(serviceRequest.getCode());
        // Add basedOn reference
        Reference basedOn = new Reference();
        basedOn.setReference("ServiceRequest/" + serviceRequest.getIdPart());
        task.addBasedOn(basedOn);
        exchange.getMessage().setBody(task);
    }
}
