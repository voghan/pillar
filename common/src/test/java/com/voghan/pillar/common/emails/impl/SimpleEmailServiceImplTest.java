package com.voghan.pillar.common.emails.impl;

import com.day.cq.commons.mail.MailTemplate;
import com.day.cq.mailer.MessageGatewayService;
import com.voghan.pillar.common.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.core.classloader.annotations.PrepareForTest;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
@PrepareForTest(MailTemplate.class)
public class SimpleEmailServiceImplTest {
    private static final AemContext context = AppAemContext.newAemContext();

    private TestLogger logger = TestLoggerFactory.getTestLogger(SimpleEmailServiceImpl.class);

    @Mock
    MessageGatewayService messageGatewayService;

    @Mock
    ResourceResolverFactory resourceResolverFactory;

    @Mock
    ResourceResolver resourceResolver;

    @Mock
    Session session;

    @InjectMocks
    private SimpleEmailServiceImpl emailService;

    @BeforeEach
    void setup() throws LoginException {
        TestLoggerFactory.clear();

        Map<String, Object> expectedParams = Collections.singletonMap(
            ResourceResolverFactory.SUBSERVICE, (Object) SimpleEmailServiceImpl.SERVICE_NAME);
        when(resourceResolverFactory.getServiceResourceResolver(expectedParams)).thenReturn(resourceResolver);

    }

    @Test
    void sendEmail() throws LoginException, RepositoryException, IOException {
        String template ="/conf/pillar-common/notifications/email/demo-email.html";
        String mailTo = "bfvaughn@gmail.com";

        Map<String, String> params = new HashMap<>();
        params.put("givenName", "Wally");
        params.put("authorLink", "http://localhost:4502/aem/start.html");
        params.put("initiator","Brian");

        //TODO Need to resolve issue with MailTemplate
//        Node node = mock(Node.class);
//        NodeType nodeType = mock(NodeType.class);
//        when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
//        when(resourceResolver.getResource(template)).thenReturn(mock(Resource.class));
//        when(session.itemExists(template)).thenReturn(true);
//        when(session.getNode(template)).thenReturn(node);
//        when(node.getPrimaryNodeType()).thenReturn(nodeType);
//        when(nodeType.getName()).thenReturn("nt:file");

        emailService.sendEmail(mailTo, template, params);

        List<LoggingEvent> loggingEvents = logger.getLoggingEvents();
        assertEquals(1, loggingEvents.size());
        LoggingEvent loggingEvent = loggingEvents.get(0);

        assertAll(
            () -> assertEquals(Level.WARN, loggingEvent.getLevel()),
            () -> assertEquals(1, loggingEvent.getArguments().size())
        );
    }
}
