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
        Task task = new Task();
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
        basedOn.setReference(serviceRequest.getId());
        task.addBasedOn(basedOn);
        exchange.getMessage().setBody(task);
    }
}
