package com.voghan.pillar.common.events;

import com.day.cq.wcm.api.PageEvent;
import com.day.cq.wcm.api.PageModification;
import com.voghan.pillar.common.jobs.JobHelper;
import com.voghan.pillar.common.jobs.SimpleJobConsumer;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

@Component(
        service = EventHandler.class,
        immediate = true,
        property = {
                EventConstants.EVENT_TOPIC + "=" + PageEvent.EVENT_TOPIC
        }
)
public class SimplePageEventHandler implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimplePageEventHandler.class);

    @Reference
    private JobManager jobManager;

    @Override
    public void handleEvent(Event event) {
        PageEvent pageEvent = PageEvent.fromEvent(event);

        Iterator<PageModification> modifications = pageEvent.getModifications();
        while (modifications.hasNext()) {
            PageModification mod = modifications.next();
            String path = mod.getPath();
            PageModification.ModificationType type = mod.getType();

            switch (type) {
                case CREATED: {
                    LOGGER.info("Page created: {}", path);
                    addJob(path, SimpleJobConsumer.JobType.ADDED.toString());
                    break;
                }
                case MODIFIED: {
                    LOGGER.info("Page modified: {}", path);
                    addJob(path, SimpleJobConsumer.JobType.CHANGED.toString());
                    break;
                }
                case DELETED: {
                    LOGGER.info("Page deleted: {}", path);
                    addJob(path, SimpleJobConsumer.JobType.REMOVED.toString());
                    break;
                }
                case MOVED: {
                    LOGGER.info("Page moved: {} -> {}", path, mod.getDestination());
                    addJob(path, SimpleJobConsumer.JobType.MOVED.toString());
                    break;
                }
                default: {
                    LOGGER.info("Page event {}: {}", type, path);
                    // No default action besides logging the event
                }
            }
        }

    }

    private void addJob(String path, String type) {
        // Update location on index
        Job job = JobHelper.addSimpleJob(path, type, jobManager);
        if (job == null) {
            LOGGER.warn("Failed to create job for {} {}", path, type);
        }
    }

}
