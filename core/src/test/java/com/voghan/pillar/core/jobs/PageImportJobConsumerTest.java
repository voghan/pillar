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

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        assertEquals(JobConsumer.JobResult.FAILED, result);
    }

    @Test
    void process_persistenceException_returnsFailed() throws LoginException, WCMException, PersistenceException {
        stubResolverAndPageManager();
        stubCreatedPage(createdPage());
        doThrow(new PersistenceException("commit failed")).when(resourceResolver).commit();

        JobConsumer.JobResult result = fixture.process(buildJob(payload(containerBody())));

        assertEquals(JobConsumer.JobResult.FAILED, result);
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
