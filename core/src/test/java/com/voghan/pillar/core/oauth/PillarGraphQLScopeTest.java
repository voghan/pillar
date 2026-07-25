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
class PillarGraphQLScopeTest {

    @Mock
    private User user;

    @Mock
    private HttpServletRequest request;

    // --- PillarGraphQLScope ---

    @Test
    void graphQLScope_getName_returnsScopeName() {
        PillarGraphQLScope scope = new PillarGraphQLScope();
        assertEquals(PillarGraphQLScope.SCOPE_NAME, scope.getName());
    }

    @Test
    void graphQLScope_getPrivileges_returnsJcrRead() {
        PillarGraphQLScope scope = new PillarGraphQLScope();
        assertArrayEquals(new String[]{"jcr:read"}, scope.getPrivileges());
    }

    @Test
    void graphQLScope_getResourcePath_returnsDamPath() {
        PillarGraphQLScope scope = new PillarGraphQLScope();
        assertEquals(PillarGraphQLScope.RESOURCE_URI, scope.getResourcePath(user));
    }

    @Test
    void graphQLScope_getEndpoint_returnsNull() {
        PillarGraphQLScope scope = new PillarGraphQLScope();
        assertNull(scope.getEndpoint());
    }

    @Test
    void graphQLScope_getDescription_returnsNonBlank() {
        PillarGraphQLScope scope = new PillarGraphQLScope();
        String description = scope.getDescription(request);
        assertAll(
            () -> assertNotNull(description),
            () -> assertEquals(PillarGraphQLScope.DESCRIPTION, description)
        );
    }

}
