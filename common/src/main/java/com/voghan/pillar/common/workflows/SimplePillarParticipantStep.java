package com.voghan.pillar.common.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    service = ParticipantStepChooser.class,
    property = {
        "chooser.label=Pillar - Workflow System User Chooser"
    }
)
public class SimplePillarParticipantStep implements ParticipantStepChooser {
  private static final Logger LOGGER = LoggerFactory.getLogger(SimplePillarParticipantStep.class);

  @Override
  public String getParticipant(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
    LOGGER.info("Entering SimplePillarParticipantStep");

    return "pillar-workflow-service-user";
  }
}
