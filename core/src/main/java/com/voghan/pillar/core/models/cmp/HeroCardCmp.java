package com.voghan.pillar.core.models.cmp;

import com.adobe.cq.export.json.ComponentExporter;
import com.voghan.pillar.core.models.HeroCard;
import com.voghan.pillar.core.models.Link;
import com.voghan.pillar.core.models.cfm.HeroCardCfm;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = {
        HeroCard.class, ComponentExporter.class
    },
    resourceType = HeroCardCmp.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeroCardCmp extends BaseModelCmp implements HeroCard {
    static final String RESOURCE_TYPE = "pillar/components/hero/v1/hero";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Self
    private SlingHttpServletRequest servletRequest;

    @ValueMapValue
    private String fragmentPath;

    @ValueMapValue
    private String variationName;

    private HeroCard heroCard;

    @PostConstruct
    protected void init() {
        logger.info("Post Construct for {} version {}", fragmentPath, variationName);
        heroCard = new HeroCardCfm();
        if (StringUtils.isEmpty(variationName)) {
            variationName = "master";
        }
        if (servletRequest != null) {
            Resource cfm = servletRequest.getResourceResolver().getResource(fragmentPath + "/jcr:content/data/" + variationName);
            if (cfm != null) {
                logger.info("Found resource at {} ", cfm.getPath());
                heroCard = cfm.adaptTo(HeroCardCfm.class);
            }
        }
    }

    @Override
    public String getHeadline() {
        return heroCard.getHeadline();
    }

    @Override
    public String getShortDescription() {
        return heroCard.getShortDescription();
    }

    @Override
    public List<Link> getCallToActions() {
        return heroCard.getCallToActions();
    }

    @Override
    public List<Link> getBreadcrumbs() {
        return heroCard.getBreadcrumbs();
    }

    @Override
    public String getBackgroundImage() {
        return heroCard.getBackgroundImage();
    }

    public boolean isCallToActionEnabled() {
        return heroCard.getCallToActions().size() > 0;
    }

    public boolean isBreadcrumbsEnabled() {
        return heroCard.getBreadcrumbs().size() > 0;
    }

    @Override
    public @NotNull String getExportedType() {
        return RESOURCE_TYPE;
    }
}
