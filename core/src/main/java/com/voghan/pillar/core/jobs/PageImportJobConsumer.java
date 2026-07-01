package com.voghan.pillar.core.jobs;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.voghan.pillar.common.AuthUtil;
import org.apache.sling.api.resource.*;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.voghan.pillar.core.jobs.PageImportJobConsumer.JOB_TOPIC;


@Component(service = JobConsumer.class, immediate = true, property = {
        Constants.SERVICE_DESCRIPTION + "= Page Import Sling Job",
        JobConsumer.PROPERTY_TOPICS + "=" + JOB_TOPIC
})
public class PageImportJobConsumer implements JobConsumer {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final Gson GSON = new Gson();
    protected static final String SERVICE_NAME = "PageImportJobConsumer";
    public static final String JOB_TOPIC = "pillar/jobs/import/page";
    public static final String PAGE_DATA = "page_data";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public JobResult process(Job job) {
        LOGGER.info("Starting new page import job");

        final Map<String, Object> authInfo = AuthUtil.getAuthInfo(SERVICE_NAME);
        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authInfo)) {
            String pageData = job.getProperty(PAGE_DATA, String.class);

            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            if (pageManager == null) {
                LOGGER.warn("Unable to get a PageManager for job  {}", job.getId());
                return JobResult.CANCEL;
            }

            JsonObject payload = GSON.fromJson(pageData, JsonObject.class);
            Page page = createPage(payload, resourceResolver, pageManager);
            if (page == null) {
                LOGGER.warn("Unable to create page for job {}", job.getId());
                return JobResult.FAILED;
            }

            JsonObject pageBody = payload.getAsJsonObject("body");
            addComponents(page, pageBody, resourceResolver);

            // Save changes
            resourceResolver.commit();

        } catch (WCMException | PersistenceException e) {
            LOGGER.warn(e.getMessage(), e);
            return JobResult.FAILED;
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return JobResult.CANCEL;
        }

        return JobResult.OK;
    }

    private Page createPage(JsonObject payload, ResourceResolver resourceResolver, PageManager pageManager) throws WCMException, PersistenceException {
        JsonObject pageJson = payload.getAsJsonObject("page");
        String title = pageJson.get("title").getAsString();
        String name = pageJson.get("name").getAsString();
        String template = pageJson.get("template").getAsString();
        String description = pageJson.get("description").getAsString();
        String path = pageJson.get("path").getAsString();

        LOGGER.info("Importing page name={} title={} template={} path={}", name, title, template, path);

        Page page = pageManager.create(path, name, template, title);
        if (page != null) {
            Resource resource = page.getContentResource();
            ModifiableValueMap valueMap = resource.adaptTo(ModifiableValueMap.class);
            if (valueMap != null) {
                valueMap.put("jcr:description", description);
            }
        }

        return page;
    }

    private void addComponents(Page page, JsonObject pageBody, ResourceResolver resourceResolver) throws PersistenceException {
        String resourceType = pageBody.get("sling:resourceType").getAsString();
        if (!"pillar/components/container/v1/container".equals(resourceType)) {
            LOGGER.warn("Missing container resource type");
            return;
        }

        String containerPath = page.getPath() + "/jcr:content/root/container/container";
        LOGGER.info("PageBody keys {}", pageBody.keySet());
        for (String key: pageBody.keySet()) {
            if (key.startsWith("hero-cards-")) {
                JsonObject cmp = pageBody.getAsJsonObject(key);
                String cmpResourceType = cmp.get("sling:resourceType").getAsString();
                String variationName = cmp.get("variationName").getAsString();
                String fragmentPath = cmp.get("fragmentPath").getAsString();
                addSingleComponent(resourceResolver, containerPath, key, cmpResourceType, variationName, fragmentPath);
            } else if (key.startsWith("simple-card-")) {
                JsonObject cmp = pageBody.getAsJsonObject(key);
                String cmpResourceType = cmp.get("sling:resourceType").getAsString();
                String variationName = cmp.get("variationName").getAsString();
                String fragmentPath = cmp.get("fragmentPath").getAsString();
                addSingleComponent(resourceResolver, containerPath, key, cmpResourceType, variationName, fragmentPath);
            }
        }

    }

    protected void addSingleComponent(ResourceResolver resourceResolver, String containerPath, String componentNodeName,
                                      String resourceType, String variationName, String fragmentPath)
            throws PersistenceException {
        Resource containerResource = resourceResolver.getResource(containerPath);

        if (containerResource != null) {
            // Define the properties for the new component
            Map<String, Object> componentProps = new HashMap<>();
            componentProps.put("jcr:primaryType", "nt:unstructured");
            componentProps.put("sling:resourceType", resourceType);
            componentProps.put("variationName", variationName);
            componentProps.put("fragmentPath", fragmentPath);

            // Create a uniquely named node under the container (e.g., text_123)
            resourceResolver.create(containerResource, componentNodeName, componentProps);
        } else {
            LOGGER.warn("Missing container resource, skipping component");
        }
    }
}
