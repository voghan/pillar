package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.core.models.Separator;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SeparatorCfm extends BaseModelCfm implements Separator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String MODEL = "/conf/pillar/settings/dam/cfm/models/separator";

    @Self
    private Resource resource;

    @ValueMapValue
    private Boolean decorative;

    @ValueMapValue
    private String spacing;

    @ValueMapValue
    private String color;

    @Override
    public Boolean isDecorative() {
        return decorative != null ? decorative : false;
    }

    @Override
    public String getSpacing() {
        return spacing != null ? spacing : StringUtils.EMPTY;
    }

    @Override
    public String getColor() {
        return color != null ? color : StringUtils.EMPTY;
    }
}
