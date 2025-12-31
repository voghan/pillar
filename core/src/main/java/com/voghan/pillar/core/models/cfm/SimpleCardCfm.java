package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.core.models.SimpleCard;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SimpleCardCfm extends CardCfm implements SimpleCard {

    private static final String master = "master";

    @Self
    private Resource resource;

    @ValueMapValue
    private String image;

    @PostConstruct
    protected void init() {
        buildCallToActions();
    }

    @Override
    public String getImage() {
        return image;
    }

}
