package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(AemContextExtension.class)
public class SeparatorCfmTest {
    private static final AemContext context = AppAemContext.newAemContext();

    private static final String DEMO_SEPARATOR_PATH = "/pillar-core/model/cfm/separator.json";

    SeparatorCfm separatorCfm;

    @BeforeAll
    static void setup() {
        // Load context content once
        context.addModelsForClasses(CardCfm.class);
        context.load().json(DEMO_SEPARATOR_PATH, "/content/dam/separator");
    }

    @Test
    void isDecorative_whenDefault() {
        separatorCfm = getComponent("/content/dam/separator/separator-default", "master");

        assertNotNull(separatorCfm);
        assertEquals(false, separatorCfm.isDecorative());
    }

    @Test
    void getColor_whenDefault() {
        separatorCfm = getComponent("/content/dam/separator/separator-default", "master");

        assertNotNull(separatorCfm);
        assertEquals("", separatorCfm.getColor());
    }

    @Test
    void getSpacing_whenDefault() {
        separatorCfm = getComponent("/content/dam/separator/separator-default", "master");

        assertNotNull(separatorCfm);
        assertEquals("", separatorCfm.getSpacing());
    }

    @Test
    void isDecorative_whenAllProvided() {
        separatorCfm = getComponent("/content/dam/separator/separator-small-light-decorative", "master");

        assertNotNull(separatorCfm);
        assertEquals(true, separatorCfm.isDecorative());
    }

    @Test
    void getColor_whenAllProvided() {
        separatorCfm = getComponent("/content/dam/separator/separator-small-light-decorative", "master");

        assertNotNull(separatorCfm);
        assertEquals("light", separatorCfm.getColor());
    }

    @Test
    void getSpacing_whenAllProvided() {
        separatorCfm = getComponent("/content/dam/separator/separator-small-light-decorative", "master");

        assertNotNull(separatorCfm);
        assertEquals("small", separatorCfm.getSpacing());
    }

    @Test
    void isDecorative_whenLargeHidden() {
        separatorCfm = getComponent("/content/dam/separator/separator-large-hidden", "en");

        assertNotNull(separatorCfm);
        assertEquals(false, separatorCfm.isDecorative());
    }

    @Test
    void getColor_whenLargeHidden() {
        separatorCfm = getComponent("/content/dam/separator/separator-large-hidden", "en");

        assertNotNull(separatorCfm);
        assertEquals("hidden", separatorCfm.getColor());
    }

    @Test
    void getSpacing_whenLargeHidden() {
        separatorCfm = getComponent("/content/dam/separator/separator-large-hidden", "en");

        assertNotNull(separatorCfm);
        assertEquals("large", separatorCfm.getSpacing());
    }

    SeparatorCfm getComponent(String path, String version) {
        Resource resource = context.currentResource(path + "/jcr:content/data/" + version);
        return resource != null ? resource.adaptTo(SeparatorCfm.class) : null;
    }
}
