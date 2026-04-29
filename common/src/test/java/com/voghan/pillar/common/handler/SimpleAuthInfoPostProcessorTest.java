package com.voghan.pillar.common.handler;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.voghan.pillar.common.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import java.util.Calendar;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.auth.core.spi.AuthenticationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SimpleAuthInfoPostProcessorTest {

    private static final AemContext context = AppAemContext.newAemContext();

    private static final String TEST_USER_ID = "testUser";

    private final TestLogger logger = TestLoggerFactory.getTestLogger(SimpleAuthInfoPostProcessor.class);

    @Mock
    private ResourceResolverFactory resolverFactory;

    @Mock
    private ResourceResolver serviceResolver;

    @Mock
    private Session jackrabbitSession;

    @Mock
    private UserManager userManager;

    @Mock
    private User user;

    @Mock
    private Group group;

    @Mock
    private ValueFactory valueFactory;

    @Mock
    private Value calendarValue;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private SimpleAuthInfoPostProcessor fixture;

    @BeforeEach
    void setup() throws Exception {
        TestLoggerFactory.clear();
    }

    @Test
    void postProcess_writesLastLoginProperty() throws Exception {
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResolver);
        when(serviceResolver.adaptTo(Session.class)).thenReturn(jackrabbitSession);
        when(serviceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(userManager.getAuthorizable(TEST_USER_ID)).thenReturn(user);
        when(user.isGroup()).thenReturn(false);
        when(jackrabbitSession.getValueFactory()).thenReturn(valueFactory);
        when(valueFactory.createValue(any(Calendar.class))).thenReturn(calendarValue);


        fixture.postProcess(authInfo(TEST_USER_ID), request, response);

        verify(user).setProperty(eq(SimpleAuthInfoPostProcessor.PROPERTY_LAST_LOGIN), eq(calendarValue));
        verify(jackrabbitSession).save();
    }

    @Test
    void postProcess_skipsWhenUserIdIsNull() throws Exception {
        fixture.postProcess(authInfo(null), request, response);

        verify(resolverFactory, never()).getServiceResourceResolver(any());
        verify(jackrabbitSession, never()).save();

        assertAll(
            () -> assertEquals(1, logger.getLoggingEvents().size()),
            () -> assertEquals(Level.DEBUG, logger.getLoggingEvents().get(0).getLevel())
        );
    }

    @Test
    void postProcess_skipsWhenUserIdIsEmpty() throws Exception {
        fixture.postProcess(authInfo(""), request, response);

        verify(resolverFactory, never()).getServiceResourceResolver(any());
        verify(jackrabbitSession, never()).save();
    }

    @Test
    void postProcess_skipsWhenAuthorizableIsNull() throws Exception {
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResolver);
        when(serviceResolver.adaptTo(Session.class)).thenReturn(jackrabbitSession);
        when(serviceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(userManager.getAuthorizable(TEST_USER_ID)).thenReturn(null);

        fixture.postProcess(authInfo(TEST_USER_ID), request, response);

        verify(jackrabbitSession, never()).save();

        assertAll(
            () -> assertEquals(1, logger.getLoggingEvents().size()),
            () -> assertEquals(Level.DEBUG, logger.getLoggingEvents().get(0).getLevel())
        );
    }

    @Test
    void postProcess_skipsWhenAuthorizableIsGroup() throws Exception {
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResolver);
        when(serviceResolver.adaptTo(Session.class)).thenReturn(jackrabbitSession);
        when(serviceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(userManager.getAuthorizable(TEST_USER_ID)).thenReturn(group);
        when(group.isGroup()).thenReturn(true);

        fixture.postProcess(authInfo(TEST_USER_ID), request, response);

        verify(jackrabbitSession, never()).save();
    }

    @Test
    void postProcess_skipsWhenSessionIsNull() throws Exception {
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResolver);
        when(serviceResolver.adaptTo(Session.class)).thenReturn(null);

        fixture.postProcess(authInfo(TEST_USER_ID), request, response);

        verify(jackrabbitSession, never()).save();

        assertAll(
            () -> assertEquals(1, logger.getLoggingEvents().size()),
            () -> assertEquals(Level.ERROR, logger.getLoggingEvents().get(0).getLevel())
        );
    }

    @Test
    void postProcess_logsErrorWhenServiceResolverUnavailable() throws Exception {
        when(resolverFactory.getServiceResourceResolver(any())).thenThrow(new LoginException("no mapping"));

        fixture.postProcess(authInfo(TEST_USER_ID), request, response);

        verify(jackrabbitSession, never()).save();

        assertAll(
            () -> assertEquals(1, logger.getLoggingEvents().size()),
            () -> assertEquals(Level.ERROR, logger.getLoggingEvents().get(0).getLevel())
        );
    }

    @Test
    void postProcess_logsErrorOnRepositoryException() throws Exception {
        when(resolverFactory.getServiceResourceResolver(anyMap())).thenReturn(serviceResolver);
        when(serviceResolver.adaptTo(Session.class)).thenReturn(jackrabbitSession);
        when(serviceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(userManager.getAuthorizable(TEST_USER_ID)).thenThrow(new javax.jcr.RepositoryException("repo error"));

        fixture.postProcess(authInfo(TEST_USER_ID), request, response);

        verify(jackrabbitSession, never()).save();

        assertAll(
            () -> assertEquals(1, logger.getLoggingEvents().size()),
            () -> assertEquals(Level.ERROR, logger.getLoggingEvents().get(0).getLevel())
        );
    }

    private static AuthenticationInfo authInfo(final String userId) {
        AuthenticationInfo info = new AuthenticationInfo("TEST");
        if (userId != null) {
            info.setUser(userId);
        }
        return info;
    }
}
