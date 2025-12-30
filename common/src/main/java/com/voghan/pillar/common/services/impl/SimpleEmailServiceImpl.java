package com.voghan.pillar.common.services.impl;

import com.day.cq.commons.mail.MailTemplate;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.voghan.pillar.common.services.SimpleEmailService;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(
    immediate = true,
    service = SimpleEmailService.class,
    property = {
        Constants.SERVICE_ID + "=Simple Email Service",
        Constants.SERVICE_DESCRIPTION + "=Pillar email service using SMTP"
    }
)
public class SimpleEmailServiceImpl implements SimpleEmailService {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String SERVICE_NAME = "SimpleEmailService";

    @Reference
    private MessageGatewayService messageGatewayService;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void sendEmail(String mailTo, String templatePath, Map<String, String> parameters) {

        final Map<String, Object> authInfo = new HashMap<>();
        authInfo.put(ResourceResolverFactory.SUBSERVICE, SERVICE_NAME);
        try(ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authInfo)) {
            MessageGateway<HtmlEmail> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);

            if (resourceResolver.getResource(templatePath) == null) {
                LOGGER.warn("Email template does not exist <{}>, aborting email", templatePath);
                return;
            }

            Session session = resourceResolver.adaptTo(Session.class);
            final MailTemplate mailTemplate = MailTemplate.create(templatePath, session);
            HtmlEmail email = mailTemplate.getEmail(StrLookup.mapLookup(parameters), HtmlEmail.class);
            email.addTo(mailTo);
            messageGateway.send(email);
        } catch (EmailException | MessagingException | IOException | LoginException e) {
            LOGGER.warn("Failed to send email to {}", mailTo, e);
        }
    }
}
