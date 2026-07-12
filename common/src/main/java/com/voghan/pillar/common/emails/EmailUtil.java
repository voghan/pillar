package com.voghan.pillar.common.emails;

import com.day.cq.commons.Externalizer;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RepositoryException;

public class EmailUtil {

    public static String getUserEmail(ResourceResolver resourceResolver, String userId) throws RepositoryException {
        String email = null;
        UserManager userManager = resourceResolver.adaptTo(UserManager.class);
        if (userManager == null) {
            return email;
        }
        Authorizable authorizable = userManager.getAuthorizable(userId);

        if (authorizable != null && authorizable.getProperty("./profile/email") != null) {
            email = authorizable.getProperty("./profile/email")[0].getString();
        }
        return email;
    }

    public static String getAuthorLink(ResourceResolver resourceResolver, Externalizer externalizer, String path) {
        String defaultUrl = "http://localhost:4502" + path;
        if (externalizer == null || resourceResolver == null) {
            return defaultUrl + ".html";
        }

        String url = externalizer.externalLink(resourceResolver, Externalizer.AUTHOR, path);
        if (url == null) {
            url = defaultUrl;
        }
        return  url + ".html";
    }
}
