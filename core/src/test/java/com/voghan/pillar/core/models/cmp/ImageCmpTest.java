package com.voghan.pillar.core.models.cmp;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.granite.asset.api.Asset;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class ImageCmpTest {
    private static final AemContext context = AppAemContext.newAemContext();

    private static final String DEMO_PAGE_PATH = "/pillar-core/model/cmp/imageCmps.json";
    private static final String DEMO_CFM_PATH = "/pillar-core/model/cfm/images.json";
    public static final String SITE_PATH = "/content/page";
    public static final String DAM_PATH = "/content/dam/cfm";
    public static final String ASSET_PATH = "/content/dam/images";

    static ContentFragment contentFragment = mock(ContentFragment.class);
    static Asset asset = mock(Asset.class);

    private ImageCmp imageCmp;

    @BeforeAll
    static void setupAll() {
        context.load().json(DEMO_PAGE_PATH, SITE_PATH);
        context.load().json(DEMO_CFM_PATH, DAM_PATH);
        context.load().json(DEMO_CFM_PATH, ASSET_PATH);
        context.addModelsForPackage("com.voghan.pillar.core.models.cfm");
        context.registerAdapter(Resource.class, ContentFragment.class, contentFragment);
        context.registerAdapter(Resource.class, Asset.class, asset);
    }

    @Test
    void getExportedType_expected() {
        imageCmp = getComponent("image_default");

        assertNotNull(imageCmp);
        assertEquals(ImageCmp.RESOURCE_TYPE, imageCmp.getExportedType());
    }

    @Test
    void getFileReference_expected() {
        imageCmp = getComponent("image_default");

        assertNotNull(imageCmp);
        assertEquals("/content/dam/images/4d2245b9.png", imageCmp.getFileReference());
    }

    @Test
    void getAltText_expected() {
        imageCmp = getComponent("image_default");

        assertNotNull(imageCmp);
        assertEquals("some alt text", imageCmp.getAltText());
    }

    @Test
    void getCaption_expected() {
        imageCmp = getComponent("image_default");

        assertNotNull(imageCmp);
        assertEquals("", imageCmp.getCaption());
    }

    @Test
    void isDecorative_expected() {
        imageCmp = getComponent("image_default");

        assertNotNull(imageCmp);
        assertEquals(false, imageCmp.isDecorative());
    }

    @Test
    void isLazyEnabled_expected() {
        imageCmp = getComponent("image_default");

        assertNotNull(imageCmp);
        assertEquals(true, imageCmp.isLazyEnabled());
    }

    @Test
    void getSrc_expected() {
        when(asset.getPath()).thenReturn("/content/image/path");
        imageCmp = getComponent("image_default");

        assertNotNull(imageCmp);
        assertEquals("/content/image/path", imageCmp.getSrc());
    }

    @Test
    void getFileReference_whenVariation() {
        imageCmp = getComponent("image_caption");

        assertNotNull(imageCmp);
        assertEquals("/content/dam/images/asset_en.jpg", imageCmp.getFileReference());
    }

    @Test
    void getAltText_whenVariation() {
        imageCmp = getComponent("image_caption");

        assertNotNull(imageCmp);
        assertEquals("", imageCmp.getAltText());
    }

    @Test
    void getCaption_whenVariation() {
        imageCmp = getComponent("image_caption");

        assertNotNull(imageCmp);
        assertEquals("demo en", imageCmp.getCaption());
    }

    @Test
    void isDecorative_whenVariation() {
        imageCmp = getComponent("image_caption");

        assertNotNull(imageCmp);
        assertEquals(true, imageCmp.isDecorative());
    }

    @Test
    void isLazyEnabled_whenVariation() {
        imageCmp = getComponent("image_caption");

        assertNotNull(imageCmp);
        assertEquals(true, imageCmp.isLazyEnabled());
    }

    @Test
    void getSrc_whenVariation() {
        when(asset.getPath()).thenReturn("/content/image/path");
        imageCmp = getComponent("image_caption");

        assertNotNull(imageCmp);
        assertEquals("/content/image/path", imageCmp.getSrc());
    }

    @Test
    void getFileReference_whenEmpty() {
        imageCmp = getComponent("image_empty");

        assertNotNull(imageCmp);
        assertNull(imageCmp.getFileReference());
    }

    @Test
    void getAltText_whenEmpty() {
        imageCmp = getComponent("image_empty");

        assertNotNull(imageCmp);
        assertEquals("", imageCmp.getAltText());
    }

    @Test
    void getCaption_whenEmpty() {
        imageCmp = getComponent("image_empty");

        assertNotNull(imageCmp);
        assertEquals("", imageCmp.getCaption());
    }

    @Test
    void isDecorative_whenEmpty() {
        imageCmp = getComponent("image_empty");

        assertNotNull(imageCmp);
        assertEquals(false, imageCmp.isDecorative());
    }

    @Test
    void isLazyEnabled_whenEmpty() {
        imageCmp = getComponent("image_empty");

        assertNotNull(imageCmp);
        assertEquals(false, imageCmp.isLazyEnabled());
    }

    @Test
    void getSrc_whenEmpty() {
        imageCmp = getComponent("image_empty");

        assertNotNull(imageCmp);
        assertNull(imageCmp.getSrc());
    }

    ImageCmp getComponent(String component) {
        context.currentResource(SITE_PATH + "/jcr:content/root/container/container/" + component);
        return context.request().adaptTo(ImageCmp.class);
    }
}
