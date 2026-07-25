package com.voghan.pillar.core.oauth;

import org.apache.jackrabbit.api.security.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class PillarSiteReadScopeTest {

    @Mock
    private User user;

    @Mock
    private HttpServletRequest request;

    // --- PillarSiteReadScope ---

    @Test
    void siteScope_getName_returnsScopeName() {
        PillarSiteReadScope scope = new PillarSiteReadScope();
        assertEquals(PillarSiteReadScope.SCOPE_NAME, scope.getName());
    }

    @Test
    void siteScope_getPrivileges_returnsJcrRead() {
        PillarSiteReadScope scope = new PillarSiteReadScope();
        assertArrayEquals(new String[]{"jcr:read"}, scope.getPrivileges());
    }

    @Test
    void siteScope_getResourcePath_returnsSitePath() {
        PillarSiteReadScope scope = new PillarSiteReadScope();
        assertEquals(PillarSiteReadScope.RESOURCE_URI, scope.getResourcePath(user));
    }

    @Test
    void siteScope_getEndpoint_returnsNull() {
        PillarSiteReadScope scope = new PillarSiteReadScope();
        assertNull(scope.getEndpoint());
    }

    @Test
    void siteScope_getDescription_returnsNonBlank() {
        PillarSiteReadScope scope = new PillarSiteReadScope();
        String description = scope.getDescription(request);
        assertAll(
            () -> assertNotNull(description),
            () -> assertEquals(PillarSiteReadScope.DESCRIPTION, description)
        );
    }

}
