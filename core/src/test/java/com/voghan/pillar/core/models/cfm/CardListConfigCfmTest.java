package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@ExtendWith(AemContextExtension.class)
public class CardListConfigCfmTest {
    private static final AemContext context = AppAemContext.newAemContext();

    private static final String DEMO_CARD_PATH = "/pillar-core/model/cfm/cardListConfigs.json";

    CardListConfigCfm cardListConfigCfm;

    @BeforeAll
    static void setup() {
        // Load context content once
        context.addModelsForClasses(LinkCfm.class);
        context.load().json(DEMO_CARD_PATH, "/content/dam/card-list-config");
    }

    @Test
    void getHeadline_default() {
        String expected = "Title for component";
        cardListConfigCfm = getComponent("/content/dam/card-list-config/simple-card-list", "master");

        String actual = cardListConfigCfm.getHeadline();

        assertEquals(expected, actual);
    }

    @Test
    void getShortDescription_default() {
        String expected = "<p>Short description about the content returned.</p>\n";
        cardListConfigCfm = getComponent("/content/dam/card-list-config/simple-card-list", "master");

        String actual = cardListConfigCfm.getShortDescription();

        assertEquals(expected, actual);
    }

    @Test
    void getSearchPath_default() {
        String expected = "/content/dam/pillar/cfm/basic-cards/demo";
        cardListConfigCfm = getComponent("/content/dam/card-list-config/simple-card-list", "master");

        String actual = cardListConfigCfm.getSearchPath();

        assertEquals(expected, actual);
    }

    @Test
    void getEnableSearch_default() {
        Boolean expected = Boolean.TRUE;
        cardListConfigCfm = getComponent("/content/dam/card-list-config/simple-card-list", "master");

        Boolean actual = cardListConfigCfm.getEnableSearch();

        assertTrue(actual);
    }

    @Test
    void getCardTags_default() {
        cardListConfigCfm = getComponent("/content/dam/card-list-config/simple-card-list", "master");

        List<String> actual = cardListConfigCfm.getCardTags();

        assertEquals(1, actual.size());
    }

    @Test
    void getFilterTags_default() {
        cardListConfigCfm = getComponent("/content/dam/card-list-config/simple-card-list", "master");

        List<String> actual = cardListConfigCfm.getFilterTags();

        assertEquals(4, actual.size());
    }

    CardListConfigCfm getComponent(String path, String version) {
        Resource resource = context.currentResource(path + "/jcr:content/data/" + version);
        return resource != null ? resource.adaptTo(CardListConfigCfm.class) : null;
    }
}
