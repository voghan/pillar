package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.common.links.model.SimpleLink;
import com.voghan.pillar.core.models.Accordion;
import com.voghan.pillar.core.models.Card;
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
public class AccordionCfm extends BaseModelCfm implements Accordion {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String MODEL = "/conf/pillar/settings/dam/cfm/models/accordionconfig";

    @Self
    private Resource resource;

    @ValueMapValue
    private String headingElement;

    @ValueMapValue
    private Boolean singleExpansion;

    @ValueMapValue
    private List<String> accordionItems;

    @ValueMapValue
    private List<String> expandedItems;

    private final List<Card> items = new ArrayList<>();

    @PostConstruct
    protected void init() {
        logger.debug("Building a Card List Config mode for {}", resource.getPath());
        buildItems();

        if (expandedItems == null) {
            expandedItems = new ArrayList<>();
        }
    }

    protected void buildItems() {
        logger.info("building accordion items for {}", resource.getPath());
        String version = getVersion();

        if (accordionItems != null && resource != null) {
            ResourceResolver resourceResolver = resource.getResourceResolver();
            for (String path : accordionItems) {
                Resource card = resourceResolver.getResource(path + "/jcr:content/data/" + version);
                if (card != null) {
                    SimpleCardCfm item = card.adaptTo(SimpleCardCfm.class);
                    items.add(item);
                }
            }
        }
    }

    @Override
    public String getHeadingElement() {
        return headingElement;
    }

    public Boolean getSingleExpansion() {
        return singleExpansion;
    }

    public List<Card> getItems() {
        return new ArrayList<>(items);
    }

    public List<String> getExpandedItems() {
        return new ArrayList<>(expandedItems);
    }
}
