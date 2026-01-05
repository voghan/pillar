package com.voghan.pillar.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.Externalizer;
import com.voghan.pillar.common.emails.SimpleEmailService;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;

@Component(service = WorkflowProcess.class,
    property = {"process.label=Pillar Workflow Notification Process"})
public class NotificationProcess implements WorkflowProcess {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String SERVICE_NAME = "NotificationProcess";
    private static final String TEMPLATE = "/conf/pillar/notifications/email/workflow-notification.html";

    @Reference
    private SimpleEmailService emailService;

    @Reference
    private Externalizer externalizer;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        String payload = (String) workItem.getWorkflowData().getPayload();
        String processArgs = metaDataMap.get("PROCESS_ARGS", String.class);

        logger.info("Starting workflow for {} with args {}", payload, processArgs);


        final Map<String, Object> authInfo = getAuthInfo();

        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authInfo)) {
            String mailTo = getInitiatorEmail(resourceResolver, workItem);
            Map<String, String> params = buildEmailMessage(resourceResolver, workItem, payload);
            emailService.sendEmail(mailTo, TEMPLATE, params);

        } catch (Exception e) {
            logger.warn("Workflow process for {} failed.", payload, e);
            throw new WorkflowException(e);
        }

    }

    protected Map<String, String> buildEmailMessage(ResourceResolver resourceResolver, WorkItem workItem, String payload) {
        Map<String, String> params = new HashMap<>();
        params.put("workflowModel", workItem.getWorkflow().getWorkflowModel().getTitle());
        params.put("givenName", getInitiator(workItem));
        params.put("authorLink", getAuthorLink(resourceResolver, "/libs/cq/workflow/admin/console/content/instances.html"));
        params.put("payload", payload);

        return params;
    }

    protected String getInitiator(WorkItem workItem) {
        String initiatorUserId = workItem.getWorkflow().getInitiator();

        if (initiatorUserId.equals("admin")) {
            initiatorUserId = "Wally";
        }

        return initiatorUserId;
    }

    protected String getInitiatorEmail(ResourceResolver resourceResolver, WorkItem workItem) throws RepositoryException {
        String email = null;
        String initiatorUserId = workItem.getWorkflow().getInitiator();
        Session session = resourceResolver.adaptTo(Session.class);
        UserManager userManager = ((JackrabbitSession) session).getUserManager();
        Authorizable authorizable = userManager.getAuthorizable(initiatorUserId);

        if (authorizable != null && authorizable.getProperty("./profile/email") != null) {
            email = authorizable.getProperty("./profile/email")[0].getString();
            workItem.getWorkflowData().getMetaDataMap().put("initiatorEmail", email);
        }
        return email;
    }

    protected String getAuthorLink(ResourceResolver resourceResolver, String path) {
        String authorLink = "http://localhost:4502" + path;

        if (externalizer != null && resourceResolver != null) {
            authorLink = externalizer.externalLink(resourceResolver, Externalizer.AUTHOR, path);
        }

        return authorLink;
    }

    protected Map<String, Object> getAuthInfo() {
        final Map<String, Object> authInfo = new HashMap<>();
        authInfo.put(ResourceResolverFactory.SUBSERVICE, SERVICE_NAME);
        return authInfo;
    }
}
