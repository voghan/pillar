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
class PillarAssetsReadScopeTest {

    @Mock
    private User user;

    @Mock
    private HttpServletRequest request;

    // --- PillarAssetsReadScope ---

    @Test
    void assetsScope_getName_returnsScopeName() {
        PillarAssetsReadScope scope = new PillarAssetsReadScope();
        assertEquals(PillarAssetsReadScope.SCOPE_NAME, scope.getName());
    }

    @Test
    void assetsScope_getPrivileges_returnsJcrRead() {
        PillarAssetsReadScope scope = new PillarAssetsReadScope();
        assertArrayEquals(new String[]{"jcr:read"}, scope.getPrivileges());
    }

    @Test
    void assetsScope_getResourcePath_returnsDamPath() {
        PillarAssetsReadScope scope = new PillarAssetsReadScope();
        assertEquals(PillarAssetsReadScope.RESOURCE_URI, scope.getResourcePath(user));
    }

    @Test
    void assetsScope_getEndpoint_returnsNull() {
        PillarAssetsReadScope scope = new PillarAssetsReadScope();
        assertNull(scope.getEndpoint());
    }

    @Test
    void assetsScope_getDescription_returnsNonBlank() {
        PillarAssetsReadScope scope = new PillarAssetsReadScope();
        String description = scope.getDescription(request);
        assertAll(
            () -> assertNotNull(description),
            () -> assertEquals(PillarAssetsReadScope.DESCRIPTION, description)
        );
    }

}
