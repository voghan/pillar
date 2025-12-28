/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.voghan.pillar.common.listeners;

import com.voghan.pillar.common.jobs.SimpleJobConsumer;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A service to demonstrate how changes in the resource tree
 * can be listened for.
 * Please note, that apart from EventHandler services,
 * the immediate flag should not be set on a service.
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
            addJob(change);
        });

    }

    private void addJob(ResourceChange change) {

        Map<String, Object> params = new HashMap<>();
        params.put(SimpleJobConsumer.JOB_PATH, change.getPath());
        params.put(SimpleJobConsumer.JOB_TYPE, change.getType());
        jobManager.addJob(SimpleJobConsumer.JOB_TOPIC, params);
    }
}

