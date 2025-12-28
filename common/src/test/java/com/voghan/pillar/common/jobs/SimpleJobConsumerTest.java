package com.voghan.pillar.common.jobs;

import com.voghan.pillar.common.listeners.SimpleResourceListener;
import com.voghan.pillar.common.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class SimpleJobConsumerTest {
    private static final TestLogger logger = TestLoggerFactory.getTestLogger(SimpleJobConsumer.class);
    private static final AemContext context = AppAemContext.newAemContext();

    private SimpleJobConsumer fixture;

    @Mock
    ResourceResolverFactory resourceResolverFactory;

    @BeforeEach
    void setupAll() {
        context.registerService(ResourceResolverFactory.class, resourceResolverFactory);
        fixture = context.registerInjectActivateService(new SimpleJobConsumer());
    }

    @Test
    void process() {
        Job job = mock(Job.class);

        JobConsumer.JobResult jobResult =fixture.process(job);

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
