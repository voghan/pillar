package com.voghan.pillar.core.models.cmp;

import com.voghan.pillar.core.models.Link;
import com.voghan.pillar.core.models.cfm.LinkCfm;
import com.voghan.pillar.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

@ExtendWith(AemContextExtension.class)
public class SimpleCardCmpTest {
    private static final AemContext context = AppAemContext.newAemContext();

    SimpleCardCmp simpleCardCmp;

    private static final String DEMO_PAGE_PATH = "/pillar-core/model/cmp/simpleCardCmps.json";
    private static final String DEMO_LINKS_PATH = "/pillar-core/model/cfm/links.json";
    private static final String DEMO_CARD_PATH = "/pillar-core/model/cfm/simpleCards.json";

    @BeforeAll
    static void setup() {
        // Load context content once
        context.addModelsForClasses(SimpleCardCmp.class);
        context.load().json(DEMO_PAGE_PATH, "/content/simple-cards");
        context.load().json(DEMO_LINKS_PATH, "/content/dam/links");
        context.load().json(DEMO_CARD_PATH, "/content/dam/simple-cards");
    }

    @Test
    void getHeadline_default() {
        simpleCardCmp = getComponent("/content/simple-cards", "card");

        assertNotNull(simpleCardCmp);
        assertEquals("Epic Journey", simpleCardCmp.getHeadline());
    }

    @Test
    void getShortDescription_default() {
        simpleCardCmp = getComponent("/content/simple-cards", "card");
        String expected = "<p>Don't stop half way, go for the top!</p>\n";

        assertNotNull(simpleCardCmp);
        assertEquals(expected, simpleCardCmp.getShortDescription());
    }

    @Test
    void getCallToActions_default() {
        simpleCardCmp = getComponent("/content/simple-cards", "card");

        assertNotNull(simpleCardCmp);
        assertFalse(simpleCardCmp.getCallToActions().isEmpty());
        assertEquals(1, simpleCardCmp.getCallToActions().size());
        Link link = simpleCardCmp.getCallToActions().get(0);
        assertNotNull(link);
        assertEquals("Get started", link.getLinkText());
        assertEquals("/content/pillar/us/en", link.getLinkPath());
    }

    @Test
    void getImage_default() {
        simpleCardCmp = getComponent("/content/simple-cards", "card");

        assertNotNull(simpleCardCmp);
        assertEquals("/content/dam/images/BrianLogo2019-medium.png", simpleCardCmp.getImage());
    }

    @Test
    void getExportedType_expected() {
        simpleCardCmp = getComponent("/content/simple-cards", "card");

        assertNotNull(simpleCardCmp);
        assertEquals(SimpleCardCmp.RESOURCE_TYPE, simpleCardCmp.getExportedType());
    }

    SimpleCardCmp getComponent(String path, String component) {
        context.currentResource(path + "/jcr:content/root/container/container/" + component);
        return context.request().adaptTo(SimpleCardCmp.class);
    }
}
