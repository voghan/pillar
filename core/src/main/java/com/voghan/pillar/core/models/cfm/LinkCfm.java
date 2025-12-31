package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.core.models.Link;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = Resource.class)
public class LinkCfm implements Link {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ValueMapValue
    private String linkText;

    @ValueMapValue
    private String linkPath;

    @Override
    public String getLinkText() {
        return linkText;
    }

    @Override
    public String getLinkPath() {
        return linkPath;
    }
}
