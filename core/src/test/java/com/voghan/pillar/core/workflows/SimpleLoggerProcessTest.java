package com.voghan.pillar.core.workflows;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.google.common.base.Optional;
import java.util.List;
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
class SimpleLoggerProcessTest {

  private TestLogger logger = TestLoggerFactory.getTestLogger(SimpleLoggerProcess.class);

  @Mock
  private WorkItem workItem;
  @Mock
  private WorkflowData workflowData;
  @Mock
  private WorkflowSession workflowSession;
  @Mock
  private MetaDataMap metaDataMap;

  @InjectMocks
  private SimpleLoggerProcess fixture;

  @BeforeEach
  void setup() {
    TestLoggerFactory.clear();
    when(workItem.getWorkflowData()).thenReturn(workflowData);
  }

  @Test
  void execute_logsPayloadAndProcessArgs() throws WorkflowException {
    when(workflowData.getPayload()).thenReturn("/content/my-page");
    when(metaDataMap.get("PROCESS_ARGS", String.class)).thenReturn("myArg");

    fixture.execute(workItem, workflowSession, metaDataMap);

    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(1, events.size());
    assertAll(
        () -> assertEquals(Level.INFO, events.getFirst().getLevel()),
        () -> assertEquals("/content/my-page", events.getFirst().getArguments().getFirst()),
        () -> assertEquals("myArg", events.getFirst().getArguments().get(1))
    );
  }

  @Test
  void execute_nullProcessArgs_logsNull() throws WorkflowException {
    when(workflowData.getPayload()).thenReturn("/content/my-page");
    when(metaDataMap.get("PROCESS_ARGS", String.class)).thenReturn(null);

    fixture.execute(workItem, workflowSession, metaDataMap);

    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(1, events.size());
    assertAll(
        () -> assertEquals(Level.INFO, events.getFirst().getLevel()),
        () -> assertEquals("/content/my-page", events.getFirst().getArguments().getFirst()),
        () -> assertEquals(Optional.absent(), events.getFirst().getArguments().get(1))
    );
  }
}
