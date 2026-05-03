package com.voghan.pillar.common.jobs;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.day.cq.commons.jcr.JcrConstants;
import com.voghan.pillar.common.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import java.util.List;
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

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class SimpleJobConsumerTest {

  private static final TestLogger logger = TestLoggerFactory.getTestLogger(SimpleJobConsumer.class);
  private static final AemContext context = AppAemContext.newAemContext();

  private static final String TEST_PATH = "/content/pillar/us/en";
  private static final String TEST_TYPE = "CHANGED";

  @InjectMocks
  private SimpleJobConsumer fixture;

  @Mock ResourceResolverFactory resourceResolverFactory;
  @Mock ResourceResolver resourceResolver;

  @BeforeEach
  void setup() {
    TestLoggerFactory.clear();
  }

  // -------------------------------------------------------------------------
  // process() — return value and first log entry
  // -------------------------------------------------------------------------

  @Test
  void process_returnsOK() throws LoginException {
    when(resourceResolverFactory.getServiceResourceResolver(anyMap())).thenReturn(resourceResolver);
    Job job = jobWithPath(TEST_PATH, TEST_TYPE);
    when(resourceResolver.getResource(TEST_PATH)).thenReturn(null);

    JobConsumer.JobResult result = fixture.process(job);

    assertEquals(JobConsumer.JobResult.OK, result);
  }

  @Test
  void process_logsStartMessage() throws LoginException {
    when(resourceResolverFactory.getServiceResourceResolver(anyMap())).thenReturn(resourceResolver);
    Job job = jobWithPath(TEST_PATH, TEST_TYPE);
    when(resourceResolver.getResource(TEST_PATH)).thenReturn(null);

    fixture.process(job);

    LoggingEvent first = logger.getLoggingEvents().get(0);
    assertAll(
        () -> assertEquals(Level.INFO, first.getLevel()),
        () -> assertEquals("Starting new simple job", first.getMessage())
    );
  }

  @Test
  void process_logsWarn_whenLoginExceptionThrown() throws LoginException {
    when(resourceResolverFactory.getServiceResourceResolver(anyMap()))
        .thenThrow(new LoginException("no mapping"));

    JobConsumer.JobResult result = fixture.process(mock(Job.class));

    assertAll(
        () -> assertEquals(JobConsumer.JobResult.OK, result),
        () -> assertEquals(2, logger.getLoggingEvents().size()),
        () -> assertEquals(Level.WARN, logger.getLoggingEvents().get(1).getLevel())
    );
  }

  // -------------------------------------------------------------------------
  // logJobParams() — resource found branch
  // -------------------------------------------------------------------------

  @Test
  void logJobParams_logsModifiedBy_fromJcrLastModifiedBy() {
    Job job = jobWithPath(TEST_PATH, TEST_TYPE);
    Resource resource = resourceWithModifiedBy(JcrConstants.JCR_LAST_MODIFIED_BY, "alice");
    when(resourceResolver.getResource(TEST_PATH)).thenReturn(resource);

    fixture.logJobParams(resourceResolver, job);

    LoggingEvent event = logger.getLoggingEvents().get(0);
    assertAll(
        () -> assertEquals(Level.INFO, event.getLevel()),
        () -> assertEquals(TEST_PATH, event.getArguments().get(0)),
        () -> assertEquals(TEST_TYPE, event.getArguments().get(1)),
        () -> assertEquals("alice", event.getArguments().get(2))
    );
  }

  @Test
  void logJobParams_fallsBackToCqLastModifiedBy_whenJcrLastModifiedByIsEmpty() {
    Job job = jobWithPath(TEST_PATH, TEST_TYPE);
    Resource resource = mock(Resource.class);
    ValueMap valueMap = mock(ValueMap.class);
    when(resource.getValueMap()).thenReturn(valueMap);
    // jcr:lastModifiedBy is blank — should fall back to cq:lastModifiedBy
    when(valueMap.get(JcrConstants.JCR_LAST_MODIFIED_BY, String.class)).thenReturn("");
    when(resourceResolver.getResource(TEST_PATH)).thenReturn(resource);

    fixture.logJobParams(resourceResolver, job);

    LoggingEvent event = logger.getLoggingEvents().get(0);
    assertAll(
        () -> assertEquals(Level.INFO, event.getLevel()),
        () -> assertEquals("", event.getArguments().get(2))
    );
  }

  // -------------------------------------------------------------------------
  // logJobParams() — resource not found branch
  // -------------------------------------------------------------------------

  @Test
  void logJobParams_logsResourceNotFound_whenResourceIsNull() {
    Job job = jobWithPath(TEST_PATH, TEST_TYPE);
    when(resourceResolver.getResource(TEST_PATH)).thenReturn(null);

    fixture.logJobParams(resourceResolver, job);

    List<LoggingEvent> events = logger.getLoggingEvents();
    assertAll(
        () -> assertEquals(1, events.size()),
        () -> assertEquals(Level.INFO, events.get(0).getLevel()),
        () -> assertEquals(1, events.get(0).getArguments().size()),
        () -> assertEquals(TEST_PATH, events.get(0).getArguments().get(0))
    );
  }

  // -------------------------------------------------------------------------
  // Helpers
  // -------------------------------------------------------------------------

  private static Job jobWithPath(String path, String type) {
    Job job = mock(Job.class);
    when(job.getProperty(SimpleJobConsumer.JOB_PATH, String.class)).thenReturn(path);
    when(job.getProperty(SimpleJobConsumer.JOB_TYPE, String.class)).thenReturn(type);
    return job;
  }

  /**
   * Creates a resource mock where only one of the two modifier properties is populated.
   * The other returns null (Mockito default), simulating a real ValueMap.
   */
  private static Resource resourceWithModifiedBy(String propertyName, String value) {
    Resource resource = mock(Resource.class);
    ValueMap valueMap = mock(ValueMap.class);
    when(resource.getValueMap()).thenReturn(valueMap);
    when(valueMap.get(propertyName, String.class)).thenReturn(value);
    return resource;
  }
}
