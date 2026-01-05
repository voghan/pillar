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
import static junit.framework.Assert.assertTrue;

@ExtendWith(AemContextExtension.class)
public class HeroCardCmpTest {
    private static final AemContext context = AppAemContext.newAemContext();

    private static final String DEMO_HERO_PAGE_PATH = "/pillar-core/model/cmp/heroCardCmps.json";
    private static final String DEMO_LINKS_PATH = "/pillar-core/model/cfm/links.json";
    private static final String DEMO_HERO_PATH = "/pillar-core/model/cfm/heroCards.json";

    HeroCardCmp heroCardCmp;

    @BeforeAll
    static void setup() {
        // Load context content once
        context.addModelsForClasses(HeroCardCmp.class);
        context.load().json(DEMO_HERO_PAGE_PATH, "/content/hero");
        context.load().json(DEMO_LINKS_PATH, "/content/dam/links");
        context.load().json(DEMO_HERO_PATH, "/content/dam/hero");
    }

    @Test
    void getHeadline_default() {
        heroCardCmp = getComponent("/content/hero", "hero");

        assertNotNull(heroCardCmp);
        assertEquals("Pillar Demo Components", heroCardCmp.getHeadline());
    }

    @Test
    void getHeadline_variation() {
        heroCardCmp = getComponent("/content/hero", "hero_es");

        assertNotNull(heroCardCmp);
        assertEquals("Componentes de demostración de Pillar", heroCardCmp.getHeadline());
    }
    @Test
    void getShortDescription_default() {
        heroCardCmp = getComponent("/content/hero", "hero");
        String expected = "<p>A simple framework of HTL components that utilize content fragments for their auhtoring experience.</p>\n";

        assertNotNull(heroCardCmp);
        assertEquals(expected, heroCardCmp.getShortDescription());
    }

    @Test
    void getShortDescription_variation() {
        heroCardCmp = getComponent("/content/hero", "hero_es");
        String expected = "<p>Un marco sencillo de componentes HTL que utilizan fragmentos de contenido para la experiencia de creación de contenido.</p>\n";

        assertNotNull(heroCardCmp);
        assertEquals(expected, heroCardCmp.getShortDescription());
    }

    @Test
    void getCallToActions_default() {
        heroCardCmp = getComponent("/content/hero", "hero");

        assertNotNull(heroCardCmp);
        assertFalse(heroCardCmp.getCallToActions().isEmpty());
        assertEquals(2, heroCardCmp.getCallToActions().size());
        Link link = heroCardCmp.getCallToActions().get(0);
        assertNotNull(link);
        assertEquals("Get started", link.getLinkText());
        assertEquals("/content/pillar/us/en", link.getLinkPath());
    }

    @Test
    void getBreadcrumbs_default() {
        heroCardCmp = getComponent("/content/hero", "hero");

        assertNotNull(heroCardCmp);
        assertFalse(heroCardCmp.getBreadcrumbs().isEmpty());
        assertEquals(2, heroCardCmp.getBreadcrumbs().size());
        Link link = heroCardCmp.getBreadcrumbs().get(0);
        assertNotNull(link);
        assertEquals("Pillar Site", link.getLinkText());
        assertEquals("/content/pillar/us/en", link.getLinkPath());
    }

    @Test
    void getBackgroundImage_default() {
        heroCardCmp = getComponent("/content/hero", "hero");

        assertNotNull(heroCardCmp);
        assertEquals("/content/dam/images/asset.jpg", heroCardCmp.getBackgroundImage());
    }

    @Test
    void isCallToActionEnabled_default() {
        heroCardCmp = getComponent("/content/hero", "hero");

        assertNotNull(heroCardCmp);
        assertTrue(heroCardCmp.isCallToActionEnabled());
    }

    @Test
    void isBreadcrumbsEnabled_default() {
        heroCardCmp = getComponent("/content/hero", "hero");

        assertNotNull(heroCardCmp);
        assertTrue(heroCardCmp.isBreadcrumbsEnabled());
    }

    @Test
    void getExportedType_expected() {
        heroCardCmp = getComponent("/content/hero", "hero");

        assertNotNull(heroCardCmp);
        assertEquals(HeroCardCmp.RESOURCE_TYPE, heroCardCmp.getExportedType());
    }

    HeroCardCmp getComponent(String path, String component) {
        context.currentResource(path + "/jcr:content/root/container/container/" + component);
        return context.request().adaptTo(HeroCardCmp.class);
    }
}
