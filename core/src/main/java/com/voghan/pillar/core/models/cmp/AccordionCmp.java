package com.voghan.pillar.core.models.cmp;

import com.adobe.cq.export.json.ComponentExporter;
import com.voghan.pillar.core.models.Accordion;
import com.voghan.pillar.core.models.Card;
import com.voghan.pillar.core.models.cfm.AccordionCfm;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = {
                Accordion.class, ComponentExporter.class
        },
        resourceType = AccordionCmp.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AccordionCmp extends BaseModelCmp implements Accordion {
    static final String RESOURCE_TYPE = "pillar/components/accordion/v1/accordion";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Self
    private SlingHttpServletRequest slingHttpServletRequest;

    @ValueMapValue
    private String fragmentPath;

    @ValueMapValue
    private String variationName;

    private AccordionCfm accordionCfm;

    @PostConstruct
    protected void init() {
        logger.debug("Post Construct for {} accordion ... ", fragmentPath);

        if (fragmentPath != null) {
            if (StringUtils.isEmpty(variationName)) {
                variationName = "master";
            }
            Resource resource = slingHttpServletRequest.getResourceResolver().getResource(fragmentPath);
            if (resource != null && resource.getChild("jcr:content/data/" + variationName) != null) {
                accordionCfm = resource.getChild("jcr:content/data/" + variationName).adaptTo(AccordionCfm.class);
            }
        }

    }

    @Override
    public Boolean getSingleExpansion() {
        return accordionCfm != null ? accordionCfm.getSingleExpansion() : Boolean.TRUE;
    }

    @Override
    public List<Card> getItems() {
        return accordionCfm != null ? new ArrayList<>(accordionCfm.getItems()) : new ArrayList<>();
    }

    @Override
    public List<String> getExpandedItems() {
        return accordionCfm != null ? new ArrayList<>(accordionCfm.getExpandedItems()) : new ArrayList<>();
    }

    public String getHeadingElement() {
        return accordionCfm != null ? accordionCfm.getHeadingElement() : "";
    }
}
