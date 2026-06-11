package com.voghan.pillar.common.listeners;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import org.apache.jackrabbit.api.observation.JackrabbitEventFilter;
import org.apache.jackrabbit.api.observation.JackrabbitObservationManager;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
public class RepPolicyModifiedListener implements EventListener {

  private static final String PILLAR_EVENT_LISTENER = "SimpleEventListener";
  private static final Logger LOGGER = LoggerFactory.getLogger(RepPolicyModifiedListener.class);

  private JackrabbitObservationManager observationManager;
  private Session session;

  @Reference
  private SlingRepository repository;

  @Activate
  public void activate() {

    try {
      session = repository.loginService(PILLAR_EVENT_LISTENER, null);

      String absolutePath = "/";
      String[] nodeTypes = new String[] {"rep:ACL","rep:GrantACE", "rep:DenyACE"};
      int eventTypes = Event.NODE_ADDED | Event.NODE_REMOVED | Event.PROPERTY_CHANGED;

      JackrabbitEventFilter jackrabbitEventFilter = new JackrabbitEventFilter()
          .setAbsPath( absolutePath)
          .setNodeTypes(nodeTypes)
          .setEventTypes(eventTypes)
          .setIsDeep(true)
          .setNoExternal(true)
          .setNoLocal(false);

      Workspace workSpace = session.getWorkspace();
      observationManager = (JackrabbitObservationManager) workSpace.getObservationManager();
      observationManager.addEventListener(this, jackrabbitEventFilter);
      LOGGER.info("The rep:Policy Event Listener is Registered at {} for the event type {}.",
          absolutePath, eventTypes);
    } catch (RepositoryException e) {
      LOGGER.error("An error occurred while getting session", e);
    }
  }

  @Override
  public void onEvent(EventIterator events) {

    for (EventIterator it = events; it.hasNext(); ) {
      Event event = it.nextEvent();
      logEvent(event);
    }
  }

  protected void logEvent(Event event) {
    try {
      String userId = event.getUserID();
      String eventType = switch (event.getType()) {
        case Event.NODE_ADDED -> "added";
        case Event.NODE_REMOVED -> "removed";
        default -> "changed";
      };
      String path = event.getPath();
      LOGGER.info("Event occurred: User {} {} {} ", userId, eventType, path);
    } catch (RepositoryException e) {
      LOGGER.error("An error occurred while getting event path", e);
    }
  }

  @Deactivate
  protected void deactivate() {
    try {
      if (null != observationManager) {
        observationManager.removeEventListener(this);
        LOGGER.info("The rep:Policy Event Listener is removed.");
      }
    } catch (RepositoryException e) {
      LOGGER.error("An error occurred while removing event listener", e);
    } finally {
      if (null != session) {
        session.logout();
      }
    }
  }
}
