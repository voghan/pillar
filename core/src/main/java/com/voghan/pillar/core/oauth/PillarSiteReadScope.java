package com.voghan.pillar.core.oauth;

import com.adobe.granite.oauth.server.Scope;
import com.adobe.granite.oauth.server.ScopeWithPrivileges;
import org.apache.jackrabbit.api.security.user.User;
import org.osgi.service.component.annotations.Component;

import javax.servlet.http.HttpServletRequest;

@Component(service= Scope.class)
public class PillarSiteReadScope implements ScopeWithPrivileges {

    protected static final String RESOURCE_URI = "/content/pillar";
    protected static final String SCOPE_NAME = "pillar_site_read";
    protected static final String DESCRIPTION = "Simple read access scope for site content.";

    @Override
    public String[] getPrivileges() {
        return new String[] {"jcr:read"};
    }

    @Override
    public String getName() {
        return SCOPE_NAME;
    }

    @Override
    public String getResourcePath(User user) {
        return RESOURCE_URI;
    }

    @Override
    public String getEndpoint() {
        return null;
    }

    @Override
    public String getDescription(HttpServletRequest httpServletRequest) {
        return DESCRIPTION;
    }
}
