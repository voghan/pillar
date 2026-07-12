package com.voghan.pillar.common.emails;

import com.day.cq.commons.Externalizer;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.RepositoryException;
import javax.jcr.Value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailUtilTest {

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Externalizer externalizer;

    @Test
    void getUserEmail_whenHappyPath_returnEmail() throws RepositoryException {
        String expected = "bill@ms.com";
        String userId = "billg";
        UserManager userManager = mock(UserManager.class);
        Authorizable authorizable = mock(Authorizable.class);
        Value[] values = new Value[]{mock(Value.class)};
        when(resourceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(userManager.getAuthorizable(userId)).thenReturn(authorizable);
        when(authorizable.getProperty("./profile/email")).thenReturn(values);
        when(values[0].getString()).thenReturn(expected);

        String actual = EmailUtil.getUserEmail(resourceResolver, userId);

        assertEquals(expected, actual);
    }

    @Test
    void getUserEmail_whenUserManagerNull_returnNull() throws RepositoryException {
        String userId = "billg";
        when(resourceResolver.adaptTo(UserManager.class)).thenReturn(null);

        String actual = EmailUtil.getUserEmail(resourceResolver, userId);

        assertNull(actual);
    }

    @Test
    void getUserEmail_whenAuthorizableNull_returnNull() throws RepositoryException {
        String userId = "billg";
        UserManager userManager = mock(UserManager.class);
        when(resourceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(userManager.getAuthorizable(userId)).thenReturn(null);

        String actual = EmailUtil.getUserEmail(resourceResolver, userId);

        assertNull(actual);
    }

    @Test
    void getUserEmail_whenProfileNull_returnNull() throws RepositoryException {
        String userId = "billg";
        UserManager userManager = mock(UserManager.class);
        Authorizable authorizable = mock(Authorizable.class);
        when(resourceResolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(userManager.getAuthorizable(userId)).thenReturn(authorizable);
        when(authorizable.getProperty("./profile/email")).thenReturn(null);

        String actual = EmailUtil.getUserEmail(resourceResolver, userId);

        assertNull(actual);
    }

    @Test
    void getAuthorLink_whenHappyPath_returnExternal() {
        String expected = "http://dev-author.pillar.com/content/page/foo.html";
        String path = "/content/page/foo";
        String externalized = "http://dev-author.pillar.com/content/page/foo";

        when(externalizer.externalLink(resourceResolver, Externalizer.AUTHOR, path)).thenReturn(externalized);

        String actual = EmailUtil.getAuthorLink(resourceResolver, externalizer, path);

        assertEquals(expected, actual);
    }

    @Test
    void getAuthorLink_whenHappyPath_returnLocalHost() {
        String expected = "http://localhost:4502/content/page/foo.html";
        String path = "/content/page/foo";
        String localized = "http://localhost:4502/content/page/foo";

        when(externalizer.externalLink(resourceResolver, Externalizer.AUTHOR, path)).thenReturn(localized);

        String actual = EmailUtil.getAuthorLink(resourceResolver, externalizer, path);

        assertEquals(expected, actual);
    }

    @Test
    void getAuthorLink_whenBadResourceResolver_returnLocalHost() {
        String expected = "http://localhost:4502/content/page/foo.html";
        String path = "/content/page/foo";

        String actual = EmailUtil.getAuthorLink(resourceResolver, externalizer, path);

        assertEquals(expected, actual);
    }

    @Test
    void getAuthorLink_whenBadExternalizer_returnLocalHost() {
        String expected = "http://localhost:4502/content/page/foo.html";
        String path = "/content/page/foo";

        String actual = EmailUtil.getAuthorLink(resourceResolver, null, path);

        assertEquals(expected, actual);
    }

    @Test
    void getAuthorLink_whenBadParams_returnLocalHost() {
        String expected = "http://localhost:4502/content/page/foo.html";
        String path = "/content/page/foo";

        String actual = EmailUtil.getAuthorLink(null, null, path);

        assertEquals(expected, actual);
    }

    @Test
    void getAuthorLink_whenNullReturned_returnLocalHost() {
        String expected = "http://localhost:4502/content/page/foo.html";
        String path = "/content/page/foo";

        when(externalizer.externalLink(resourceResolver, Externalizer.AUTHOR, path)).thenReturn(null);

        String actual = EmailUtil.getAuthorLink(resourceResolver, externalizer, path);

        assertEquals(expected, actual);
    }
}
