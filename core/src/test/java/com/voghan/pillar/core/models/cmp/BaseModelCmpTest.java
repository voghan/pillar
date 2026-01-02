package com.voghan.pillar.core.models.cmp;

import com.voghan.pillar.core.models.cfm.LinkCfm;
import com.voghan.pillar.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static junit.framework.Assert.assertEquals;

@ExtendWith(AemContextExtension.class)
public class BaseModelCmpTest {
    private static final AemContext context = AppAemContext.newAemContext();

    BaseModelCmp baseModelCmp;

    private static final String DEMO_HERO_PAGE_PATH = "/pillar-core/model/cmp/heroCardCmps.json";
    private static final String DEMO_LINKS_PATH = "/pillar-core/model/cfm/links.json";
    private static final String DEMO_HERO_PATH = "/pillar-core/model/cfm/heroCards.json";

    @BeforeAll
    static void setup() {
        // Load context content once
        context.addModelsForClasses(LinkCfm.class);
        context.load().json(DEMO_HERO_PAGE_PATH, "/content/hero");
        context.load().json(DEMO_LINKS_PATH, "/content/dam/links");
        context.load().json(DEMO_HERO_PATH, "/content/dam/hero");
    }

    @Test
    void overrides_default() {
        baseModelCmp = getComponent("/content/hero", "hero");

        assertEquals(null, baseModelCmp.getId());
        assertEquals(null, baseModelCmp.getAppliedCssClasses());
        assertEquals(null, baseModelCmp.getData());
        assertEquals(null, baseModelCmp.getCurrentPage());
    }

    HeroCardCmp getComponent(String path, String component) {
        context.currentResource(path + "/jcr:content/root/container/container/" + component);
        return context.request().adaptTo(HeroCardCmp.class);
    }
}
