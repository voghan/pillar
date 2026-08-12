package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.core.models.Image;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ImageCfm extends BaseModelCfm implements Image {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String MODEL = "/conf/pillar/settings/dam/cfm/models/image";

    @Self
    private Resource resource;

    @ValueMapValue
    private String fileReference;

    @ValueMapValue
    private String altText;

    @ValueMapValue
    private String caption;

    @ValueMapValue
    private Boolean decorative;

    @ValueMapValue
    private Boolean lazyEnabled;

    @Override
    public String getFileReference() {
        return fileReference;
    }

    @Override
    public Boolean isLazyEnabled() {
        return lazyEnabled != null ? lazyEnabled : false;
    }

    @Override
    public Boolean isDecorative() {
        return decorative != null ? decorative : false;
    }

    @Override
    public String getCaption() {
        return caption != null ? caption : StringUtils.EMPTY;
    }

    @Override
    public String getAltText() {
        return altText != null ? altText : StringUtils.EMPTY;
    }
}
