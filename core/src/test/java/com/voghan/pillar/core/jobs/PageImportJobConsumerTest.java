package com.voghan.pillar.core.jobs;


import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.*;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class PageImportJobConsumerTest {

    private static final String CONTAINER_TYPE = "pillar/components/container/v1/container";
    private static final String PAGE_PATH = "/content/import/home";
    private static final String CONTAINER_PATH = PAGE_PATH + "/jcr:content/root/container/container";

    private static final String VALID_PAGE =
            "\"page\":{\"title\":\"Home\",\"name\":\"home\",\"template\":\"simple\","
                    + "\"description\":\"desc\",\"path\":\"/content/import\"}";

    @InjectMocks
    private PageImportJobConsumer fixture;

    @Mock
    ResourceResolverFactory resourceResolverFactory;

    @Mock
    ResourceResolver resourceResolver;

    @Mock
    PageManager pageManager;

    @Test
    void process_returnsOK() throws LoginException, WCMException, PersistenceException {
        stubResolverAndPageManager();
        stubCreatedPage(createdPage());

        JobConsumer.JobResult result = fixture.process(buildJob(payload(containerBody())));

        assertEquals(JobConsumer.JobResult.OK, result);
        verify(resourceResolver, times(1)).commit();
    }

    @Test
    void process_setsDescriptionOnContentResource() throws LoginException, WCMException {
        stubResolverAndPageManager();
        Page page = createdPage();
        ModifiableValueMap valueMap = stubCreatedPage(page);

        fixture.process(buildJob(payload(containerBody())));

        verify(valueMap, times(1)).put("jcr:description", "desc");
    }

    @Test
    void process_createsComponentsUnderContainer() throws LoginException, WCMException, PersistenceException {
        stubResolverAndPageManager();
        stubCreatedPage(createdPage());
        Resource container = mock(Resource.class);
        when(resourceResolver.getResource(CONTAINER_PATH)).thenReturn(container);

        String body = "\"body\":{"
                + "\"sling:resourceType\":\"" + CONTAINER_TYPE + "\","
                + "\"hero-cards-1\":{\"sling:resourceType\":\"pillar/components/hero/v1/hero\","
                + "\"variationName\":\"master\",\"fragmentPath\":\"/content/dam/hero\"},"
                + "\"simple-card-1\":{\"sling:resourceType\":\"pillar/components/card/v1/card\","
                + "\"variationName\":\"master\",\"fragmentPath\":\"/content/dam/card\"}"
                + "}";

        JobConsumer.JobResult result = fixture.process(buildJob(payload(body)));

        assertEquals(JobConsumer.JobResult.OK, result);
        // A node is created for each hero-cards-*/simple-card-* key.
        verify(resourceResolver).create(eq(container), eq("simple-card-1"), anyMap());

        ArgumentCaptor<Map<String, Object>> props = ArgumentCaptor.forClass(Map.class);
        verify(resourceResolver).create(eq(container), eq("hero-cards-1"), props.capture());
        assertEquals("pillar/components/hero/v1/hero", props.getValue().get("sling:resourceType"));
        assertEquals("master", props.getValue().get("variationName"));
        assertEquals("/content/dam/hero", props.getValue().get("fragmentPath"));
        assertEquals("nt:unstructured", props.getValue().get("jcr:primaryType"));
    }

    @Test
    void process_missingContainerResource_skipsComponentButReturnsOK() throws LoginException, WCMException, PersistenceException {
        stubResolverAndPageManager();
        stubCreatedPage(createdPage());
        when(resourceResolver.getResource(CONTAINER_PATH)).thenReturn(null);

        String body = "\"body\":{\"sling:resourceType\":\"" + CONTAINER_TYPE + "\","
                + "\"hero-cards-1\":{\"sling:resourceType\":\"x\",\"variationName\":\"master\",\"fragmentPath\":\"/p\"}}";

        JobConsumer.JobResult result = fixture.process(buildJob(payload(body)));

        assertEquals(JobConsumer.JobResult.OK, result);
        verify(resourceResolver, never()).create(any(), anyString(), anyMap());
    }

    @Test
    void process_nonContainerBody_skipsComponents() throws LoginException, WCMException {
        stubResolverAndPageManager();
        // addComponents returns before page.getPath() is used, so don't stub it here.
        Page page = createdPage();
        when(pageManager.create(anyString(), anyString(), anyString(), anyString())).thenReturn(page);
        Resource content = mock(Resource.class);
        when(page.getContentResource()).thenReturn(content);
        when(content.adaptTo(ModifiableValueMap.class)).thenReturn(mock(ModifiableValueMap.class));

        String body = "\"body\":{\"sling:resourceType\":\"pillar/components/other\","
                + "\"hero-cards-1\":{\"sling:resourceType\":\"x\",\"variationName\":\"m\",\"fragmentPath\":\"/p\"}}";

        JobConsumer.JobResult result = fixture.process(buildJob(payload(body)));

        assertEquals(JobConsumer.JobResult.OK, result);
        // Early return before resolving the container path.
        verify(resourceResolver, never()).getResource(anyString());
    }

    @Test
    void process_nullValueMap_returnsOK() throws LoginException, WCMException {
        stubResolverAndPageManager();
        Page page = createdPage();
        when(pageManager.create(anyString(), anyString(), anyString(), anyString())).thenReturn(page);
        Resource content = mock(Resource.class);
        when(page.getContentResource()).thenReturn(content);
        when(content.adaptTo(ModifiableValueMap.class)).thenReturn(null);
        when(page.getPath()).thenReturn(PAGE_PATH);

        JobConsumer.JobResult result = fixture.process(buildJob(payload(containerBody())));

        assertEquals(JobConsumer.JobResult.OK, result);
    }

    @Test
    void process_missingPageManager_returnsCancel() throws LoginException {
        when(resourceResolverFactory.getServiceResourceResolver(anyMap())).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(null);

        JobConsumer.JobResult result = fixture.process(buildJob(payload(containerBody())));

        assertEquals(JobConsumer.JobResult.CANCEL, result);
    }

    @Test
    void process_pageCreateReturnsNull_returnsFailed() throws LoginException, WCMException {
        stubResolverAndPageManager();
        when(pageManager.create(anyString(), anyString(), anyString(), anyString())).thenReturn(null);

        JobConsumer.JobResult result = fixture.process(buildJob(payload(containerBody())));

        assertEquals(JobConsumer.JobResult.FAILED, result);
    }

    @Test
    void process_wcmException_returnsFailed() throws LoginException, WCMException {
        stubResolverAndPageManager();
        when(pageManager.create(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new WCMException("create failed"));

        JobConsumer.JobResult result = fixture.process(buildJob(payload(containerBody())));

        assertEquals(JobConsumer.JobResult.CANCEL, result);
    }

    @Test
    void process_persistenceException_returnsFailed() throws LoginException, WCMException, PersistenceException {
        stubResolverAndPageManager();
        stubCreatedPage(createdPage());
        doThrow(new PersistenceException("commit failed")).when(resourceResolver).commit();

        JobConsumer.JobResult result = fixture.process(buildJob(payload(containerBody())));

        assertEquals(JobConsumer.JobResult.CANCEL, result);
    }

    @Test
    void process_malformedJson_returnsCancel() throws LoginException {
        stubResolverAndPageManager();

        JobConsumer.JobResult result = fixture.process(buildJob("{ not json"));

        assertEquals(JobConsumer.JobResult.CANCEL, result);
    }

    @Test
    void process_missingRequiredField_returnsCancel() throws LoginException {
        stubResolverAndPageManager();

        // "page" present but the required "title" is absent -> NPE, caught as CANCEL.
        String pageData = "{\"page\":{\"name\":\"home\"},\"body\":{}}";

        JobConsumer.JobResult result = fixture.process(buildJob(pageData));

        assertEquals(JobConsumer.JobResult.CANCEL, result);
    }

    @Test
    void process_loginException_returnsCancel() throws LoginException {
        when(resourceResolverFactory.getServiceResourceResolver(anyMap()))
                .thenThrow(new LoginException("no service user"));

        // getProperty is never reached, so use a bare job mock (no stubbing).
        JobConsumer.JobResult result = fixture.process(mock(Job.class));

        assertEquals(JobConsumer.JobResult.CANCEL, result);
    }

    @Test
    void process_createsComponentsForAllSupportedPrefixes() throws LoginException, WCMException, PersistenceException {
        stubResolverAndPageManager();
        stubCreatedPage(createdPage());
        Resource container = mock(Resource.class);
        when(resourceResolver.getResource(CONTAINER_PATH)).thenReturn(container);

        String body = "\"body\":{\"sling:resourceType\":\"" + CONTAINER_TYPE + "\","
                + "\"card-list-1\":{\"sling:resourceType\":\"pillar/components/cardlist/v1/cardlist\","
                + "\"variationName\":\"master\",\"fragmentPath\":\"/content/dam/cl\"},"
                + "\"featured-1\":{\"sling:resourceType\":\"pillar/components/featured/v1/featured\","
                + "\"variationName\":\"master\",\"fragmentPath\":\"/content/dam/f\"}}";

        JobConsumer.JobResult result = fixture.process(buildJob(payload(body)));

        assertEquals(JobConsumer.JobResult.OK, result);
        verify(resourceResolver).create(eq(container), eq("card-list-1"), anyMap());
        verify(resourceResolver).create(eq(container), eq("featured-1"), anyMap());
    }

    @Test
    void process_componentMissingResourceType_isSkipped() throws LoginException, WCMException, PersistenceException {
        stubResolverAndPageManager();
        stubCreatedPage(createdPage());
        Resource container = mock(Resource.class);
        when(resourceResolver.getResource(CONTAINER_PATH)).thenReturn(container);

        // hero-cards-1 lacks sling:resourceType -> skipped; simple-card-1 is valid -> created.
        String body = "\"body\":{\"sling:resourceType\":\"" + CONTAINER_TYPE + "\","
                + "\"hero-cards-1\":{\"variationName\":\"master\",\"fragmentPath\":\"/p\"},"
                + "\"simple-card-1\":{\"sling:resourceType\":\"pillar/components/card/v1/card\","
                + "\"variationName\":\"master\",\"fragmentPath\":\"/content/dam/card\"}}";

        JobConsumer.JobResult result = fixture.process(buildJob(payload(body)));

        assertEquals(JobConsumer.JobResult.OK, result);
        verify(resourceResolver).create(eq(container), eq("simple-card-1"), anyMap());
        verify(resourceResolver, never()).create(any(), eq("hero-cards-1"), anyMap());
    }

    @Test
    void process_optionalFieldsAbsent_storedAsNull() throws LoginException, WCMException, PersistenceException {
        stubResolverAndPageManager();
        stubCreatedPage(createdPage());
        Resource container = mock(Resource.class);
        when(resourceResolver.getResource(CONTAINER_PATH)).thenReturn(container);

        // Only the required sling:resourceType is present; variationName/fragmentPath are absent.
        String body = "\"body\":{\"sling:resourceType\":\"" + CONTAINER_TYPE + "\","
                + "\"featured-1\":{\"sling:resourceType\":\"pillar/components/featured/v1/featured\"}}";

        JobConsumer.JobResult result = fixture.process(buildJob(payload(body)));

        assertEquals(JobConsumer.JobResult.OK, result);
        ArgumentCaptor<Map<String, Object>> props = ArgumentCaptor.forClass(Map.class);
        verify(resourceResolver).create(eq(container), eq("featured-1"), props.capture());
        assertEquals("pillar/components/featured/v1/featured", props.getValue().get("sling:resourceType"));
        assertEquals("nt:unstructured", props.getValue().get("jcr:primaryType"));
        assertNull(props.getValue().get("variationName"));
        assertNull(props.getValue().get("fragmentPath"));
    }

    @Test
    void process_imageComponent_createsNodeWithImageProps() throws LoginException, WCMException, PersistenceException {
        stubResolverAndPageManager();
        stubCreatedPage(createdPage());
        Resource container = mock(Resource.class);
        when(resourceResolver.getResource(CONTAINER_PATH)).thenReturn(container);

        // "alt" is a JSON object (non-primitive) -> getProperty returns null for it.
        String body = "\"body\":{\"sling:resourceType\":\"" + CONTAINER_TYPE + "\","
                + "\"image-1\":{\"sling:resourceType\":\"pillar/components/image/v1/image\","
                + "\"fileReference\":\"/content/dam/img.png\",\"imageFromPageImage\":\"false\","
                + "\"alt\":{\"nested\":1},\"isDecorative\":\"true\"}}";

        JobConsumer.JobResult result = fixture.process(buildJob(payload(body)));

        assertEquals(JobConsumer.JobResult.OK, result);
        ArgumentCaptor<Map<String, Object>> props = ArgumentCaptor.forClass(Map.class);
        verify(resourceResolver).create(eq(container), eq("image-1"), props.capture());
        assertEquals("pillar/components/image/v1/image", props.getValue().get("sling:resourceType"));
        assertEquals("/content/dam/img.png", props.getValue().get("fileReference"));
        assertEquals("false", props.getValue().get("imageFromPageImage"));
        assertEquals("true", props.getValue().get("isDecorative"));
        assertNull(props.getValue().get("alt"));
    }

    @Test
    void process_textComponent_createsNodeWithTextProps() throws LoginException, WCMException, PersistenceException {
        stubResolverAndPageManager();
        stubCreatedPage(createdPage());
        Resource container = mock(Resource.class);
        when(resourceResolver.getResource(CONTAINER_PATH)).thenReturn(container);

        String body = "\"body\":{\"sling:resourceType\":\"" + CONTAINER_TYPE + "\","
                + "\"text-1\":{\"sling:resourceType\":\"pillar/components/text/v1/text\","
                + "\"text\":\"<p>Hi</p>\",\"textIsRich\":\"true\"}}";

        JobConsumer.JobResult result = fixture.process(buildJob(payload(body)));

        assertEquals(JobConsumer.JobResult.OK, result);
        ArgumentCaptor<Map<String, Object>> props = ArgumentCaptor.forClass(Map.class);
        verify(resourceResolver).create(eq(container), eq("text-1"), props.capture());
        assertEquals("pillar/components/text/v1/text", props.getValue().get("sling:resourceType"));
        assertEquals("<p>Hi</p>", props.getValue().get("text"));
        assertEquals("true", props.getValue().get("textIsRich"));
    }

    @Test
    void process_separatorComponent_createsNodeAndAppliesStyleIds() throws LoginException, WCMException, PersistenceException {
        stubResolverAndPageManager();
        stubCreatedPage(createdPage());
        Resource container = mock(Resource.class);
        when(resourceResolver.getResource(CONTAINER_PATH)).thenReturn(container);

        // The created separator node is looked up again by addStyleIds to set cq:styleIds.
        String separatorPath = CONTAINER_PATH + "/separator-1";
        Resource separator = mock(Resource.class);
        when(resourceResolver.create(eq(container), eq("separator-1"), anyMap())).thenReturn(separator);
        when(separator.getPath()).thenReturn(separatorPath);
        ModifiableValueMap separatorVm = mock(ModifiableValueMap.class);
        when(resourceResolver.getResource(separatorPath)).thenReturn(separator);
        when(separator.adaptTo(ModifiableValueMap.class)).thenReturn(separatorVm);

        String body = "\"body\":{\"sling:resourceType\":\"" + CONTAINER_TYPE + "\","
                + "\"separator-1\":{\"sling:resourceType\":\"pillar/components/separator/v1/separator\","
                + "\"isDecorative\":\"true\",\"cq:styleIds\":[\"111\",\"222\"]}}";

        JobConsumer.JobResult result = fixture.process(buildJob(payload(body)));

        assertEquals(JobConsumer.JobResult.OK, result);
        ArgumentCaptor<Object> styleIds = ArgumentCaptor.forClass(Object.class);
        verify(separatorVm).put(eq("cq:styleIds"), styleIds.capture());
        assertArrayEquals(new String[]{"111", "222"}, (String[]) styleIds.getValue());
    }

    @Test
    void process_articleDetailComponent_createsFixedNodeUnderRootContainer()
            throws LoginException, WCMException, PersistenceException {
        stubResolverAndPageManager();
        stubCreatedPage(createdPage());
        // articledetail is created under root/container (not root/container/container) with a fixed node name.
        String articleContainerPath = PAGE_PATH + "/jcr:content/root/container";
        Resource container = mock(Resource.class);
        when(resourceResolver.getResource(articleContainerPath)).thenReturn(container);

        String body = "\"body\":{\"sling:resourceType\":\"" + CONTAINER_TYPE + "\","
                + "\"articledetail-1\":{\"sling:resourceType\":\"pillar/components/articledetail/v1/articledetail\","
                + "\"fragmentPath\":\"/content/dam/article\"}}";

        JobConsumer.JobResult result = fixture.process(buildJob(payload(body)));

        assertEquals(JobConsumer.JobResult.OK, result);
        ArgumentCaptor<Map<String, Object>> props = ArgumentCaptor.forClass(Map.class);
        verify(resourceResolver).create(eq(container), eq("articledetail"), props.capture());
        assertEquals("pillar/components/articledetail/v1/articledetail", props.getValue().get("sling:resourceType"));
        assertEquals("/content/dam/article", props.getValue().get("fragmentPath"));
    }

    @Test
    void process_nonObjectComponentMember_isSkipped() throws LoginException, WCMException, PersistenceException {
        stubResolverAndPageManager();
        stubCreatedPage(createdPage());

        // hero-cards-1 is a string, not an object -> skipped before any container lookup.
        String body = "\"body\":{\"sling:resourceType\":\"" + CONTAINER_TYPE + "\","
                + "\"hero-cards-1\":\"not-an-object\"}";

        JobConsumer.JobResult result = fixture.process(buildJob(payload(body)));

        assertEquals(JobConsumer.JobResult.OK, result);
        verify(resourceResolver, never()).create(any(), anyString(), anyMap());
    }

    // --- helpers ---

    private void stubResolverAndPageManager() throws LoginException {
        when(resourceResolverFactory.getServiceResourceResolver(anyMap())).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
    }

    /** A page mock wired with content resource, value map and path for the happy path. */
    private ModifiableValueMap stubCreatedPage(Page page) throws WCMException {
        when(pageManager.create(anyString(), anyString(), anyString(), anyString())).thenReturn(page);
        Resource content = mock(Resource.class);
        ModifiableValueMap valueMap = mock(ModifiableValueMap.class);
        when(page.getContentResource()).thenReturn(content);
        when(content.adaptTo(ModifiableValueMap.class)).thenReturn(valueMap);
        when(page.getPath()).thenReturn(PAGE_PATH);
        return valueMap;
    }

    private Page createdPage() {
        return mock(Page.class);
    }

    private static String containerBody() {
        return "\"body\":{\"sling:resourceType\":\"" + CONTAINER_TYPE + "\"}";
    }

    private static String payload(String body) {
        return "{" + VALID_PAGE + "," + body + "}";
    }

    private static Job buildJob(String pageData) {
        Job job = mock(Job.class);
        when(job.getProperty(PageImportJobConsumer.PAGE_DATA, String.class)).thenReturn(pageData);
        return job;
    }

}
