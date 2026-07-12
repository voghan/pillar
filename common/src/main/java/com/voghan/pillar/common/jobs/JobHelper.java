package com.voghan.pillar.common.jobs;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;

import java.util.HashMap;
import java.util.Map;

public class JobHelper {
    public static Job addSimpleJob(String path, String type, JobManager jobManager) {
        Map<String, Object> params = new HashMap<>();
        params.put(SimpleJobConsumer.JOB_PATH, path);
        params.put(SimpleJobConsumer.JOB_TYPE, type);
        return jobManager.addJob(SimpleJobConsumer.JOB_TOPIC, params);
    }
}
