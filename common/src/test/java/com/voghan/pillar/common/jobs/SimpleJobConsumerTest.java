package com.voghan.pillar.common.jobs;

import com.day.cq.commons.jcr.JcrConstants;
import com.voghan.pillar.common.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class SimpleJobConsumerTest {
    private static final TestLogger logger = TestLoggerFactory.getTestLogger(SimpleJobConsumer.class);
    private static final AemContext context = AppAemContext.newAemContext();

    @InjectMocks
    private SimpleJobConsumer fixture;

    @Mock
    ResourceResolverFactory resourceResolverFactory;

    @Mock
    ResourceResolver resourceResolver;

    @BeforeEach
    void setupAll() throws LoginException {
        TestLoggerFactory.clear();
    }

    @Test
    void process() throws LoginException {
        Job job = mock(Job.class);
        String path = "/content/pillar/us/en";
        Resource resource = mock(Resource.class);
        ValueMap valueMap = mock(ValueMap.class);
        when(job.getProperty(SimpleJobConsumer.JOB_PATH, String.class)).thenReturn(path);
        when(resourceResolverFactory.getServiceResourceResolver(fixture.getAuthInfo())).thenReturn(resourceResolver);
        when(resourceResolver.getResource(path)).thenReturn(resource);
        when(resource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get(JcrConstants.JCR_LAST_MODIFIED_BY, String.class)).thenReturn("admin");

        JobConsumer.JobResult jobResult = fixture.process(job);

        List<LoggingEvent> events = logger.getLoggingEvents();
        assertEquals(2, events.size());
        LoggingEvent event = events.get(0);

        assertAll(
            () -> assertEquals(Level.INFO, event.getLevel()),
            () -> assertEquals(0, event.getArguments().size()),
            () -> assertEquals(JobConsumer.JobResult.OK, jobResult)
        );


    }
}
