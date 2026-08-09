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
public class TextCfmTest {
    private static final AemContext context = AppAemContext.newAemContext();

    private static final String DEMO_ACCORDION_PATH = "/pillar-core/model/cfm/text.json";

    TextCfm textCfm;

    @BeforeAll
    static void setup() {
        // Load context content once
        context.addModelsForClasses(CardCfm.class);
        context.load().json(DEMO_ACCORDION_PATH, "/content/dam/text");
    }

    @Test
    void getVersion_whenSimpleText() {
        textCfm = getComponent("/content/dam/text/demo-simple-text", "master");

        assertNotNull(textCfm);
        assertEquals("master", textCfm.getVersion());
    }

    @Test
    void getText_whenSimpleText() {
        String expected = "WKND is a collective of outdoors, music, crafts, adventure sports, and travel enthusiasts that want to share our experiences, connections, and expertise with the world. Our objective is create a community to help like-minded adventure seekers find fun, engaging, and responsible ways to to enjoy life and create lasting memories.";
        textCfm = getComponent("/content/dam/text/demo-simple-text", "master");

        assertNotNull(textCfm);
        assertEquals(expected, textCfm.getText());
    }

    @Test
    void isRichText_whenSimpleText() {
        textCfm = getComponent("/content/dam/text/demo-simple-text", "master");

        assertNotNull(textCfm);
        assertEquals(false, textCfm.isRichText());
    }

    @Test
    void getId_whenSimpleText() {
        textCfm = getComponent("/content/dam/text/demo-simple-text", "master");

        assertNotNull(textCfm);
        assertNotNull(textCfm.getId());
    }

    @Test
    void getName_whenSimpleText() {
        textCfm = getComponent("/content/dam/text/demo-simple-text", "master");

        assertNotNull(textCfm);
        assertEquals("/content/dam/text/demo-simple-text", textCfm.getName());
    }

    @Test
    void getVersion_whenRichText() {
        textCfm = getComponent("/content/dam/text/demo-rich-text", "master");

        assertNotNull(textCfm);
        assertEquals("master", textCfm.getVersion());
    }

    @Test
    void getVersion_whenSimpleTextEn() {
        textCfm = getComponent("/content/dam/text/demo-simple-text", "en");

        assertNotNull(textCfm);
        assertEquals("en", textCfm.getVersion());
    }

    @Test
    void getText_whenRichText() {
        String expected = "<p>WKND is a collective of outdoors, music, crafts, adventure sports, and travel enthusiasts that want to share our experiences, connections, and expertise with the world. Our objective is create a community to help like-minded adventure seekers find fun, engaging, and responsible ways to to enjoy life and create lasting memories.</p>\n";
        textCfm = getComponent("/content/dam/text/demo-rich-text", "master");

        assertNotNull(textCfm);
        assertEquals(expected, textCfm.getText());
    }

    @Test
    void isRichText_whenRichText() {
        textCfm = getComponent("/content/dam/text/demo-rich-text", "master");

        assertNotNull(textCfm);
        assertEquals(true, textCfm.isRichText());
    }

    @Test
    void getId_whenRichText() {
        textCfm = getComponent("/content/dam/text/demo-rich-text", "master");

        assertNotNull(textCfm);
        assertNotNull(textCfm.getId());
    }

    @Test
    void getName_whenRichText() {
        textCfm = getComponent("/content/dam/text/demo-rich-text", "master");

        assertNotNull(textCfm);
        assertEquals("/content/dam/text/demo-rich-text", textCfm.getName());
    }

    TextCfm getComponent(String path, String version) {
        Resource resource = context.currentResource(path + "/jcr:content/data/" + version);
        return resource != null ? resource.adaptTo(TextCfm.class) : null;
    }
}
