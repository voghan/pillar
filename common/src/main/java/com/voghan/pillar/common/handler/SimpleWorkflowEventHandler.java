package com.voghan.pillar.common.handler;

import com.day.cq.workflow.event.WorkflowEvent;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = EventHandler.class, immediate = true, property = {
    Constants.SERVICE_DESCRIPTION + "= Workflow event handler",
    EventConstants.EVENT_TOPIC + "=" + WorkflowEvent.EVENT_TOPIC})
public class SimpleWorkflowEventHandler implements EventHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public void handleEvent(Event event) {
        String topic = event.getTopic();
        String instanceId = (String) event.getProperty(WorkflowEvent.WORKFLOW_INSTANCE_ID);

        if (topic.equals(WorkflowEvent.EVENT_TOPIC)) {
            Object eventType = event.getProperty(WorkflowEvent.EVENT_TYPE);

            if (eventType.equals(WorkflowEvent.WORKFLOW_STARTED_EVENT)) {
                LOGGER.info("Workflow has started with workflow instance ID : {}", instanceId);
            } else if (eventType.equals(WorkflowEvent.WORKFLOW_COMPLETED_EVENT)) {
                LOGGER.info("Workflow has completed");
            } else if (eventType.equals(WorkflowEvent.WORKFLOW_RESUMED_EVENT)) {
                LOGGER.info("Workflow is resumed");
            } else if (eventType.equals(WorkflowEvent.WORKFLOW_ABORTED_EVENT)) {
                LOGGER.info("Workflow is aborted");
            } else if (eventType.equals(WorkflowEvent.WORKFLOW_SUSPENDED_EVENT)) {
                LOGGER.info("Workflow is suspended");
            } else if (eventType.equals(WorkflowEvent.WORKITEM_DELEGATION_EVENT)) {
                LOGGER.info("Workflow is delegated");
            } else {
                LOGGER.warn("Something is wrong with a workflow {}", eventType);
            }
        }
    }
}
