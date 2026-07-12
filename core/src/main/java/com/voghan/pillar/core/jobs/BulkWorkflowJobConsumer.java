package com.voghan.pillar.core.jobs;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.voghan.pillar.common.AuthUtil;
import com.voghan.pillar.common.emails.EmailUtil;
import com.voghan.pillar.common.emails.SimpleEmailService;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Map;

import static com.voghan.pillar.core.jobs.BulkWorkflowJobConsumer.JOB_TOPIC;


@Component(service = JobConsumer.class, immediate = true, property = {
        Constants.SERVICE_DESCRIPTION + "= Bulk Workflow Sling Job",
        JobConsumer.PROPERTY_TOPICS + "=" + JOB_TOPIC
})
public class BulkWorkflowJobConsumer implements JobConsumer{
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkWorkflowJobConsumer.class);
    private static final String TEMPLATE = "/conf/pillar/notifications/email/bulk-workflow-completed.html";
    protected static final String SERVICE_NAME = "BulkWorkflowJobConsumer";
    public static final String JOB_TOPIC = "com/pillar/jobs/bulk-workflow";
    public static final String CONTENT_PATH = "PATH";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private SimpleEmailService simpleEmailService;

    @Reference
    private Externalizer externalizer;

    @Override
    public JobResult process(Job job) {
        final Map<String, Object> authInfo = AuthUtil.getAuthInfo(SERVICE_NAME);
        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authInfo)) {
            String path = job.getProperty(CONTENT_PATH, String.class);
            LOGGER.info("Starting job for {}", path);
            Resource resource = resourceResolver.getResource(path);
            if(resource == null) {
                LOGGER.warn("Resource not found: {}", path);
                return JobResult.CANCEL;
            }

            logStatus(resource);
            if (resource.getName().equals("workspace")) {
                ValueMap valueMap = resource.getValueMap();
                String status = valueMap.get("status", String.class);
                if ("COMPLETED".equals(status)) {
                    notifyBatchCompleted(resource, resourceResolver);
                }
            }

        } catch (LoginException  e) {
            LOGGER.warn(e.getMessage(), e);
            return JobResult.CANCEL;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return JobResult.CANCEL;
        }
        return JobResult.OK;
    }

    private void notifyBatchCompleted(Resource resource, ResourceResolver resourceResolver) throws RepositoryException {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if(pageManager == null) {
            LOGGER.warn("Unable to get PageManager");
            return;
        }

        Page page = pageManager.getContainingPage(resource);
        if (page == null) {
            LOGGER.info("Unable to get Page containing {}", resource.getPath());
            return;
        }

        String userId = page.getLastModifiedBy();
        String mailTo = EmailUtil.getUserEmail(resourceResolver, userId);
        if (mailTo == null) {
            LOGGER.info("No email found for user {}, skipping notification", userId);
            return;
        }

        Map<String, String> params = buildEmailParameters(page, resource, userId);
        params.put("authorLink", EmailUtil.getAuthorLink(resourceResolver, externalizer, page.getPath()));

        simpleEmailService.sendEmail(mailTo, TEMPLATE, params);

    }

    private static @NotNull Map<String, String> buildEmailParameters(Page page, Resource resource, String userId) {
        Map<String,String> params = new HashMap<>();
        ValueMap workflowProperties = resource.getValueMap();
        ValueMap pageProperties = page.getProperties();

        Long totalCount = workflowProperties.get("totalCount", Long.class);
        Long completeCount = workflowProperties.get("completeCount", Long.class);
        Long failedCount = workflowProperties.get("failedCount", Long.class);

        params.put("userId", userId);
        params.put("pageTitle", page.getTitle());
        params.put("totalCount", totalCount != null ? totalCount.toString() : "error");
        params.put("completeCount", completeCount != null ? completeCount.toString() : "0");
        params.put("failedCount", failedCount != null ? failedCount.toString() : "0");
        params.put("workflowModel",pageProperties.get("workflowModel", String.class));

        return params;
    }

    private void logStatus(Resource resource) {
        ValueMap valueMap = resource.getValueMap();
        String status = valueMap.get("status", String.class);
        Long totalCount = valueMap.get("totalCount", Long.class);
        Long completeCount = valueMap.get("completeCount", Long.class);
        String workflowId = valueMap.get("workflowInstanceId", String.class);

        if (totalCount != null && completeCount != null) {
            LOGGER.debug("Job completed {} of {} workflows", completeCount, totalCount);
        } else if (workflowId != null) {
            LOGGER.debug("{} workflow {}", status, workflowId);
        }
    }

}
