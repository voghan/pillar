package com.voghan.pillar.core.models.cmp;

import com.adobe.cq.export.json.ComponentExporter;
import com.voghan.pillar.core.models.Card;
import com.voghan.pillar.core.models.CardList;
import org.apache.sling.api.SlingHttpServletRequest;
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
        CardList.class, ComponentExporter.class
    },
    resourceType = CardListCmp.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CardListCmp extends BaseModelCmp implements CardList {
    static final String RESOURCE_TYPE = "pillar/components/card-list/v1/card-list";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Self
    private SlingHttpServletRequest servletRequest;

    @ValueMapValue
    private String fragmentFolderPath;

    private List<Card> cards = new ArrayList<>();

    @PostConstruct
    protected void init() {
        logger.info("Post Construct for {} filters ... ", fragmentFolderPath);

        // TODO build list of cards
        // TODO add filters
    }

    public List<Card> getCards() {
        return  new ArrayList<>(cards);
    }
}
