package com.voghan.pillar.core.models.cmp;

import com.adobe.cq.export.json.ComponentExporter;
import com.voghan.pillar.common.queries.SimpleQueryBuilder;
import com.voghan.pillar.core.models.Card;
import com.voghan.pillar.core.models.CardList;
import com.voghan.pillar.core.models.CardListConfig;
import com.voghan.pillar.core.models.cfm.CardCfm;
import com.voghan.pillar.core.models.cfm.CardListConfigCfm;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private SlingHttpServletRequest slingHttpServletRequest;

    @OSGiService
    private SimpleQueryBuilder simpleQueryBuilder;

    @ValueMapValue
    private String fragmentPath;

    private CardListConfig cardListConfig;

    private List<Card> cards = new ArrayList<>();

    private List<String> filterOptions = new ArrayList<>();

    @PostConstruct
    protected void init() {
        logger.info("Post Construct for {} filters ... ", fragmentPath);

        if (fragmentPath != null) {
            Resource resource = slingHttpServletRequest.getResourceResolver().getResource(fragmentPath);
            if (resource != null && resource.getChild("jcr:content/data/master") != null){
                cardListConfig = resource.getChild("jcr:content/data/master").adaptTo(CardListConfigCfm.class);
            }
        }

        if (cardListConfig != null && simpleQueryBuilder != null) {
            searchForCards(cardListConfig);
            // TODO add filters
        } else {
            logger.info("Component is not ready filter:{} queryBuilder:" + simpleQueryBuilder, fragmentPath);
        }
    }

    protected void searchForCards(CardListConfig cardListConfig) {
        Map<String, String> map = buildQuery(cardListConfig);
        List<Resource> resources = simpleQueryBuilder.search(slingHttpServletRequest, map);

        //hydrate
        for (Resource resource : resources) {
            Card card = resource.getChild("data/master").adaptTo(CardCfm.class);
            if (card != null) {
                cards.add(card);
            }
        }
    }

    @NotNull
    private Map<String, String> buildQuery(CardListConfig cardListConfig) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("path", cardListConfig.getSearchPath());
        map.put("type", "dam:AssetContent");
        int i = 0;
        for(String tagId : cardListConfig.getCardTags()) {
            map.put(i +"_tagid", tagId);
            map.put(i +"_tagid.property", "metadata/cq:tags");
        }
        map.put("p.limit","25");
        return map;
    }

    public List<Card> getCards() {
        return  new ArrayList<>(cards);
    }

    public String getHeadline() {
        return (cardListConfig != null) ? cardListConfig.getHeadline() : null;
    }

    public List<String> getFilterOptions() {
        return filterOptions;
    }

    public String getShortDescription() {
        return (cardListConfig != null) ? cardListConfig.getShortDescription() : null;
    }

    public Boolean isEnableSearch() {
        return (cardListConfig != null) ? cardListConfig.getEnableSearch() : false;
    }

    @Override
    public @NotNull String getExportedType() {
        return RESOURCE_TYPE;
    }
}
