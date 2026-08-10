package com.voghan.pillar.common.policies;

import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import org.apache.sling.api.resource.Resource;

public final class PolicyUtils {
    private PolicyUtils() {}

    /**
     * Retrieves the ContentPolicy for the given resource.
     */
    public static ContentPolicy getContentPolicy(Resource resource) {
        ContentPolicyManager cpm = resource.getResourceResolver()
                .adaptTo(ContentPolicyManager.class);
        return cpm != null ? cpm.getPolicy(resource) : null;
    }

    /**
     * Reads a typed property from a content policy.
     */
    public static <T> T getPolicyProperty(ContentPolicy policy,
                                          String property, Class<T> type) {
        if (policy != null) {
            return policy.getProperties().get(property, type);
        }
        return null;
    }

    /**
     * Reads a typed property with a default fallback.
     */
    public static <T> T getPolicyProperty(ContentPolicy policy,
                                          String property, T defaultValue) {
        if (policy != null) {
            return policy.getProperties().get(property, defaultValue);
        }
        return defaultValue;
    }
}
