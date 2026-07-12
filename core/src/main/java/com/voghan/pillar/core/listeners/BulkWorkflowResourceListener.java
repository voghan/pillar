package com.voghan.pillar.core.listeners;

import com.voghan.pillar.core.jobs.BulkWorkflowJobConsumer;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = ResourceChangeListener.class, property = {
        ResourceChangeListener.PATHS + "=" + "/etc/acs-commons/bulk-workflow-manager",
        ResourceChangeListener.CHANGES + "=" + "CHANGED",
})
@ServiceDescription("Listen for changes in bulk workflow manager page content")
public class BulkWorkflowResourceListener implements ResourceChangeListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkWorkflowResourceListener.class);

    @Reference
    private JobManager jobManager;

    @Override
    public void onChange(List<ResourceChange> changes) {
        changes.forEach(change -> {
            if (ResourceChange.ChangeType.CHANGED.equals(change.getType())) {
                if (change.getPath().endsWith("/jcr:content/workspace")) {
                    LOGGER.info("Resource event: {} at: {}", change.getType(), change.getPath());
                    Map<String, Object> params = new HashMap<>();
                    params.put(BulkWorkflowJobConsumer.CONTENT_PATH, change.getPath());
                    Job job = jobManager.addJob(BulkWorkflowJobConsumer.JOB_TOPIC, params);
                    if (job == null) {
                        LOGGER.warn("Failed to create job for {}", change.getPath());
                    }
                }
            }
        });
    }
}
