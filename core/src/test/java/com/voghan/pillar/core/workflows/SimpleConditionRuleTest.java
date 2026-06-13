package com.voghan.pillar.core.workflows;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.wcm.api.Page;
import java.util.List;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimpleConditionRuleTest {

  private final TestLogger logger = TestLoggerFactory.getTestLogger(SimpleConditionRule.class);

  @Mock
  private WorkItem workItem;
  @Mock
  private WorkflowData workflowData;
  @Mock
  private WorkflowSession workflowSession;
  @Mock
  private MetaDataMap metaDataMap;
  @Mock
  private ResourceResolver resourceResolver;
  @Mock
  private Resource resource;

  @InjectMocks
  private SimpleConditionRule fixture;

  @BeforeEach
  void setup() {
    TestLoggerFactory.clear();
    when(workItem.getWorkflowData()).thenReturn(workflowData);
    when(workflowData.getPayload()).thenReturn("/content/my-page");
  }

  @Test
  void evaluate_returnsTrue_whenResourceIsPage() {
    when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resourceResolver);
    when(resourceResolver.getResource("/content/my-page")).thenReturn(resource);
    when(resource.adaptTo(Page.class)).thenReturn(mock(Page.class));

    assertTrue(fixture.evaluate(workItem, workflowSession, metaDataMap));
  }

  @Test
  void evaluate_returnsFalse_whenResourceIsNotPage() {
    when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resourceResolver);
    when(resourceResolver.getResource("/content/my-page")).thenReturn(resource);
    when(resource.adaptTo(Page.class)).thenReturn(null);

    assertFalse(fixture.evaluate(workItem, workflowSession, metaDataMap));
  }

  @Test
  void evaluate_returnsFalse_whenResourceNotFound() {
    when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resourceResolver);
    when(resourceResolver.getResource("/content/my-page")).thenReturn(null);

    assertFalse(fixture.evaluate(workItem, workflowSession, metaDataMap));
  }

  @Test
  void evaluate_returnsFalse_whenResourceResolverIsNull() {
    when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(null);

    assertFalse(fixture.evaluate(workItem, workflowSession, metaDataMap));
  }

  @Test
  void evaluate_logsInfoMessage() {
    when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resourceResolver);
    when(resourceResolver.getResource("/content/my-page")).thenReturn(resource);
    when(resource.adaptTo(Page.class)).thenReturn(mock(Page.class));

    fixture.evaluate(workItem, workflowSession, metaDataMap);

    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(1, events.size());
    assertEquals(Level.INFO, events.getFirst().getLevel());
  }
}
