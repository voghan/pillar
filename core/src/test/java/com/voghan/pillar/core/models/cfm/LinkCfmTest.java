package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static junit.framework.Assert.assertNotNull;
import static junitx.framework.Assert.assertEquals;

@ExtendWith(AemContextExtension.class)
public class LinkCfmTest {
    private static final AemContext context = AppAemContext.newAemContext();

    private static final String DEMO_LINKS_PATH = "/pillar-core/model/cfm/links.json";

    private LinkCfm linkCfm;

    @BeforeAll
    static void setup() {
        // Load context content once
        context.addModelsForClasses(LinkCfm.class);
        context.load().json(DEMO_LINKS_PATH, "/content/dam/links");
    }

    @Test
    void getLinkText_default() {
        linkCfm = getComponent("/content/dam/links/link1", "master");

        assertNotNull(linkCfm);
        assertEquals("Get started", linkCfm.getLinkText());
    }

    @Test
    void getLinkPath_default() {
        linkCfm = getComponent("/content/dam/links/link1", "master");

        assertNotNull(linkCfm);
        assertEquals("/content/pillar/us/en", linkCfm.getLinkPath());
    }

    @Test
    void getLinkText_variationEs() {
        linkCfm = getComponent("/content/dam/links/link1", "es");

        assertNotNull(linkCfm);
        assertEquals("Learn more es", linkCfm.getLinkText());
    }

    @Test
    void getLinkPath_variationEs() {
        linkCfm = getComponent("/content/dam/links/link1", "es");

        assertNotNull(linkCfm);
        assertEquals("/content/pillar/us/es", linkCfm.getLinkPath());
    }

    LinkCfm getComponent(String path, String version) {
        Resource resource = context.currentResource(path + "/jcr:content/data/" + version);
        return resource != null ? resource.adaptTo(LinkCfm.class) : null;
    }
}
