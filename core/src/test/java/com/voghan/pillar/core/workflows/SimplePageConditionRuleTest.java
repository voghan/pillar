package com.voghan.pillar.core.workflows;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.wcm.api.Page;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
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

@ExtendWith(MockitoExtension.class)
class SimplePageConditionRuleTest {

  private final TestLogger logger = TestLoggerFactory.getTestLogger(SimplePageConditionRule.class);

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
  @Mock
  ResourceResolverFactory resourceResolverFactory;

  @InjectMocks
  private SimplePageConditionRule fixture;

  @BeforeEach
  void setup()  {
    TestLoggerFactory.clear();
    when(workItem.getWorkflowData()).thenReturn(workflowData);
    when(workflowData.getPayload()).thenReturn("/content/my-page");
  }

  @Test
  void execute_returnsTrue_whenResourceIsPage() throws WorkflowException, LoginException {
    Map<String, Object> expectedParams = Collections.singletonMap(
        ResourceResolverFactory.SUBSERVICE, (Object) SimplePageConditionRule.SERVICE_NAME);
    when(resourceResolverFactory.getServiceResourceResolver(expectedParams)).thenReturn(
        resourceResolver);
    when(resourceResolver.getResource("/content/my-page")).thenReturn(resource);
    when(resource.adaptTo(Page.class)).thenReturn(mock(Page.class));
    when(workItem.getWorkflowData()).thenReturn(workflowData);
    when(workflowData.getMetaDataMap()).thenReturn(metaDataMap);

    fixture.execute(workItem, workflowSession, metaDataMap);

    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(1, events.size());
    assertAll(
        () -> assertEquals(Level.INFO, events.getFirst().getLevel()),
        () -> assertEquals(true, events.getFirst().getArguments().get(1))
    );

  }

  @Test
  void execute_returnsFalse_whenLoginException() throws LoginException {
    when(resourceResolverFactory.getServiceResourceResolver(anyMap())).thenThrow(new LoginException());

    assertThrowsExactly(WorkflowException.class, () -> fixture.execute(workItem, workflowSession, metaDataMap));
  }

  @Test
  void execute_returnsFalse_whenResourceIsNotPage() throws WorkflowException, LoginException {
    Map<String, Object> expectedParams = Collections.singletonMap(
        ResourceResolverFactory.SUBSERVICE, (Object) SimplePageConditionRule.SERVICE_NAME);
    when(resourceResolverFactory.getServiceResourceResolver(expectedParams)).thenReturn(
        resourceResolver);
    when(resourceResolver.getResource("/content/my-page")).thenReturn(resource);
    when(resource.adaptTo(Page.class)).thenReturn(null);
    when(workItem.getWorkflowData()).thenReturn(workflowData);
    when(workflowData.getMetaDataMap()).thenReturn(metaDataMap);

    fixture.execute(workItem, workflowSession, metaDataMap);

    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(1, events.size());
    assertAll(
        () -> assertEquals(Level.INFO, events.getFirst().getLevel()),
        () -> assertEquals(false, events.getFirst().getArguments().get(1))
    );
  }

  @Test
  void execute_returnsFalse_whenResourceNotFound() throws WorkflowException, LoginException {
    Map<String, Object> expectedParams = Collections.singletonMap(
        ResourceResolverFactory.SUBSERVICE, (Object) SimplePageConditionRule.SERVICE_NAME);
    when(resourceResolverFactory.getServiceResourceResolver(expectedParams)).thenReturn(
        resourceResolver);
    when(resourceResolver.getResource("/content/my-page")).thenReturn(null);
    when(workItem.getWorkflowData()).thenReturn(workflowData);
    when(workflowData.getMetaDataMap()).thenReturn(metaDataMap);

    fixture.execute(workItem, workflowSession, metaDataMap);

    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(1, events.size());
    assertAll(
        () -> assertEquals(Level.INFO, events.getFirst().getLevel()),
        () -> assertEquals(false, events.getFirst().getArguments().get(1))
    );
  }

  @Test
  void execute_logsInfoMessage() throws WorkflowException, LoginException {
    Map<String, Object> expectedParams = Collections.singletonMap(
        ResourceResolverFactory.SUBSERVICE, (Object) SimplePageConditionRule.SERVICE_NAME);
    when(resourceResolverFactory.getServiceResourceResolver(expectedParams)).thenReturn(
        resourceResolver);
    when(resourceResolver.getResource("/content/my-page")).thenReturn(resource);
    when(resource.adaptTo(Page.class)).thenReturn(mock(Page.class));
    when(workItem.getWorkflowData()).thenReturn(workflowData);
    when(workflowData.getMetaDataMap()).thenReturn(metaDataMap);

    fixture.execute(workItem, workflowSession, metaDataMap);

    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(1, events.size());
    assertEquals(Level.INFO, events.getFirst().getLevel());
  }
}
