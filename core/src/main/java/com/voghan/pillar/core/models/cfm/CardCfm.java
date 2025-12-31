package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.core.models.Card;
import com.voghan.pillar.core.models.Link;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CardCfm implements Card {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String master = "master";

    @Self
    private Resource resource;

    @ValueMapValue
    private String headline;

    @ValueMapValue
    private String shortDescription;

    @ValueMapValue
    private List<String> callToActions;

    private final List<Link> callToActionLinks = new ArrayList<>();

    @PostConstruct
    protected void init() {

        buildCallToActions();
    }

    protected void buildCallToActions() {
        logger.info("building callToActions for {}", resource.getPath());
        String version = getVersion();

        if (callToActions != null && resource != null) {
            ResourceResolver resourceResolver = resource.getResourceResolver();
            for (String path: callToActions) {
                Resource cta  = resourceResolver.getResource(path + "/jcr:content/data/" + version);
                if (cta != null) {
                    Link link = cta.adaptTo(LinkCfm.class);
                    callToActionLinks.add(link);
                }
            }
        }
    }

    @Override
    public String getHeadline() {
        return headline;
    }

    @Override
    public String getShortDescription() {
        return shortDescription;
    }

    @Override
    public List<Link> getCallToActions() {
        return callToActionLinks;
    }

    protected String getVersion() {
        if (resource != null) {
            return resource.getName();
        }
        return master;
    }
}
