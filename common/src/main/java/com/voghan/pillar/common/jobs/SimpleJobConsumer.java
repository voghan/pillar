package com.voghan.pillar.common.jobs;

import com.day.cq.commons.jcr.JcrConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

import static com.voghan.pillar.common.jobs.SimpleJobConsumer.JOB_TOPIC;

@Component(service = JobConsumer.class, immediate = true, property = {
    Constants.SERVICE_DESCRIPTION + "= Simple Sling Job",
    JobConsumer.PROPERTY_TOPICS + "=" + JOB_TOPIC
})
public class SimpleJobConsumer implements JobConsumer {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    protected static final String SERVICE_NAME = "pillar-simple-job-consumer";
    public static final String JOB_TOPIC = "simple/sling/job";
    public static final String JOB_PATH = "job_path";
    public static final String JOB_TYPE = "job_type";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public JobResult process(Job job) {
        LOGGER.info("Starting new simple job");

        final Map<String, Object> authInfo = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, SERVICE_NAME);
        try(ResourceResolver resourceResolver = resourceResolverFactory.getResourceResolver(authInfo)) {
            logJobParams(resourceResolver, job);
        } catch (LoginException e) {
            LOGGER.warn(e.getMessage(), e);
        }

        return JobResult.OK;
    }

    protected void logJobParams(ResourceResolver resourceResolver, Job job) {
        String path = job.getProperty(JOB_PATH, String.class);
        String type = job.getProperty(JOB_TYPE, String.class);
        Resource resource = resourceResolver.getResource(path);
        if (resource != null) {
             ValueMap valueMap = resource.getValueMap();
             String modifiedBy = valueMap.get(JcrConstants.JCR_LAST_MODIFIED_BY, String.class);
            LOGGER.info("Resource {} modified {} by {}", path, type, modifiedBy);
        } else {
            LOGGER.info("Resource is not found {}", path);
        }
    }

}
