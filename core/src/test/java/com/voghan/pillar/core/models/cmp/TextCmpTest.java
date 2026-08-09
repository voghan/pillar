package com.voghan.pillar.core.models.cmp;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.voghan.pillar.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@ExtendWith(AemContextExtension.class)
public class TextCmpTest {
    private static final AemContext context = AppAemContext.newAemContext();

    private static final String DEMO_PAGE_PATH = "/pillar-core/model/cmp/textCmps.json";
    private static final String DEMO_TEXT_PATH = "/pillar-core/model/cfm/text.json";

    static ContentFragment contentFragment = mock(ContentFragment.class);

    private TextCmp textCmp;

    @BeforeAll
    static void setupAll() {
        context.load().json(DEMO_PAGE_PATH, "/content/text");
        context.load().json(DEMO_TEXT_PATH, "/content/dam/text");
        context.addModelsForPackage("com.voghan.pillar.core.models.cfm");
        context.registerAdapter(Resource.class, ContentFragment.class, contentFragment);
    }

    @Test
    void getText_whenTextSimple() {
        String expected = "WKND is a collective of outdoors, music, crafts, adventure sports, and travel enthusiasts that want to share our experiences, connections, and expertise with the world. Our objective is create a community to help like-minded adventure seekers find fun, engaging, and responsible ways to to enjoy life and create lasting memories.";

        textCmp = getComponent("text_simple");

        assertNotNull(textCmp);
        assertEquals(expected, textCmp.getText());
        textCmp.isRichText();
    }

    @Test
    void isRichText_whenTextSimple() {
        textCmp = getComponent("text_simple");

        assertNotNull(textCmp);
        assertEquals(Boolean.FALSE, textCmp.isRichText());
    }

    @Test
    void getText_whenTextRich() {
        String expected = "<p>WKND is a collective of outdoors, music, crafts, adventure sports, and travel enthusiasts that want to share our experiences, connections, and expertise with the world. Our objective is create a community to help like-minded adventure seekers find fun, engaging, and responsible ways to to enjoy life and create lasting memories.</p>\n";

        textCmp = getComponent("text_rich");

        assertNotNull(textCmp);
        assertEquals(expected, textCmp.getText());
        textCmp.isRichText();
    }

    @Test
    void isRichText_whenTextRich() {
        textCmp = getComponent("text_rich");

        assertNotNull(textCmp);
        assertEquals(Boolean.TRUE, textCmp.isRichText());
    }

    @Test
    void getText_whenTextEmpty() {
        String expected = StringUtils.EMPTY;
        textCmp = getComponent("text_empty");

        assertNotNull(textCmp);
        assertEquals(expected, textCmp.getText());
        textCmp.isRichText();
    }

    @Test
    void isRichText_whenTextEmpty() {
        textCmp = getComponent("text_empty");

        assertNotNull(textCmp);
        assertEquals(Boolean.FALSE, textCmp.isRichText());
    }

    @Test
    void getText_whenVariationMissing() {
        String expected = "<h2>Somehting</h2>\n<p>WKND is a collective of outdoors, music, crafts, adventure sports, and travel enthusiasts that want to share our experiences, connections, and expertise with the world. Our objective is create a community to help like-minded adventure seekers find fun, engaging, and responsible ways to to enjoy life and create lasting memories.</p>\n<h3>Additional</h3>\n<p>WKND is a collective of outdoors, music, crafts, adventure sports, and travel enthusiasts that want to share our experiences, connections, and expertise with the world. Our objective is create a community to help like-minded adventure seekers find fun, engaging, and responsible ways to to enjoy life and create lasting memories.</p>\n";

        textCmp = getComponent("text_long");

        assertNotNull(textCmp);
        assertEquals(expected, textCmp.getText());
        textCmp.isRichText();
    }

    @Test
    void isRichText_whenVariationMissing() {
        textCmp = getComponent("text_long");

        assertNotNull(textCmp);
        assertEquals(Boolean.TRUE, textCmp.isRichText());
    }

    TextCmp getComponent(String component) {
        context.currentResource("/content/text" + "/jcr:content/root/container/container/" + component);
        return context.request().adaptTo(TextCmp.class);
    }
}
