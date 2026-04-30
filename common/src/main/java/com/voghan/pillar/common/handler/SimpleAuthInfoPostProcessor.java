package com.voghan.pillar.common.handler;

import com.voghan.pillar.common.AuthUtil;
import java.util.Calendar;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.auth.core.spi.AuthenticationInfo;
import org.apache.sling.auth.core.spi.AuthenticationInfoPostProcessor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(service = AuthenticationInfoPostProcessor.class, immediate = true)
@ServiceDescription("Records the last login timestamp on the user profile node")
@ServiceRanking(100)
public class SimpleAuthInfoPostProcessor implements AuthenticationInfoPostProcessor {

  private final Logger LOGGER = LoggerFactory.getLogger(SimpleAuthInfoPostProcessor.class);

  protected static final String SERVICE_NAME = "SimpleAuthInfoPostProcessor";

  public static final String PROPERTY_LAST_LOGIN = "profile/lastLogin";

  @Reference
  private ResourceResolverFactory resolverFactory;

  @Override
  public void postProcess(AuthenticationInfo authInfo, HttpServletRequest request, HttpServletResponse response) {

    final String userId = authInfo.getUser();
    if (userId == null || userId.isEmpty()) {
      LOGGER.trace("UserLastLoginPostProcessor: no user in AuthenticationInfo, skipping");
      return;
    }

    writeLastLogin(userId);

  }

  protected void writeLastLogin(final String userId) {
    try (ResourceResolver resourceResolver = resolverFactory.getServiceResourceResolver(AuthUtil.getAuthInfo(SERVICE_NAME))) {
      final Session session = resourceResolver.adaptTo(Session.class);
      if (session == null) {
        LOGGER.error("UserLastLoginPostProcessor: could not adapt ResourceResolver to Session");
        return;
      }

      final UserManager userManager = ((JackrabbitSession)session).getUserManager();
      if (userManager == null) {
        LOGGER.error("UserLastLoginPostProcessor: could not adapt ResourceResolver to UserManager");
        return;
      }
      final Authorizable authorizable = userManager.getAuthorizable(userId);

      if (authorizable == null || authorizable.isGroup()) {
        LOGGER.debug("UserLastLoginPostProcessor: no user found for id '{}', skipping", userId);
        return;
      }
      ValueFactory valueFactory = session.getValueFactory();
      authorizable.setProperty(PROPERTY_LAST_LOGIN, valueFactory.createValue(Calendar.getInstance()));
      session.save();

      LOGGER.debug("UserLastLoginPostProcessor: recorded lastLogin for user '{}'", userId);

    } catch (RepositoryException | LoginException e) {
      LOGGER.error("UserLastLoginPostProcessor: failed to write lastLogin for user '{}'", userId, e);
    }
  }



}
