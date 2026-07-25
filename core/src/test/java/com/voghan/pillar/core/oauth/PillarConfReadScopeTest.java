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
class PillarConfReadScopeTest {

    @Mock
    private User user;

    @Mock
    private HttpServletRequest request;

    // --- PillarConfReadScope ---

    @Test
    void confScope_getName_returnsScopeName() {
        PillarConfReadScope scope = new PillarConfReadScope();
        assertEquals(PillarConfReadScope.SCOPE_NAME, scope.getName());
    }

    @Test
    void confScope_getPrivileges_returnsJcrRead() {
        PillarConfReadScope scope = new PillarConfReadScope();
        assertArrayEquals(new String[]{"jcr:read"}, scope.getPrivileges());
    }

    @Test
    void confScope_getResourcePath_returnsConfPath() {
        PillarConfReadScope scope = new PillarConfReadScope();
        assertEquals(PillarConfReadScope.RESOURCE_URI, scope.getResourcePath(user));
    }

    @Test
    void confScope_getEndpoint_returnsNull() {
        PillarConfReadScope scope = new PillarConfReadScope();
        assertNull(scope.getEndpoint());
    }

    @Test
    void confScope_getDescription_returnsNonBlank() {
        PillarConfReadScope scope = new PillarConfReadScope();
        String description = scope.getDescription(request);
        assertAll(
            () -> assertNotNull(description),
            () -> assertEquals(PillarConfReadScope.DESCRIPTION, description)
        );
    }
}
