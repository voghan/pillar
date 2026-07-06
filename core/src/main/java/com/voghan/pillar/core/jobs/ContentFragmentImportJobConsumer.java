package com.voghan.pillar.core.jobs;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.ContentFragmentException;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.voghan.pillar.common.AuthUtil;
import com.voghan.pillar.core.models.cfm.ArticleDetailCfm;
import com.voghan.pillar.core.models.cfm.CardCfm;
import com.voghan.pillar.core.models.cfm.CardListConfigCfm;
import com.voghan.pillar.core.models.cfm.FeaturedCardCfm;
import com.voghan.pillar.core.models.cfm.HeroCardCfm;
import com.voghan.pillar.core.models.cfm.LinkCfm;
import com.voghan.pillar.core.models.cfm.SimpleCardCfm;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static com.voghan.pillar.core.jobs.ContentFragmentImportJobConsumer.JOB_TOPIC;

@Component(service = JobConsumer.class, immediate = true, property = {
        Constants.SERVICE_DESCRIPTION + "= Content Fragment Model Import Sling Job",
        JobConsumer.PROPERTY_TOPICS + "=" + JOB_TOPIC
})
public class ContentFragmentImportJobConsumer implements JobConsumer {
    public static final String IMPORT_DATA = "IMPORT_DATA";
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentFragmentImportJobConsumer.class);

    private static final Gson GSON = new Gson();
    protected static final String SERVICE_NAME = "ContentFragmentImportJobConsumer";
    public static final String JOB_TOPIC = "com/pillar/jobs/cfm-import";
    public static final String DEFAULT_LINK_PATH = "/content/dam/pillar/cfm/links/imports";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public JobResult process(Job job) {

        final Map<String, Object> authInfo = AuthUtil.getAuthInfo(SERVICE_NAME);
        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authInfo)) {
            String jsonData = job.getProperty(IMPORT_DATA, String.class);

            JsonObject payload = GSON.fromJson(jsonData, JsonObject.class);
            if (!payload.has("content") || !payload.get("content").isJsonObject()){
                return JobResult.CANCEL;
            }
            JsonObject content = payload.getAsJsonObject("content");
            ContentFragment contentFragment = createContentFragment(content, resourceResolver);
            if (contentFragment == null) {
                LOGGER.warn("Unable to create content fragment for job {}", job.getId());
                return JobResult.FAILED;
            }

            addModelData(resourceResolver, contentFragment, content);

            resourceResolver.commit();

        } catch (LoginException | ContentFragmentException | PersistenceException e) {
            LOGGER.warn(e.getMessage(), e);
            return JobResult.CANCEL;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return JobResult.CANCEL;
        }

        return JobResult.OK;
    }

    private void addModelData(ResourceResolver resourceResolver, ContentFragment contentFragment, JsonObject payload) {
        String model = payload.get("model").getAsString();

        if (ArticleDetailCfm.MODEL.equals(model)) {
            addArticleDetailProperties(contentFragment, payload);
        } else if (CardCfm.MODEL.equals(model)) {
            addCardProperties(resourceResolver, contentFragment, payload);
        } else if (SimpleCardCfm.MODEL.equals(model)) {
            addSimpleCardProperties(resourceResolver, contentFragment, payload);
        } else if (HeroCardCfm.MODEL.equals(model)) {
            addHeroCardProperties(resourceResolver, contentFragment, payload);
        } else if (FeaturedCardCfm.MODEL.equals((model))) {
            addFeaturedCardProperties(resourceResolver, contentFragment, payload);
        } else if (CardListConfigCfm.MODEL.equals((model))) {
            addCardListProperties(resourceResolver, contentFragment, payload);
        }
    }

    private void addArticleDetailProperties(ContentFragment contentFragment, JsonObject payload) {
        JsonObject data = payload.getAsJsonObject("data");
        if (data.has("master") && data.get("master").isJsonObject()) {
            JsonObject master = data.getAsJsonObject("master");
            setTextProperty(contentFragment, master, "headline", "text/plain");
            setTextProperty(contentFragment, master, "url", "text/plain");
            setTextProperty(contentFragment, master, "subheadline", "text/plain");
            setTextProperty(contentFragment, master, "bannerImage", "text/plain");
            setDateProperty(contentFragment, master, "postDate");
            setTextProperty(contentFragment, master, "content", "text/html");
            setTextProperty(contentFragment, master, "shortDescription", "text/html");
        }
    }

    private void addCardProperties(ResourceResolver resourceResolver, ContentFragment contentFragment, JsonObject payload) {
        JsonObject data = payload.getAsJsonObject("data");
        if (data.has("master") && data.get("master").isJsonObject()) {
            JsonObject master = data.getAsJsonObject("master");
            setTextProperty(contentFragment, master, "headline", "text/plain");
            setTextProperty(contentFragment, master, "shortDescription", "text/html");
            setFragmentReferencesProperty(resourceResolver, contentFragment, master, "callToActions");
        }
    }

    private void addSimpleCardProperties(ResourceResolver resourceResolver, ContentFragment contentFragment, JsonObject payload) {
        JsonObject data = payload.getAsJsonObject("data");
        if (data.has("master") && data.get("master").isJsonObject()) {
            JsonObject master = data.getAsJsonObject("master");
            setTextProperty(contentFragment, master, "headline", "text/plain");
            setTextProperty(contentFragment, master, "shortDescription", "text/html");
            setTextProperty(contentFragment, master, "image", "text/plain");
            setFragmentReferencesProperty(resourceResolver, contentFragment, master, "callToActions");
        }
    }

    private void addHeroCardProperties(ResourceResolver resourceResolver, ContentFragment contentFragment, JsonObject payload) {
        JsonObject data = payload.getAsJsonObject("data");
        if (data.has("master") && data.get("master").isJsonObject()) {
            JsonObject master = data.getAsJsonObject("master");
            setTextProperty(contentFragment, master, "headline", "text/plain");
            setTextProperty(contentFragment, master, "shortDescription", "text/html");
            setTextProperty(contentFragment, master, "backgroundImage", "text/plain");
            setFragmentReferencesProperty(resourceResolver, contentFragment, master, "callToActions");
            setFragmentReferencesProperty(resourceResolver, contentFragment, master, "breadcrumbs");
        }
    }

    private void addFeaturedCardProperties(ResourceResolver resourceResolver, ContentFragment contentFragment, JsonObject payload) {
        JsonObject data = payload.getAsJsonObject("data");
        if (data.has("master") && data.get("master").isJsonObject()) {
            JsonObject master = data.getAsJsonObject("master");
            setTextProperty(contentFragment, master, "headline", "text/plain");
            setTextProperty(contentFragment, master, "subheadline", "text/plain");
            setTextProperty(contentFragment, master, "shortDescription", "text/html");
            setTextProperty(contentFragment, master, "image", "text/plain");
            setFragmentReferencesProperty(resourceResolver, contentFragment, master, "callToActions");
        }
    }

    private void addCardListProperties(ResourceResolver resourceResolver, ContentFragment contentFragment, JsonObject payload) {
        JsonObject data = payload.getAsJsonObject("data");
        if (data.has("master") && data.get("master").isJsonObject()) {
            JsonObject master = data.getAsJsonObject("master");
            setTextProperty(contentFragment, master, "headline", "text/plain");
            setTextProperty(contentFragment, master, "shortDescription", "text/html");
            setTextProperty(contentFragment, master, "searchPath", "text/plain");
            setTextProperty(contentFragment, master, "enableSearch", "text/plain");
            setTextProperty(contentFragment, master, "enablePostDate", "text/plain");
            setTagProperty(contentFragment, master, "filterTags");
            setTagProperty(contentFragment, master, "cardTags");
        }
    }

    private ContentFragment createContentFragment(JsonObject content, ResourceResolver resourceResolver) throws ContentFragmentException, PersistenceException {
        String model = content.get("model").getAsString();
        String path = content.get("path").getAsString();
        String title = content.get("title").getAsString();
        String name = content.get("name").getAsString();

        return createContentFragment(resourceResolver, model, path, name, title);
    }

    private @Nullable ContentFragment createContentFragment(ResourceResolver resourceResolver, String model, String path, String name, String title) throws ContentFragmentException, PersistenceException {
        Resource modelResource = resourceResolver.getResource(model);
        if (modelResource == null) {
            throw new ContentFragmentException("Model not found: " + model);
        }

        FragmentTemplate template = modelResource.adaptTo(FragmentTemplate.class);
        if (template == null) {
            throw new ContentFragmentException("Cannot adapt model to FragmentTemplate: " + model);
        }

        Resource folder = resourceResolver.getResource(path);
        if (folder == null) {
            throw new ContentFragmentException("Folder not found: " + path);
        }

        // Generate a unique node name from the title
        String nodeName = ResourceUtil.createUniqueChildName(folder, name);

        // Create the fragment
        ContentFragment contentFragment = template.createFragment(folder, nodeName, title);

        if (contentFragment != null && contentFragment.adaptTo(Resource.class) != null) {
            LOGGER.info("Created Content Fragment: {} ({})", title, contentFragment.adaptTo(Resource.class).getPath());
        }

        return contentFragment;
    }

    private List<ContentFragment> createSimpleLinks(ResourceResolver resourceResolver, JsonObject master, String key) {
        List<ContentFragment> links = new ArrayList<>();
        if (master.has(key) && master.get(key).isJsonArray()) {
            JsonArray jsonArray = master.getAsJsonArray(key);
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                try {
                    ContentFragment link = createContentFragment(resourceResolver, LinkCfm.MODEL, DEFAULT_LINK_PATH, "link", "link");
                    if (link != null) {
                        setTextProperty(link, jsonObject, "linkText", "text/plain");
                        setTextProperty(link, jsonObject, "linkPath", "text/plain");
                        links.add(link);
                    }
                } catch (ContentFragmentException | PersistenceException e) {
                    LOGGER.warn("Unable to create link for {}", key, e);
                }
            }
        }

        return links;
    }

    private FragmentData getFragmentData(ContentFragment contentFragment, String fieldName) throws ContentFragmentException {
        return contentFragment.getElement(fieldName).getValue();
    }

    private void setTextProperty(ContentFragment contentFragment, JsonObject master, String key, String type) {
        try {
            contentFragment.getElement(key).setContent(getProperty(master, key), type);
        } catch (ContentFragmentException e) {
            LOGGER.warn("Unable to set property for {}", key, e);
        }
    }

    private void setTagProperty(ContentFragment contentFragment, JsonObject master, String key) {
        try {
            List<String> links = new ArrayList<>();
            if (master.has(key) && master.get(key).isJsonArray()) {
                JsonArray jsonArray = master.getAsJsonArray(key);
                for (JsonElement element : jsonArray) {
                    String tagId = element.getAsString();
                    links.add(tagId);
                }
                if (links.isEmpty()) {
                    return;
                }

                String[] paths = links.toArray(new String[0]);
                FragmentData data = contentFragment.getElement(key).getValue();
                data.setValue(paths);
                contentFragment.getElement(key).setValue(data);
            }
        } catch (ContentFragmentException e) {
            LOGGER.warn("Unable to set property for {}", key, e);
        }
    }

    private void setDateProperty(ContentFragment contentFragment, JsonObject master, String key)  {
        try {
            FragmentData fragmentData = getFragmentData(contentFragment, key);
            fragmentData.setValue(getDateProperty(master, key));
            contentFragment.getElement(key).setValue(fragmentData);
        } catch (ContentFragmentException | ParseException e) {
            LOGGER.warn("Unable to parse date for {}", key, e);
        }
    }

    private void setFragmentReferencesProperty(ResourceResolver resourceResolver, ContentFragment contentFragment, JsonObject master, String key) {
        try {
            List<ContentFragment> references = createSimpleLinks(resourceResolver, master, key);

            if (references.isEmpty()) {
                return;
            }

            String[] paths = references.stream()
                    .filter(ref -> ref.adaptTo(Resource.class) != null)
                    .map(ref -> ref.adaptTo(Resource.class).getPath())
                    .toArray(String[]::new);

            FragmentData data = contentFragment.getElement(key).getValue();
            data.setValue(paths);
            contentFragment.getElement(key).setValue(data);
        } catch (ContentFragmentException  e) {
            LOGGER.warn("Unable to add references for {}", key, e);
        }
    }

    private static String getProperty(JsonObject object, String key) {
        return object.keySet().contains(key) && object.get(key).isJsonPrimitive() ? object.get(key).getAsString() : null;
    }

    private static Calendar getDateProperty(JsonObject object, String key) throws ParseException {
        Calendar calendar = null;
        String rawDate = getProperty(object, key);
        if (rawDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", java.util.Locale.ENGLISH);
            java.util.Date date = sdf.parse(rawDate);
            calendar = Calendar.getInstance();
            calendar.setTime(date);
        }
        return calendar;
    }

}
