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
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
public class ImageCfmTest {
    private static final AemContext context = AppAemContext.newAemContext();

    private static final String DEMO_CFM_PATH = "/pillar-core/model/cfm/images.json";
    public static final String DAM_PATH = "/content/dam/image";

    ImageCfm imageCfm;

    @BeforeAll
    static void setup() {
        // Load context content once
        context.addModelsForClasses(ImageCfm.class);
        context.load().json(DEMO_CFM_PATH, DAM_PATH);
    }

    @Test
    void getVersion_whenVersionMaster() {
        imageCfm = getComponent(DAM_PATH + "/image_default", "master");

        assertNotNull(imageCfm);
        assertEquals("master", imageCfm.getVersion());
    }

    @Test
    void getFileReference_whenEmpty() {
        imageCfm = getComponent(DAM_PATH + "/image_empty", "master");

        assertNotNull(imageCfm);
        assertNull(imageCfm.getFileReference());
    }

    @Test
    void getAltText_whenEmpty() {
        imageCfm = getComponent(DAM_PATH + "/image_empty", "master");

        assertNotNull(imageCfm);
        assertEquals("", imageCfm.getAltText());
    }

    @Test
    void getCaption_whenEmpty() {
        imageCfm = getComponent(DAM_PATH + "/image_empty", "master");

        assertNotNull(imageCfm);
        assertEquals("", imageCfm.getCaption());
    }

    @Test
    void isDecorative_whenEmpty() {
        imageCfm = getComponent(DAM_PATH + "/image_empty", "master");

        assertNotNull(imageCfm);
        assertEquals(false, imageCfm.isDecorative());
    }

    @Test
    void isLazyEnabled_whenEmpty() {
        imageCfm = getComponent(DAM_PATH + "/image_empty", "master");

        assertNotNull(imageCfm);
        assertEquals(false, imageCfm.isLazyEnabled());
    }

    @Test
    void getFileReference_whenFileRefProvided() {
        imageCfm = getComponent(DAM_PATH + "/image_default", "master");

        assertNotNull(imageCfm);
        assertEquals("/content/dam/images/4d2245b9.png", imageCfm.getFileReference());
    }

    @Test
    void getAltText_whenALtTextProvided() {
        imageCfm = getComponent(DAM_PATH + "/image_default", "master");

        assertNotNull(imageCfm);
        assertEquals("some alt text", imageCfm.getAltText());
    }

    @Test
    void getCaption_whenCaptionMissing() {
        imageCfm = getComponent(DAM_PATH + "/image_default", "master");

        assertNotNull(imageCfm);
        assertEquals("", imageCfm.getCaption());
    }

    @Test
    void isDecorative_whenNotDecorative() {
        imageCfm = getComponent(DAM_PATH + "/image_default", "master");

        assertNotNull(imageCfm);
        assertEquals(false, imageCfm.isDecorative());
    }

    @Test
    void isLazyEnabled_whenSimpleText() {
        imageCfm = getComponent(DAM_PATH + "/image_default", "master");

        assertNotNull(imageCfm);
        assertEquals(true, imageCfm.isLazyEnabled());
    }

    @Test
    void getAltText_whenAltTextMissing() {
        imageCfm = getComponent(DAM_PATH + "/image_caption", "master");

        assertNotNull(imageCfm);
        assertEquals("", imageCfm.getAltText());
    }

    @Test
    void getCaption_whenCaptionProvided() {
        imageCfm = getComponent(DAM_PATH + "/image_caption", "master");

        assertNotNull(imageCfm);
        assertEquals("demo", imageCfm.getCaption());
    }

    @Test
    void isDecorative_whenDecorative() {
        imageCfm = getComponent(DAM_PATH + "/image_caption", "master");

        assertNotNull(imageCfm);
        assertEquals(true, imageCfm.isDecorative());
    }

    @Test
    void getVersion_whenEnVersion() {
        imageCfm = getComponent(DAM_PATH + "/image_caption", "en");

        assertNotNull(imageCfm);
        assertEquals("en", imageCfm.getVersion());
    }

    @Test
    void getFileReference_whenEnVersion() {
        imageCfm = getComponent(DAM_PATH + "/image_caption", "en");

        assertNotNull(imageCfm);
        assertEquals("/content/dam/images/asset_en.jpg", imageCfm.getFileReference());
    }

    @Test
    void getAltText_whenEnVersion() {
        imageCfm = getComponent(DAM_PATH + "/image_caption", "en");

        assertNotNull(imageCfm);
        assertEquals("", imageCfm.getAltText());
    }

    @Test
    void getCaption_whenEnVersion() {
        imageCfm = getComponent(DAM_PATH + "/image_caption", "en");

        assertNotNull(imageCfm);
        assertEquals("demo en", imageCfm.getCaption());
    }

    @Test
    void isDecorative_whenEnVersion() {
        imageCfm = getComponent(DAM_PATH + "/image_caption", "en");

        assertNotNull(imageCfm);
        assertEquals(true, imageCfm.isDecorative());
    }

    @Test
    void isLazyEnabled_whenEnVersion() {
        imageCfm = getComponent(DAM_PATH + "/image_caption", "en");

        assertNotNull(imageCfm);
        assertEquals(true, imageCfm.isLazyEnabled());
    }

    ImageCfm getComponent(String path, String version) {
        Resource resource = context.currentResource(path + "/jcr:content/data/" + version);
        return resource != null ? resource.adaptTo(ImageCfm.class) : null;
    }
}
