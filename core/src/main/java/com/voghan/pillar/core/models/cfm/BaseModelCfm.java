package com.voghan.pillar.core.models.cfm;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BaseModelCfm {
    private static final String master = "master";

    @Self
    private Resource resource;

    protected String getVersion() {
        if (resource != null) {
            return resource.getName();
        }
        return master;
    }
}
