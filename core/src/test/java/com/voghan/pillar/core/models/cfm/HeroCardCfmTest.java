package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.core.models.Link;
import com.voghan.pillar.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

@ExtendWith(AemContextExtension.class)
public class HeroCardCfmTest {
    private static final AemContext context = AppAemContext.newAemContext();

    private static final String DEMO_LINKS_PATH = "/pillar-core/model/cfm/links.json";
    private static final String DEMO_HERO_PATH = "/pillar-core/model/cfm/heroCards.json";

    HeroCardCfm heroCardCfm;

    @BeforeAll
    static void setup() {
        // Load context content once
        context.addModelsForClasses(LinkCfm.class);
        context.load().json(DEMO_LINKS_PATH, "/content/dam/links");
        context.load().json(DEMO_HERO_PATH, "/content/dam/hero");
    }

    @Test
    void getHeadline_default() {
        heroCardCfm = getComponent("/content/dam/hero/demo-hero", "master");

        assertNotNull(heroCardCfm);
        assertEquals("Pillar Demo Components", heroCardCfm.getHeadline());
    }

    @Test
    void getHeadline_variationEs() {
        heroCardCfm = getComponent("/content/dam/hero/demo-hero", "es");

        assertNotNull(heroCardCfm);
        assertEquals("Componentes de demostración de Pillar", heroCardCfm.getHeadline());
    }

    @Test
    void getShortDescription_default() {
        heroCardCfm = getComponent("/content/dam/hero/demo-hero", "master");
        String expected = "<p>A simple framework of HTL components that utilize content fragments for their auhtoring experience.</p>\n";

        assertNotNull(heroCardCfm);
        assertEquals(expected, heroCardCfm.getShortDescription());
    }

    @Test
    void getShortDescription_variation() {
        heroCardCfm = getComponent("/content/dam/hero/demo-hero", "es");
        String expected = "<p>Un marco sencillo de componentes HTL que utilizan fragmentos de contenido para la experiencia de creación de contenido.</p>\n";

        assertNotNull(heroCardCfm);
        assertEquals(expected, heroCardCfm.getShortDescription());
    }

    @Test
    void getCallToActions_default() {
        heroCardCfm = getComponent("/content/dam/hero/demo-hero", "master");

        assertNotNull(heroCardCfm);
        assertFalse(heroCardCfm.getCallToActions().isEmpty());
        assertEquals(2, heroCardCfm.getCallToActions().size());
        Link link = heroCardCfm.getCallToActions().get(0);
        assertNotNull(link);
        assertEquals("Get started", link.getLinkText());
        assertEquals("/content/pillar/us/en", link.getLinkPath());
    }

    @Test
    void getBreadcrumbs_default() {
        heroCardCfm = getComponent("/content/dam/hero/demo-hero", "master");

        assertNotNull(heroCardCfm);
        assertFalse(heroCardCfm.getBreadcrumbs().isEmpty());
        assertEquals(2, heroCardCfm.getBreadcrumbs().size());
        Link link = heroCardCfm.getBreadcrumbs().get(0);
        assertNotNull(link);
        assertEquals("Pillar Site", link.getLinkText());
        assertEquals("/content/pillar/us/en", link.getLinkPath());
    }

    @Test
    void getVersion_default() {
        heroCardCfm = getComponent("/content/dam/hero/demo-hero", "master");

        assertNotNull(heroCardCfm);
        assertEquals("master", heroCardCfm.getVersion());
    }

    @Test
    void getVersion_variationEs() {
        heroCardCfm = getComponent("/content/dam/hero/demo-hero", "es");

        assertNotNull(heroCardCfm);
        assertEquals("es", heroCardCfm.getVersion());
    }

    HeroCardCfm getComponent(String path, String version) {
        Resource resource = context.currentResource(path + "/jcr:content/data/" + version);
        return resource != null ? resource.adaptTo(HeroCardCfm.class) : null;
    }
}
