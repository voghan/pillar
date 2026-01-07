package com.voghan.pillar.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.granite.workflow.model.WorkflowModel;
import com.day.cq.commons.Externalizer;
import com.voghan.pillar.common.emails.SimpleEmailService;
import com.voghan.pillar.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class NotificationProcessTest {

    private static final AemContext context = AppAemContext.newAemContext();

    private TestLogger logger = TestLoggerFactory.getTestLogger(NotificationProcess.class);

    @Mock
    ResourceResolverFactory resourceResolverFactory;

    @Mock
    Externalizer externalizer;

    @Mock
    SimpleEmailService emailService;

    @Mock
    ResourceResolver resourceResolver;

    @Mock
    Session session;

    @Mock
    UserManager userManager;

    @Mock
    Authorizable authorizable;

    @InjectMocks
    NotificationProcess notificationProcess;

    @BeforeEach
    void setup() throws LoginException {
        TestLoggerFactory.clear();

        Map<String, Object> expectedParams = Collections.singletonMap(
            ResourceResolverFactory.SUBSERVICE, (Object) NotificationProcess.SERVICE_NAME);
        when(resourceResolverFactory.getServiceResourceResolver(expectedParams)).thenReturn(resourceResolver);

    }

    @Test
    void execute_default() throws WorkflowException, RepositoryException {

        WorkItem workItem = mock(WorkItem.class);
        WorkflowData workflowData = mock(WorkflowData.class);
        WorkflowModel workflowModel = mock(WorkflowModel.class);
        Workflow workflow = mock(Workflow.class);
        WorkflowSession workflowSession = mock(WorkflowSession.class);
        MetaDataMap metaDataMap = mock(MetaDataMap.class);
        Value value = mock(Value.class);
        Value[] values = new Value[] {value};
        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workItem.getWorkflow()).thenReturn(workflow);
        when(workflow.getInitiator()).thenReturn("admin");
        when(workflowData.getPayload()).thenReturn("payload");
        when(metaDataMap.get("PROCESS_ARGS", String.class)).thenReturn("args");
        when(userManager.getAuthorizable("admin")).thenReturn(authorizable);
        when(resourceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(userManager.getAuthorizable("admin")).thenReturn(authorizable);
        when(authorizable.getProperty("./profile/email")).thenReturn(values);
        when(value.getString()).thenReturn("no-reply@gmail.com");
        when(workflowData.getMetaDataMap()).thenReturn(metaDataMap);
        when(workflow.getWorkflowModel()).thenReturn(workflowModel);
        when(workflowModel.getTitle()).thenReturn("My Workflow");
        ArgumentCaptor<String> mailTo = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> tempalte = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Map<String, String>> params = ArgumentCaptor.forClass(Map.class);
        notificationProcess.execute(workItem, workflowSession, metaDataMap);

        verify(emailService).sendEmail(mailTo.capture(), tempalte.capture(), params.capture());
        assertEquals("no-reply@gmail.com", mailTo.getValue());
        assertEquals("/conf/pillar/notifications/email/workflow-notification.html", tempalte.getValue());
        assertEquals(4, params.getValue().size());
        assertEquals("Wally", params.getValue().get("givenName"));
        assertEquals("payload", params.getValue().get("payload"));
        assertEquals("My Workflow", params.getValue().get("workflowModel"));
//        assertEquals(null, params.getValue().get("authorLink"));

        List<LoggingEvent> loggingEvents = logger.getLoggingEvents();
        assertEquals(1, loggingEvents.size());
        LoggingEvent loggingEvent = loggingEvents.get(0);

        assertAll(
            () -> assertEquals(Level.INFO, loggingEvent.getLevel()),
            () -> assertEquals(2, loggingEvent.getArguments().size())
        );
    }
}
