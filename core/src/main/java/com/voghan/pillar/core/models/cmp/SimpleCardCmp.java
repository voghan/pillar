package com.voghan.pillar.core.models.cmp;

import com.adobe.cq.export.json.ComponentExporter;
import com.voghan.pillar.core.models.Link;
import com.voghan.pillar.core.models.SimpleCard;
import com.voghan.pillar.core.models.cfm.SimpleCardCfm;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = {
        SimpleCard.class, ComponentExporter.class
    },
    resourceType = SimpleCardCmp.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SimpleCardCmp extends BaseModelCmp implements SimpleCard {
    static final String RESOURCE_TYPE = "pillar/components/card/v1/card";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Self
    private SlingHttpServletRequest servletRequest;

    @ValueMapValue
    private String fragmentPath;

    @ValueMapValue
    private String variationName;

    private SimpleCard simpleCard;

    @PostConstruct
    protected void init() {
        logger.info("Post Construct for {} version {}", fragmentPath, variationName);
        simpleCard = new SimpleCardCfm();
        if (servletRequest != null) {
            Resource cfm = servletRequest.getResourceResolver().getResource(fragmentPath + "/jcr:content/data/" + variationName);
            if (cfm != null) {
                logger.info("Found resource at {} ", cfm.getPath());
                simpleCard = cfm.adaptTo(SimpleCardCfm.class);
            }
        }
    }

    @Override
    public String getHeadline() {
        return simpleCard.getHeadline();
    }

    @Override
    public String getShortDescription() {
        return simpleCard.getShortDescription();
    }

    @Override
    public List<Link> getCallToActions() {
        return simpleCard.getCallToActions();
    }

    @Override
    public String getImage() {
        return simpleCard.getImage();
    }

    @Override
    public @NotNull String getExportedType() {
        return RESOURCE_TYPE;
    }

    public boolean isCallToActionEnabled() {
        return simpleCard.getCallToActions().size() > 0;
    }
}
