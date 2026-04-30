package com.voghan.pillar.common.emails.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.day.cq.commons.mail.MailTemplate;
import com.day.cq.mailer.MailingException;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jcr.Session;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SimpleEmailServiceImplTest {

    private static final String TEMPLATE_PATH = "/conf/pillar-common/notifications/email/demo-email.html";
    private static final String MAIL_TO = "no-reply@example.com";

    private final TestLogger logger = TestLoggerFactory.getTestLogger(SimpleEmailServiceImpl.class);

    @Mock private MessageGatewayService messageGatewayService;
    @Mock private ResourceResolverFactory resourceResolverFactory;
    @Mock private ResourceResolver resourceResolver;
    @Mock private Session session;
    @Mock private MailTemplate mailTemplate;
    @Mock private HtmlEmail htmlEmail;
    @Mock private MessageGateway<HtmlEmail> messageGateway;
    @Mock private Resource templateResource;

    @InjectMocks
    private SimpleEmailServiceImpl emailService;

    private Map<String, String> params;

    @BeforeEach
    void setup() throws LoginException {
        TestLoggerFactory.clear();

        // Common stubs used by most tests; lenient because some tests override them
        lenient().when(resourceResolverFactory.getServiceResourceResolver(any()))
                .thenReturn(resourceResolver);
        lenient().when(resourceResolver.getResource(TEMPLATE_PATH)).thenReturn(templateResource);
        lenient().when(resourceResolver.adaptTo(Session.class)).thenReturn(session);
        lenient().when(messageGatewayService.getGateway(HtmlEmail.class)).thenReturn(messageGateway);

        params = new HashMap<>();
        params.put("givenName", "Wally");
        params.put("authorLink", "http://localhost:4502/aem/start.html");
        params.put("initiator", "Brian");
    }

    // -------------------------------------------------------------------------
    // Happy path
    // -------------------------------------------------------------------------

    @Test
    void sendEmail_sendsEmailSuccessfully() throws Exception {
        when(mailTemplate.getEmail(eq(params), eq(HtmlEmail.class))).thenReturn(htmlEmail);

        try (MockedStatic<MailTemplate> mockedMailTemplate = mockStatic(MailTemplate.class)) {
            mockedMailTemplate.when(() -> MailTemplate.create(TEMPLATE_PATH, session))
                    .thenReturn(mailTemplate);

            emailService.sendEmail(MAIL_TO, TEMPLATE_PATH, params);

            verify(htmlEmail).addTo(MAIL_TO);
            verify(messageGateway).send(htmlEmail);
            assertEquals(0, logger.getLoggingEvents().size());
        }
    }

    // -------------------------------------------------------------------------
    // Guard conditions
    // -------------------------------------------------------------------------

    @Test
    void sendEmail_logsWarnAndAborts_whenTemplateResourceNotFound() {
        // Override the default stub to simulate missing template
        when(resourceResolver.getResource(TEMPLATE_PATH)).thenReturn(null);

        emailService.sendEmail(MAIL_TO, TEMPLATE_PATH, params);

        // MailTemplate.create() must never be reached
        List<LoggingEvent> events = logger.getLoggingEvents();
        assertAll(
            () -> assertEquals(1, events.size()),
            () -> assertEquals(Level.WARN, events.get(0).getLevel()),
            () -> assertEquals(1, events.get(0).getArguments().size()),
            () -> assertEquals(TEMPLATE_PATH, events.get(0).getArguments().get(0))
        );
    }

    // -------------------------------------------------------------------------
    // Error handling
    // -------------------------------------------------------------------------

    @Test
    void sendEmail_logsWarn_whenLoginExceptionThrown() throws LoginException {
        when(resourceResolverFactory.getServiceResourceResolver(any()))
                .thenThrow(new LoginException("no service user"));

        emailService.sendEmail(MAIL_TO, TEMPLATE_PATH, params);

        List<LoggingEvent> events = logger.getLoggingEvents();
        assertAll(
            () -> assertEquals(1, events.size()),
            () -> assertEquals(Level.WARN, events.get(0).getLevel()),
            () -> assertEquals(MAIL_TO, events.get(0).getArguments().get(0))
        );
    }

    @Test
    void sendEmail_logsWarn_whenEmailExceptionThrown() throws Exception {
        when(mailTemplate.getEmail(eq(params), eq(HtmlEmail.class)))
                .thenThrow(new EmailException("bad address"));

        try (MockedStatic<MailTemplate> mockedMailTemplate = mockStatic(MailTemplate.class)) {
            mockedMailTemplate.when(() -> MailTemplate.create(TEMPLATE_PATH, session))
                    .thenReturn(mailTemplate);

            emailService.sendEmail(MAIL_TO, TEMPLATE_PATH, params);

            verify(messageGateway, never()).send(any());

            List<LoggingEvent> events = logger.getLoggingEvents();
            assertAll(
                () -> assertEquals(1, events.size()),
                () -> assertEquals(Level.WARN, events.get(0).getLevel()),
                () -> assertEquals(MAIL_TO, events.get(0).getArguments().get(0))
            );
        }
    }

    @Test
    void sendEmail_propagatesMailingException_whenGatewayFails() throws Exception {
        // MailingException extends RuntimeException, not MessagingException,
        // so it is NOT caught by the production catch block and propagates to the caller.
        when(mailTemplate.getEmail(eq(params), eq(HtmlEmail.class))).thenReturn(htmlEmail);
        doThrow(new MailingException("smtp error")).when(messageGateway).send(htmlEmail);

        try (MockedStatic<MailTemplate> mockedMailTemplate = mockStatic(MailTemplate.class)) {
            mockedMailTemplate.when(() -> MailTemplate.create(TEMPLATE_PATH, session))
                    .thenReturn(mailTemplate);

            org.junit.jupiter.api.Assertions.assertThrows(
                MailingException.class,
                () -> emailService.sendEmail(MAIL_TO, TEMPLATE_PATH, params)
            );
        }
    }

    @Test
    void sendEmail_logsWarn_whenIOExceptionThrown() throws Exception {
        when(mailTemplate.getEmail(eq(params), eq(HtmlEmail.class)))
                .thenThrow(new IOException("template read error"));

        try (MockedStatic<MailTemplate> mockedMailTemplate = mockStatic(MailTemplate.class)) {
            mockedMailTemplate.when(() -> MailTemplate.create(TEMPLATE_PATH, session))
                    .thenReturn(mailTemplate);

            emailService.sendEmail(MAIL_TO, TEMPLATE_PATH, params);

            verify(messageGateway, never()).send(any());

            List<LoggingEvent> events = logger.getLoggingEvents();
            assertAll(
                () -> assertEquals(1, events.size()),
                () -> assertEquals(Level.WARN, events.get(0).getLevel()),
                () -> assertEquals(MAIL_TO, events.get(0).getArguments().get(0))
            );
        }
    }
}
