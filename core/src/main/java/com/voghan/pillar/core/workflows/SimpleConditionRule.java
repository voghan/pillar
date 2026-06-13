package com.voghan.pillar.core.workflows;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    // com.adobe.granite.workflow.exec.EvalScript unavailable
    service = SimpleConditionRule.class,
    property = {
        "process.label=Pillar Simple Condition Rule"
    }
)
public class SimpleConditionRule {
  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleConditionRule.class);

  public boolean evaluate(WorkItem workItem, WorkflowSession session, MetaDataMap args) {
    LOGGER.info("Evaluating Simple Condition Rule");
    String payloadPath = workItem.getWorkflowData().getPayload().toString();
    ResourceResolver resourceResolver = session.adaptTo(ResourceResolver.class);
    if (resourceResolver == null) return false;

    Resource resource = resourceResolver.getResource(payloadPath);
    if (resource == null) return false;

    return resource.adaptTo(Page.class) != null;
  }
}
