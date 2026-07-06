package com.voghan.pillar.core.jobs;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.voghan.pillar.common.AuthUtil;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public static final String JOB_TOPIC = "com/pillar/jobs/page-import";
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
            Page page = createPage(payload, pageManager);
            if (page == null) {
                LOGGER.warn("Unable to create page for job {}", job.getId());
                return JobResult.FAILED;
            }

            JsonObject pageBody = payload.getAsJsonObject("body");
            addComponents(page, pageBody, resourceResolver);

            // Save changes
            resourceResolver.commit();
        } catch (LoginException | WCMException | PersistenceException e) {
            LOGGER.warn(e.getMessage(), e);
            return JobResult.CANCEL;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return JobResult.CANCEL;
        }

        return JobResult.OK;
    }

    private Page createPage(JsonObject payload, PageManager pageManager) throws WCMException {
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
        for (String key : pageBody.keySet()) {
            if (!pageBody.get(key).isJsonObject()) {
                // Skipping all members that are not json objects
                continue;
            }

            if (key.startsWith("hero-cards-") || key.startsWith("simple-card-") || key.startsWith("card-list-") || key.startsWith("featured-")) {
                JsonObject cmp = pageBody.getAsJsonObject(key);
                if(!cmp.has("sling:resourceType")) {
                    LOGGER.warn("Missing required field for component");
                    continue;
                }
                addSingleComponent(resourceResolver, containerPath, key, cmp);
            } else if (key.startsWith("image-")) {
                JsonObject cmp = pageBody.getAsJsonObject(key);
                addImageComponent(resourceResolver, containerPath, key, cmp);
            } else if (key.startsWith("separator-")) {
                JsonObject cmp = pageBody.getAsJsonObject(key);
                addSeparatorComponent(resourceResolver, containerPath, key, cmp);
            } else if (key.startsWith("text-")) {
                JsonObject cmp = pageBody.getAsJsonObject(key);
                addTextComponent(resourceResolver, containerPath, key, cmp);
            } else if (key.startsWith("articledetail-")) {
                JsonObject cmp = pageBody.getAsJsonObject(key);
                addArticleDetail(resourceResolver, page.getPath(), cmp);
            }
        }

    }

    protected void addSingleComponent(ResourceResolver resourceResolver, String containerPath, String componentNodeName, JsonObject cmp)
            throws PersistenceException {

        Resource containerResource = resourceResolver.getResource(containerPath);
        if (containerResource != null) {
            // Define the properties for the new component
            Map<String, Object> componentProps = new HashMap<>();
            componentProps.put("jcr:primaryType", "nt:unstructured");
            componentProps.put("sling:resourceType", getProperty(cmp, "sling:resourceType"));
            componentProps.put("variationName", getProperty(cmp, "variationName"));
            componentProps.put("fragmentPath", getProperty(cmp, "fragmentPath"));

            // Create a uniquely named node under the container (e.g., text_123)
            resourceResolver.create(containerResource, componentNodeName, componentProps);
        } else {
            LOGGER.warn("Missing container resource, skipping component");
        }
    }

    protected void addImageComponent(ResourceResolver resourceResolver, String containerPath, String componentNodeName, JsonObject cmp)
            throws PersistenceException {

        Resource containerResource = resourceResolver.getResource(containerPath);
        if (containerResource != null) {
            // Define the properties for the new component
            Map<String, Object> componentProps = new HashMap<>();
            componentProps.put("jcr:primaryType", "nt:unstructured");
            componentProps.put("sling:resourceType", getProperty(cmp, "sling:resourceType"));
            componentProps.put("fileReference", getProperty(cmp, "fileReference"));
            componentProps.put("imageFromPageImage", getProperty(cmp, "imageFromPageImage"));
            componentProps.put("alt", getProperty(cmp, "alt"));
            componentProps.put("isDecorative", getProperty(cmp, "isDecorative"));

            // Create a uniquely named node under the container (e.g., text_123)
            resourceResolver.create(containerResource, componentNodeName, componentProps);
        } else {
            LOGGER.warn("Missing container resource, skipping component");
        }
    }

    protected void addSeparatorComponent(ResourceResolver resourceResolver, String containerPath, String componentNodeName, JsonObject cmp)
            throws PersistenceException {

        Resource containerResource = resourceResolver.getResource(containerPath);
        if (containerResource != null) {
            // Define the properties for the new component
            Map<String, Object> componentProps = new HashMap<>();
            componentProps.put("jcr:primaryType", "nt:unstructured");
            componentProps.put("sling:resourceType", getProperty(cmp, "sling:resourceType"));
            componentProps.put("isDecorative", getProperty(cmp, "isDecorative"));

            // Create a uniquely named node under the container (e.g., text_123)
            Resource resource = resourceResolver.create(containerResource, componentNodeName, componentProps);

            // Add styles cq:styleIds
            addStyleIds(resourceResolver, resource.getPath(), cmp);

        } else {
            LOGGER.warn("Missing container resource, skipping component");
        }
    }

    protected void addTextComponent(ResourceResolver resourceResolver, String containerPath, String componentNodeName, JsonObject cmp)
            throws PersistenceException {

        Resource containerResource = resourceResolver.getResource(containerPath);
        if (containerResource != null) {
            // Define the properties for the new component
            Map<String, Object> componentProps = new HashMap<>();
            componentProps.put("jcr:primaryType", "nt:unstructured");
            componentProps.put("sling:resourceType", getProperty(cmp, "sling:resourceType"));
            componentProps.put("text", getProperty(cmp, "text"));
            componentProps.put("textIsRich", getProperty(cmp, "textIsRich"));

            // Create a uniquely named node under the container (e.g., text_123)
            resourceResolver.create(containerResource, componentNodeName, componentProps);
        } else {
            LOGGER.warn("Missing container resource, skipping component");
        }
    }

    protected void addArticleDetail(ResourceResolver resourceResolver, String pagePath, JsonObject cmp) throws PersistenceException {
        String componentPath = pagePath + "/jcr:content/root/container";
        Resource containerResource = resourceResolver.getResource(componentPath);
        if (containerResource != null) {
            // Only allow a single articledetail node
            if (containerResource.getChild("articledetail") == null) {
                // Define the properties for the new component
                Map<String, Object> componentProps = new HashMap<>();
                componentProps.put("jcr:primaryType", "nt:unstructured");
                componentProps.put("sling:resourceType", getProperty(cmp, "sling:resourceType"));
                componentProps.put("fragmentPath", getProperty(cmp, "fragmentPath"));

                //
                resourceResolver.create(containerResource, "articledetail", componentProps);
            }

        } else {
            LOGGER.warn("Missing container resource, skipping component");
        }
    }

    protected void addStyleIds(ResourceResolver resourceResolver, String containerPath, JsonObject cmp) {
        Resource resource = resourceResolver.getResource(containerPath);
        if (resource == null) {
            return;
        }
        JsonArray array = getPropertyArray(cmp, "cq:styleIds");
        if (array == null || array.isEmpty()) {
            return;
        }

        List<String> styleIds = new ArrayList<>();
        for (JsonElement element : array) {
            if (element.isJsonPrimitive()) {
                styleIds.add(element.getAsString());
            }
        }

        ModifiableValueMap valueMap = resource.adaptTo(ModifiableValueMap.class);
        if (valueMap != null) {
            valueMap.put("cq:styleIds", styleIds.toArray(String[]::new));
        }
    }

    private static String getProperty(JsonObject cmp, String key) {
        return cmp.keySet().contains(key) && cmp.get(key).isJsonPrimitive() ? cmp.get(key).getAsString() : null;
    }

    private static JsonArray getPropertyArray(JsonObject cmp, String key) {
        return cmp.keySet().contains(key) && cmp.get(key).isJsonArray() ? cmp.get(key).getAsJsonArray() : null;
    }
}
