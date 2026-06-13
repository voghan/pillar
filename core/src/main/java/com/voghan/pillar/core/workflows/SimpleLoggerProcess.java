package com.voghan.pillar.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = WorkflowProcess.class,
    property = {"process.label=Pillar Simple Logger Process"})
public class SimpleLoggerProcess implements WorkflowProcess {
  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleLoggerProcess.class);

  @Override
  public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
    String payload = workItem.getWorkflowData().getPayload().toString();
    String processArgs = metaDataMap.get("PROCESS_ARGS", String.class);

    LOGGER.info("Simple message: payload '{}' processArg '{}'",payload, processArgs);
  }
}
