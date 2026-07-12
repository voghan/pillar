package com.voghan.pillar.common.listeners;

import com.voghan.pillar.common.jobs.JobHelper;
import com.voghan.pillar.common.jobs.SimpleJobConsumer;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A service to demonstrate how changes in the resource tree can be listened for. Please note, that
 * apart from EventHandler services, the immediate flag should not be set on a service.
 */
@Component(service = ResourceChangeListener.class, property = {
    ResourceChangeListener.PATHS + "=" + "/content",
    ResourceChangeListener.CHANGES + "=" + "ADDED",
    ResourceChangeListener.CHANGES + "=" + "CHANGED",
    ResourceChangeListener.CHANGES + "=" + "REMOVED"
})
@ServiceDescription("Demo to listen on changes in the resource tree")
public class SimpleResourceListener implements ResourceChangeListener {

  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  @Reference
  private JobManager jobManager;

  @Override
  public void onChange(List<ResourceChange> changes) {

    changes.forEach(change -> {
      LOGGER.debug("Resource event: {} at: {} isExternal {}", change.getType(), change.getPath(), change.isExternal());
      ResourceChange.ChangeType type  = change.getType();
      String path = change.getPath();
      switch (type) {
        case ResourceChange.ChangeType.ADDED: {
          addJob(path, SimpleJobConsumer.JobType.ADDED.toString());
          break;
          }
        case ResourceChange.ChangeType.CHANGED: {
          addJob(path, SimpleJobConsumer.JobType.CHANGED.toString());
          break;
        }
        case ResourceChange.ChangeType.REMOVED: {
          addJob(path, SimpleJobConsumer.JobType.REMOVED.toString());
          break;
        }
        default: {
          LOGGER.warn("Change type skipped: {} - {}", type, path);
        }
      }
    });

  }

  private void addJob(String path, String type) {
    Job job = JobHelper.addSimpleJob(path, type, jobManager);
    if (job == null) {
      LOGGER.warn("Failed to create job for {} {}", path, type);
    }
  }
}

