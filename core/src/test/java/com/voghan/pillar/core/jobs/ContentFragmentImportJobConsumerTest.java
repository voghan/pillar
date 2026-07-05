package com.voghan.pillar.core.jobs;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.ContentFragmentException;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.voghan.pillar.core.models.cfm.ArticleDetailCfm;
import com.voghan.pillar.core.models.cfm.CardCfm;
import com.voghan.pillar.core.models.cfm.FeaturedCardCfm;
import com.voghan.pillar.core.models.cfm.SimpleCardCfm;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ContentFragmentImportJobConsumerTest {

    private static final String IMPORT_DATA = "IMPORT_DATA";

    // A model path that is neither article-detail nor card, so addModelData is a no-op.
    private static final String PLAIN_MODEL = "/conf/pillar/settings/dam/cfm/models/other";
    private static final String HERO_MODEL  = "/conf/pillar/settings/dam/cfm/models/hero";
    private static final String FOLDER      = "/content/dam/pillar/cfm/imports";
    private static final String HERO_FOLDER = "/content/dam/pillar/cfm/hero-cards/imports";

    // Resource paths relative to the test classpath
    private static final String RES_ARTICLE_CARD         = "pillar-core/model/jobs/article-card.json";
    private static final String RES_ARTICLE_CARD_NO_MASTER = "pillar-core/model/jobs/article-card-no-master.json";
    private static final String RES_BASIC_CARD           = "pillar-core/model/jobs/basic-card.json";
    private static final String RES_GENERIC_CFM          = "pillar-core/model/jobs/generic-cfm.json";
    private static final String RES_HERO_CARD            = "pillar-core/model/jobs/hero-card.json";
    private static final String RES_SIMPLE_CARD            = "pillar-core/model/jobs/simple-card.json";
    private static final String RES_FEATURED_CARD            = "pillar-core/model/jobs/featured-card.json";

    @InjectMocks
    private ContentFragmentImportJobConsumer fixture;

    @Mock
    ResourceResolverFactory resourceResolverFactory;

    @Mock
    ResourceResolver resourceResolver;

    @Test
    void process_returnsOK() throws LoginException, ContentFragmentException, PersistenceException, IOException {
        stubResolver();
        stubModel(PLAIN_MODEL, FOLDER);

        JobConsumer.JobResult result = fixture.process(buildJob(RES_GENERIC_CFM));

        assertEquals(JobConsumer.JobResult.OK, result);
        verify(resourceResolver, times(1)).commit();
    }

    @Test
    void process_modelResourceNotFound_returnsCancel() throws LoginException, IOException {
        stubResolver();
        // The model path resolves to nothing -> ContentFragmentException -> CANCEL.
        when(resourceResolver.getResource(PLAIN_MODEL)).thenReturn(null);

        JobConsumer.JobResult result = fixture.process(buildJob(RES_GENERIC_CFM));

        assertEquals(JobConsumer.JobResult.CANCEL, result);
        verify(resourceResolver, never()).commit();
    }

    @Test
    void process_modelNotAdaptableToTemplate_returnsCancel() throws LoginException, IOException {
        stubResolver();
        Resource modelResource = mock(Resource.class);
        when(resourceResolver.getResource(PLAIN_MODEL)).thenReturn(modelResource);
        when(modelResource.adaptTo(FragmentTemplate.class)).thenReturn(null);

        JobConsumer.JobResult result = fixture.process(buildJob(RES_GENERIC_CFM));

        assertEquals(JobConsumer.JobResult.CANCEL, result);
    }

    @Test
    void process_folderNotFound_returnsCancel() throws LoginException, IOException {
        stubResolver();
        Resource modelResource = mock(Resource.class);
        when(resourceResolver.getResource(PLAIN_MODEL)).thenReturn(modelResource);
        when(modelResource.adaptTo(FragmentTemplate.class)).thenReturn(mock(FragmentTemplate.class));
        // Model resolves, but the destination folder does not -> CANCEL.
        when(resourceResolver.getResource(FOLDER)).thenReturn(null);

        JobConsumer.JobResult result = fixture.process(buildJob(RES_GENERIC_CFM));

        assertEquals(JobConsumer.JobResult.CANCEL, result);
    }

    @Test
    void process_createFragmentReturnsNull_returnsFailed()
            throws LoginException, ContentFragmentException, PersistenceException, IOException {
        stubResolver();
        Resource modelResource = mock(Resource.class);
        when(resourceResolver.getResource(PLAIN_MODEL)).thenReturn(modelResource);
        FragmentTemplate template = mock(FragmentTemplate.class);
        when(modelResource.adaptTo(FragmentTemplate.class)).thenReturn(template);
        Resource folder = mock(Resource.class);
        when(resourceResolver.getResource(FOLDER)).thenReturn(folder);
        when(template.createFragment(eq(folder), anyString(), anyString())).thenReturn(null);

        JobConsumer.JobResult result = fixture.process(buildJob(RES_GENERIC_CFM));

        assertEquals(JobConsumer.JobResult.FAILED, result);
        verify(resourceResolver, never()).commit();
    }

    @Test
    void process_malformedJson_returnsCancel() throws LoginException {
        stubResolver();

        JobConsumer.JobResult result = fixture.process(buildJobFromString("{ not json"));

        assertEquals(JobConsumer.JobResult.CANCEL, result);
    }

    @Test
    void process_loginException_returnsCancel() throws LoginException {
        when(resourceResolverFactory.getServiceResourceResolver(anyMap()))
                .thenThrow(new LoginException("no service user"));

        // The import data is never read, so a bare job mock is enough.
        JobConsumer.JobResult result = fixture.process(mock(Job.class));

        assertEquals(JobConsumer.JobResult.CANCEL, result);
    }

    @Test
    void process_commitThrows_returnsCancel()
            throws LoginException, ContentFragmentException, PersistenceException, IOException {
        stubResolver();
        stubModel(PLAIN_MODEL, FOLDER);
        doThrow(new PersistenceException("commit failed")).when(resourceResolver).commit();

        JobConsumer.JobResult result = fixture.process(buildJob(RES_GENERIC_CFM));

        assertEquals(JobConsumer.JobResult.CANCEL, result);
    }

    @Test
    void process_articleDetailModel_setsTextAndDateProperties()
            throws LoginException, ContentFragmentException, PersistenceException, IOException {
        stubResolver();
        ContentFragment fragment = stubModel(ArticleDetailCfm.MODEL, FOLDER);
        ContentElement element = stubElements(fragment);

        JobConsumer.JobResult result = fixture.process(buildJob(RES_ARTICLE_CARD));

        assertEquals(JobConsumer.JobResult.OK, result);
        verify(resourceResolver, times(1)).commit();
        // Each field is written to its own element via setContent, with the right mime type.
        verify(element).setContent("Downhill Skiing Snowbird", "text/plain");
        verify(element).setContent("/us/en/articles/downhill-skiing-snowbird", "text/plain");
        verify(element).setContent("<p>Full body content</p>", "text/html");
        verify(element).setContent("<p>Short description</p>", "text/html");
        // postDate is applied through the FragmentData round-trip.
        verify(element, atLeastOnce()).setValue(any(FragmentData.class));
    }

    @Test
    void process_articleDetailModel_withoutMaster_isNoOp()
            throws LoginException, ContentFragmentException, PersistenceException, IOException {
        stubResolver();
        ContentFragment fragment = stubModel(ArticleDetailCfm.MODEL, FOLDER);

        // No "master" object -> no properties are touched, but the job still succeeds.
        JobConsumer.JobResult result = fixture.process(buildJob(RES_ARTICLE_CARD_NO_MASTER));

        assertEquals(JobConsumer.JobResult.OK, result);
        verify(resourceResolver, times(1)).commit();
        verify(fragment, never()).getElement(anyString());
    }

    @Test
    void process_cardModel_setsTextProperties()
            throws LoginException, ContentFragmentException, PersistenceException, IOException {
        stubResolver();
        ContentFragment fragment = stubModel(CardCfm.MODEL, FOLDER);
        ContentElement element = stubElements(fragment);

        JobConsumer.JobResult result = fixture.process(buildJob(RES_BASIC_CARD));

        assertEquals(JobConsumer.JobResult.OK, result);
        verify(resourceResolver, times(1)).commit();
        verify(element).setContent("Downhill Skiing Snowbird", "text/plain");
        verify(element).setContent("<p>Short description here</p>", "text/html");
    }

    @Test
    void process_heroCardModel_returnsOK()
            throws LoginException, ContentFragmentException, PersistenceException, IOException {
        stubResolver();
        ContentFragment fragment = stubModel(HERO_MODEL, HERO_FOLDER);
        ContentElement element = stubElements(fragment);

        JobConsumer.JobResult result = fixture.process(buildJob(RES_HERO_CARD));

        assertEquals(JobConsumer.JobResult.OK, result);
        verify(resourceResolver, times(1)).commit();
        verify(element).setContent("Hero Card", "text/plain");
    }

    @Test
    void process_simpleCardModel_returnsOK()
            throws LoginException, ContentFragmentException, PersistenceException, IOException {
        stubResolver();
        ContentFragment fragment = stubModel(SimpleCardCfm.MODEL, FOLDER);
        ContentElement element = stubElements(fragment);

        JobConsumer.JobResult result = fixture.process(buildJob(RES_SIMPLE_CARD));

        assertEquals(JobConsumer.JobResult.OK, result);
        verify(resourceResolver, times(1)).commit();
        verify(element).setContent("Simple Card", "text/plain");
    }

    @Test
    void process_featuredCardModel_returnsOK()
            throws LoginException, ContentFragmentException, PersistenceException, IOException {
        stubResolver();
        ContentFragment fragment = stubModel(FeaturedCardCfm.MODEL, FOLDER);
        ContentElement element = stubElements(fragment);

        JobConsumer.JobResult result = fixture.process(buildJob(RES_FEATURED_CARD));

        assertEquals(JobConsumer.JobResult.OK, result);
        verify(resourceResolver, times(1)).commit();
        verify(element).setContent("Featured Card", "text/plain");
    }

    // --- helpers ---

    private void stubResolver() throws LoginException {
        when(resourceResolverFactory.getServiceResourceResolver(anyMap())).thenReturn(resourceResolver);
    }

    /**
     * Wires the model path to a resource that adapts to a template, and the destination folder to a
     * resource, so the template produces a fragment for the configured name and title.
     */
    private ContentFragment stubModel(String modelPath, String folderPath)
            throws ContentFragmentException, PersistenceException {
        Resource modelResource = mock(Resource.class);
        when(resourceResolver.getResource(modelPath)).thenReturn(modelResource);
        FragmentTemplate template = mock(FragmentTemplate.class);
        when(modelResource.adaptTo(FragmentTemplate.class)).thenReturn(template);
        Resource folder = mock(Resource.class);
        when(resourceResolver.getResource(folderPath)).thenReturn(folder);
        ContentFragment fragment = mock(ContentFragment.class);
        when(template.createFragment(eq(folder), anyString(), anyString())).thenReturn(fragment);
        return fragment;
    }

    /** Makes every {@code getElement(...)} on the fragment return the same stubbed element. */
    private ContentElement stubElements(ContentFragment fragment) {
        ContentElement element = mock(ContentElement.class);
        when(fragment.getElement(anyString())).thenReturn(element);
        // Only the article-detail path reads the value (for the date round-trip); keep lenient.
        lenient().when(element.getValue()).thenReturn(mock(FragmentData.class));
        return element;
    }

    /**
     * Loads the given classpath resource and builds a mocked {@link Job} whose
     * {@code IMPORT_DATA} property returns the file's contents.
     *
     * @param resourcePath path relative to the test classpath (no leading slash)
     */
    private static Job buildJob(String resourcePath) throws IOException {
        try (InputStream is = ContentFragmentImportJobConsumerTest.class
                .getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Test resource not found on classpath: " + resourcePath);
            }
            return buildJobFromString(new String(is.readAllBytes(), StandardCharsets.UTF_8));
        }
    }

    /** Builds a mocked {@link Job} with a literal string as its {@code IMPORT_DATA} payload. */
    private static Job buildJobFromString(String importData) {
        Job job = mock(Job.class);
        when(job.getProperty(IMPORT_DATA, String.class)).thenReturn(importData);
        return job;
    }
}
