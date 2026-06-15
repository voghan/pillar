package com.voghan.pillar.common.workflows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

class SimplePillarParticipantStepTest {

  private final TestLogger logger = TestLoggerFactory.getTestLogger(SimplePillarParticipantStep.class);
  private final SimplePillarParticipantStep fixture = new SimplePillarParticipantStep();

  @BeforeEach
  void setup() {
    TestLoggerFactory.clear();
  }

  @Test
  void getParticipant_returnsSystemUser() throws WorkflowException {
    String result = fixture.getParticipant(
        mock(WorkItem.class), mock(WorkflowSession.class), mock(MetaDataMap.class));

    assertEquals("pillar-workflow-service-user", result);
  }

  @Test
  void getParticipant_logsInfoMessage() throws WorkflowException {
    fixture.getParticipant(
        mock(WorkItem.class), mock(WorkflowSession.class), mock(MetaDataMap.class));

    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(1, events.size());
    assertEquals(Level.INFO, events.getFirst().getLevel());
  }
}
