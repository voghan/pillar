package com.voghan.pillar.common.queries.impl;

import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.voghan.pillar.common.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class SimpleQueryBuilderTest {
    private static final AemContext context = AppAemContext.newAemContext();

    private TestLogger logger = TestLoggerFactory.getTestLogger(SimpleQueryBuilderImpl.class);

    @Mock
    QueryBuilder queryBuilder;

    @InjectMocks
    SimpleQueryBuilderImpl simpleQueryBuilder;

    @Test
    void search_default() throws RepositoryException {

        Query query = mock(Query.class);
        SearchResult searchResult = mock(SearchResult.class);
        List<Hit> hits = new ArrayList<>();
        Hit hit = mock(Hit.class);
        hits.add(hit);
        Resource resource = mock(Resource.class);
        ResourceResolver leakingResourceResolver = mock(ResourceResolver.class);
        when(queryBuilder.createQuery(any(), any())).thenReturn(query);
        when(query.getResult()).thenReturn(searchResult);
        when(searchResult.getHits()).thenReturn(hits);
        when(hit.getResource()).thenReturn(resource);
        when(hit.getPath()).thenReturn("/conten/dam/asset");
        when(resource.getResourceResolver()).thenReturn(leakingResourceResolver);
        Map<String, String> map = new HashMap<String, String>();
        map.put("path", "/content/dam/pillar/cfm/basic-cards/demo");

        List<Resource> resources = simpleQueryBuilder.search(context.request(), map);

        assertFalse(resources.isEmpty());
    }

    @Test
    void search_throwsException() throws RepositoryException {

        Query query = mock(Query.class);
        SearchResult searchResult = mock(SearchResult.class);
        List<Hit> hits = new ArrayList<>();
        Hit hit = mock(Hit.class);
        hits.add(hit);
        when(queryBuilder.createQuery(any(), any())).thenReturn(query);
        when(query.getResult()).thenReturn(searchResult);
        when(searchResult.getHits()).thenReturn(hits);
        when(hit.getResource()).thenThrow(new RepositoryException("Test"));
        Map<String, String> map = new HashMap<String, String>();
        map.put("path", "/content/dam/pillar/cfm/basic-cards/demo");
        map.put("type", "dam:AssetContent");

        List<Resource> resources = simpleQueryBuilder.search(context.request(), map);

        List<LoggingEvent> events = logger.getLoggingEvents();
        assertEquals(2, events.size());
        LoggingEvent event = events.get(1);
        assertEquals(Level.ERROR, event.getLevel());
    }
}
