package com.voghan.pillar.common.workflows;

import com.adobe.granite.asset.api.Asset;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.voghan.pillar.common.AuthUtil;
import java.util.Map;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
    // com.adobe.granite.workflow.exec.EvalScript unavailable
    service = WorkflowProcess.class,
    property = {
        "process.label=Pillar Simple Asset Condition Rule"
    }
)
public class SimpleAssetConditionRule implements WorkflowProcess {
  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAssetConditionRule.class);

  public static final String SERVICE_NAME = "SimpleAssetConditionRule";

  @Reference
  private ResourceResolverFactory resourceResolverFactory;

  /**
   * Add metaData attribute evaluated as a boolean to be used in workflows
   * in collaboration with /apps/workflow/scripts/is_evaluated_condition.emca
   */
  @Override
  public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
    String payloadPath = workItem.getWorkflowData().getPayload().toString();

    final Map<String, Object> authInfo = AuthUtil.getAuthInfo(SERVICE_NAME);
    try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authInfo)) {
      Resource resource = resourceResolver.getResource(payloadPath);
      boolean assetFound = resource != null && resource.adaptTo(Asset.class) != null;
      workItem.getWorkflowData().getMetaDataMap().put("evaluated", assetFound);
      LOGGER.info("Evaluated payload {} as {}", payloadPath, assetFound);
    } catch (LoginException e) {
      LOGGER.warn("Workflow process for {} failed.", payloadPath, e);
      throw new WorkflowException(e);
    }
  }

}
