package com.voghan.pillar.core.models.cmp;

import com.voghan.pillar.common.queries.SimpleQueryBuilder;
import com.voghan.pillar.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class CardListCmpTest {
    private static final AemContext context = AppAemContext.newAemContext();

    private static final String DEMO_CARD_LIST_PAGE_PATH = "/pillar-core/model/cmp/cardListCmps.json";
    private static final String DEMO_CARD_LIST_PATH = "/pillar-core/model/cfm/cardListConfigs.json";
    private static final String DEMO_BASIC_CARD_PATH = "/pillar-core/model/cfm/basicCards.json";
    private static final String DEMO_LINKS_PATH = "/pillar-core/model/cfm/links.json";

    @Mock
    SimpleQueryBuilder simpleQueryBuilder;

    @InjectMocks
    CardListCmp cardListCmp;

    final List<Resource> cards = new ArrayList<>();

    @BeforeAll
    static void setupAll() {
        // Load context content once
        context.addModelsForClasses(CardListCmp.class);
        context.load().json(DEMO_CARD_LIST_PAGE_PATH, "/content/card-list");
        context.load().json(DEMO_CARD_LIST_PATH, "/content/dam/card-list");
        context.load().json(DEMO_BASIC_CARD_PATH, "/content/dam/basic-cards");
        context.load().json(DEMO_LINKS_PATH, "/content/dam/links");
    }

    @BeforeEach
    void setup() {
        context.registerService(SimpleQueryBuilder.class, simpleQueryBuilder);

        cards.clear();
        cards.add(context.request().getResourceResolver().getResource("/content/dam/basic-cards/basic-hero").getChild("jcr:content"));
        cards.add(context.request().getResourceResolver().getResource("/content/dam/basic-cards/simple-card").getChild("jcr:content"));
    }

    @Disabled
    @Test
    void getCards_default() {
        when(simpleQueryBuilder.search(any(), anyMap())).thenReturn(cards);

        cardListCmp = getComponent("/content/card-list", "card_list");

        assertNotNull(cardListCmp);
        assertFalse(cardListCmp.getCards().isEmpty());
        assertTrue(cardListCmp.getCards().getFirst().isCallToActionEnabled());
    }

    @Test
    void getHeadline_default() {
        String expected = "Title for component";
        cardListCmp = getComponent("/content/card-list", "card_list");

        assertNotNull(cardListCmp);
        assertEquals(expected, cardListCmp.getHeadline());
    }

    @Test
    void getShortDescription_default() {
        String expected = "<p>Short description about the content returned.</p>\n";
        cardListCmp = getComponent("/content/card-list", "card_list");

        assertNotNull(cardListCmp);
        assertEquals(expected, cardListCmp.getShortDescription());
    }

    @Test
    void isEnableSearch_default() {
        cardListCmp = getComponent("/content/card-list", "card_list");

        assertNotNull(cardListCmp);
        assertTrue(cardListCmp.isEnableSearch());
    }

    @Disabled
    @Test
    void isCardsFound_default() {
       when(simpleQueryBuilder.search(any(), anyMap())).thenReturn(cards);

        cardListCmp = getComponent("/content/card-list", "card_list");

        assertNotNull(cardListCmp);
        assertTrue(cardListCmp.isCardsFound());
    }

    @Test
    void getExportedType_expected() {
        cardListCmp = getComponent("/content/card-list", "card_list");

        assertNotNull(cardListCmp);
        assertEquals(CardListCmp.RESOURCE_TYPE, cardListCmp.getExportedType());
    }

    CardListCmp getComponent(String path, String component) {
        context.currentResource(path + "/jcr:content/root/container/container/" + component);
        return context.request().adaptTo(CardListCmp.class);
    }
}
