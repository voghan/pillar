package com.voghan.pillar.core.models.cfm;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.Nullable;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BaseModelCfm {

    private static final String master = "master";
    public static final String JCR_CONTENT_DATA = "/jcr:content/data/";

    @Self
    private Resource resource;

    private String id;

    private String name;

    @PostConstruct
    protected void initModel() {
        id = UUID.randomUUID().toString();
        if (resource != null) {
            name = resource.getPath().replaceFirst(JCR_CONTENT_DATA + resource.getName(), "" );
        }
    }

    @Nullable
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    protected String getVersion() {
        if (resource != null) {
            return resource.getName();
        }
        return master;
    }
}
