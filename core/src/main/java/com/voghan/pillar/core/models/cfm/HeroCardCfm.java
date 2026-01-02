package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.core.models.HeroCard;
import com.voghan.pillar.core.models.Link;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeroCardCfm extends CardCfm implements HeroCard {

    @Self
    private Resource resource;

    @ValueMapValue
    private List<String> breadcrumbs;

    private final List<Link> breadcrumbLinks = new ArrayList<>();

    @ValueMapValue
    private String backgroundImage;

    @PostConstruct
    protected void init() {

        String version = getVersion();

        buildCallToActions();

        if (breadcrumbs != null && resource != null) {
            ResourceResolver resourceResolver = resource.getResourceResolver();
            for (String path: breadcrumbs) {
                Resource cta  = resourceResolver.getResource(path + "/jcr:content/data/" + version);
                if (cta != null) {
                    Link link = cta.adaptTo(LinkCfm.class);
                    breadcrumbLinks.add(link);
                }
            }
        }
    }

    @Override
    public List<Link> getBreadcrumbs() {
        return new ArrayList<>(breadcrumbLinks);
    }

    @Override
    public String getBackgroundImage() {
        return backgroundImage;
    }

}
