package com.voghan.pillar.common.handler;

import com.day.cq.workflow.event.WorkflowEvent;
import com.voghan.pillar.common.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.event.Event;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class SimpleWorkflowEventHandlerTest {
    private static final AemContext context = AppAemContext.newAemContext();
    private SimpleWorkflowEventHandler workflowEventHandler;

    private TestLogger logger = TestLoggerFactory.getTestLogger(SimpleWorkflowEventHandler.class);

    @BeforeEach
    void setup() {
        TestLoggerFactory.clear();

        workflowEventHandler = context.registerInjectActivateService(new SimpleWorkflowEventHandler());
    }

    @Test
    void handleEvent_started() {
        Map<String, String> props = new HashMap<>();
        props.put(WorkflowEvent.EVENT_TYPE, WorkflowEvent.WORKFLOW_STARTED_EVENT);
        Event event = new Event(WorkflowEvent.EVENT_TOPIC,props);

        workflowEventHandler.handleEvent(event);

        List<LoggingEvent> loggingEvents = logger.getLoggingEvents();
        assertEquals(1, loggingEvents.size());
        LoggingEvent loggingEvent = loggingEvents.get(0);

        assertAll(
            () -> assertEquals(Level.INFO, loggingEvent.getLevel()),
            () -> assertEquals(1, loggingEvent.getArguments().size())
        );
    }

    @Test
    void handleEvent_completed() {
        Map<String, String> props = new HashMap<>();
        props.put(WorkflowEvent.EVENT_TYPE, WorkflowEvent.WORKFLOW_COMPLETED_EVENT);
        Event event = new Event(WorkflowEvent.EVENT_TOPIC,props);

        workflowEventHandler.handleEvent(event);

        List<LoggingEvent> loggingEvents = logger.getLoggingEvents();
        assertEquals(1, loggingEvents.size());
        LoggingEvent loggingEvent = loggingEvents.get(0);

        assertAll(
            () -> assertEquals(Level.INFO, loggingEvent.getLevel()),
            () -> assertEquals(0, loggingEvent.getArguments().size())
        );
    }

    @Test
    void handleEvent_aborted() {
        Map<String, String> props = new HashMap<>();
        props.put(WorkflowEvent.EVENT_TYPE, WorkflowEvent.WORKFLOW_ABORTED_EVENT);
        Event event = new Event(WorkflowEvent.EVENT_TOPIC,props);

        workflowEventHandler.handleEvent(event);

        List<LoggingEvent> loggingEvents = logger.getLoggingEvents();
        assertEquals(1, loggingEvents.size());
        LoggingEvent loggingEvent = loggingEvents.get(0);

        assertAll(
            () -> assertEquals(Level.INFO, loggingEvent.getLevel()),
            () -> assertEquals(0, loggingEvent.getArguments().size())
        );
    }

    @Test
    void handleEvent_suspended() {
        Map<String, String> props = new HashMap<>();
        props.put(WorkflowEvent.EVENT_TYPE, WorkflowEvent.WORKFLOW_SUSPENDED_EVENT);
        Event event = new Event(WorkflowEvent.EVENT_TOPIC,props);

        workflowEventHandler.handleEvent(event);

        List<LoggingEvent> loggingEvents = logger.getLoggingEvents();
        assertEquals(1, loggingEvents.size());
        LoggingEvent loggingEvent = loggingEvents.get(0);

        assertAll(
            () -> assertEquals(Level.INFO, loggingEvent.getLevel()),
            () -> assertEquals(0, loggingEvent.getArguments().size())
        );
    }

    @Test
    void handleEvent_failed() {
        Map<String, String> props = new HashMap<>();
        props.put(WorkflowEvent.EVENT_TYPE, WorkflowEvent.JOB_FAILED_EVENT);
        Event event = new Event(WorkflowEvent.EVENT_TOPIC,props);

        workflowEventHandler.handleEvent(event);

        List<LoggingEvent> loggingEvents = logger.getLoggingEvents();
        assertEquals(1, loggingEvents.size());
        LoggingEvent loggingEvent = loggingEvents.get(0);

        assertAll(
            () -> assertEquals(Level.INFO, loggingEvent.getLevel()),
            () -> assertEquals(0, loggingEvent.getArguments().size())
        );
    }

    @Test
    void handleEvent_unknown() {
        Map<String, String> props = new HashMap<>();
        props.put(WorkflowEvent.EVENT_TYPE, "");
        Event event = new Event(WorkflowEvent.EVENT_TOPIC,props);
        props.put(WorkflowEvent.EVENT_TYPE, "");

        workflowEventHandler.handleEvent(event);

        List<LoggingEvent> loggingEvents = logger.getLoggingEvents();
        assertEquals(1, loggingEvents.size());
        LoggingEvent loggingEvent = loggingEvents.get(0);

        assertAll(
            () -> assertEquals(Level.WARN, loggingEvent.getLevel()),
            () -> assertEquals(1, loggingEvent.getArguments().size())
        );
    }
}
