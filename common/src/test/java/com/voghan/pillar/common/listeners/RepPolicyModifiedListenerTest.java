package com.voghan.pillar.common.listeners;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import org.apache.jackrabbit.api.observation.JackrabbitEventFilter;
import org.apache.jackrabbit.api.observation.JackrabbitObservationManager;
import org.apache.sling.jcr.api.SlingRepository;
import org.junit.jupiter.api.AfterEach;
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
class RepPolicyModifiedListenerTest {

  private static final TestLogger logger = TestLoggerFactory.getTestLogger(
      RepPolicyModifiedListener.class);

  @Mock
  private SlingRepository repository;
  @Mock
  private Session session;
  @Mock
  private Workspace workspace;
  @Mock
  private JackrabbitObservationManager observationManager;

  @InjectMocks
  private RepPolicyModifiedListener fixture;

  @BeforeEach
  void setup() throws RepositoryException {
    logger.clearAll();
    when(repository.loginService(anyString(), isNull())).thenReturn(session);
    when(session.getWorkspace()).thenReturn(workspace);
    when(workspace.getObservationManager()).thenReturn(observationManager);
    fixture.activate();
  }

  @AfterEach
  void tearDown() {
    logger.clearAll();
  }

  @Test
  void activate_registersEventListener() throws RepositoryException {
    verify(observationManager).addEventListener(eq(fixture), any(JackrabbitEventFilter.class));
  }

  @Test
  void activate_logsInfoMessage() {
    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(1, events.size());
    assertEquals(Level.INFO, events.getFirst().getLevel());
  }

  @Test
  void activate_throwsException() throws RepositoryException {
    when(repository.loginService(anyString(), isNull())).thenThrow(new RepositoryException());
    fixture.activate();

    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(2, events.size());
    assertEquals(Level.ERROR, events.get(1).getLevel());
  }

  @Test
  void onEvent_logsEachEvent() throws RepositoryException {
    logger.clearAll();

    Event event = mock(Event.class);
    when(event.getUserID()).thenReturn("admin");
    when(event.getType()).thenReturn(Event.PROPERTY_CHANGED);
    when(event.getPath()).thenReturn("/content/rep:policy");

    EventIterator iterator = mock(EventIterator.class);
    when(iterator.hasNext()).thenReturn(true, false);
    when(iterator.nextEvent()).thenReturn(event);

    fixture.onEvent(iterator);

    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(1, events.size());
    assertEquals(Level.INFO, events.getFirst().getLevel());
  }

  @Test
  void logEvent_nodeAdded_logsAddAction() throws RepositoryException {
    logger.clearAll();

    Event event = mock(Event.class);
    when(event.getUserID()).thenReturn("admin");
    when(event.getType()).thenReturn(Event.NODE_ADDED);
    when(event.getPath()).thenReturn("/content/rep:policy");

    fixture.logEvent(event);

    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(1, events.size());
    assertAll(
        () -> assertEquals(Level.INFO, events.getFirst().getLevel()),
        () -> assertEquals("admin", events.getFirst().getArguments().getFirst()),
        () -> assertEquals("added", events.getFirst().getArguments().get(1)),
        () -> assertEquals("/content/rep:policy", events.getFirst().getArguments().get(2))
    );
  }

  @Test
  void logEvent_propertyChanged_logsChangedAction() throws RepositoryException {
    logger.clearAll();

    Event event = mock(Event.class);
    when(event.getUserID()).thenReturn("admin");
    when(event.getType()).thenReturn(Event.PROPERTY_CHANGED);
    when(event.getPath()).thenReturn("/content/rep:policy");

    fixture.logEvent(event);

    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(1, events.size());
    assertAll(
        () -> assertEquals(Level.INFO, events.getFirst().getLevel()),
        () -> assertEquals("admin", events.getFirst().getArguments().getFirst()),
        () -> assertEquals("changed", events.getFirst().getArguments().get(1)),
        () -> assertEquals("/content/rep:policy", events.getFirst().getArguments().get(2))
    );
  }

  @Test
  void logEvent_nodeRemoved_logsRemoveAction() throws RepositoryException {
    logger.clearAll();

    Event event = mock(Event.class);
    when(event.getUserID()).thenReturn("admin");
    when(event.getType()).thenReturn(Event.NODE_REMOVED);
    when(event.getPath()).thenReturn("/content/rep:policy");

    fixture.logEvent(event);

    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(1, events.size());
    assertAll(
        () -> assertEquals(Level.INFO, events.getFirst().getLevel()),
        () -> assertEquals("admin", events.getFirst().getArguments().getFirst()),
        () -> assertEquals("removed", events.getFirst().getArguments().get(1)),
        () -> assertEquals("/content/rep:policy", events.getFirst().getArguments().get(2))
    );
  }

  @Test
  void logEvent_nodeRemoved_throwsException() throws RepositoryException {
    logger.clearAll();

    Event event = mock(Event.class);
    when(event.getUserID()).thenReturn("admin");
    when(event.getType()).thenReturn(Event.NODE_REMOVED);
    when(event.getPath()).thenThrow(new RepositoryException("Get path failed"));

    fixture.logEvent(event);

    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(1, events.size());
    assertAll(
        () -> assertEquals(Level.ERROR, events.getFirst().getLevel()),
        () -> assertEquals("An error occurred while getting event path", events.getFirst().getMessage())
    );
  }

  @Test
  void deactivate_removesListenerAndLogsOut() throws RepositoryException {
    fixture.deactivate();
    verify(observationManager).removeEventListener(fixture);
    verify(session).logout();
  }

  @Test
  void deactivate_repositoryException_stillLogsOut() throws RepositoryException {
    doThrow(RepositoryException.class).when(observationManager).removeEventListener(fixture);
    fixture.deactivate();
    verify(session).logout();
  }
}
