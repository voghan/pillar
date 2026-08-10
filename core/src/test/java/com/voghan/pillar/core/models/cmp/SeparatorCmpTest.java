package com.voghan.pillar.core.models.cmp;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.voghan.pillar.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@ExtendWith(AemContextExtension.class)
public class SeparatorCmpTest {
    private static final AemContext context = AppAemContext.newAemContext();

    private static final String DEMO_PAGE_PATH = "/pillar-core/model/cmp/separatorCmps.json";
    private static final String DEMO_SEPARATOR_PATH = "/pillar-core/model/cfm/separator.json";
    public static final String SITE_PATH = "/content/separator";
    public static final String DAM_PATH = "/content/dam/separator";

    static ContentFragment contentFragment = mock(ContentFragment.class);

    private SeparatorCmp separatorCmp;

    @BeforeAll
    static void setupAll() {
        context.load().json(DEMO_PAGE_PATH, SITE_PATH);
        context.load().json(DEMO_SEPARATOR_PATH, DAM_PATH);
        context.addModelsForPackage("com.voghan.pillar.core.models.cfm");
        context.registerAdapter(Resource.class, ContentFragment.class, contentFragment);
    }

    @Test
    void getExportedType_expected() {
        separatorCmp = getComponent("separator-default");

        assertNotNull(separatorCmp);
        assertEquals(SeparatorCmp.RESOURCE_TYPE, separatorCmp.getExportedType());
    }

    @Test
    void isDecorative_expected() {
        separatorCmp = getComponent("separator-default");

        assertNotNull(separatorCmp);
        assertEquals(Boolean.FALSE, separatorCmp.isDecorative());
    }

    @Test
    void getColor_expected() {
        separatorCmp = getComponent("separator-default");

        assertNotNull(separatorCmp);
        assertEquals("", separatorCmp.getColor());
    }

    @Test
    void getSpacing_expected() {
        separatorCmp = getComponent("separator-default");

        assertNotNull(separatorCmp);
        assertEquals("", separatorCmp.getSpacing());
    }

    @Test
    void getAppliedCssClasses_exported() {
        separatorCmp = getComponent("separator-default");

        assertNotNull(separatorCmp);
        assertEquals(" ", separatorCmp.getAppliedCssClasses());
    }

    @Test
    void isDecorative_whenEmpty() {
        separatorCmp = getComponent("separator-empty");

        assertNotNull(separatorCmp);
        assertEquals(Boolean.FALSE, separatorCmp.isDecorative());
    }

    @Test
    void getColor_whenEmpty() {
        separatorCmp = getComponent("separator-empty");

        assertNotNull(separatorCmp);
        assertEquals("", separatorCmp.getColor());
    }

    @Test
    void getSpacing_whenEmpty() {
        separatorCmp = getComponent("separator-empty");

        assertNotNull(separatorCmp);
        assertEquals("", separatorCmp.getSpacing());
    }

    @Test
    void getAppliedCssClasses_whenEmpty() {
        separatorCmp = getComponent("separator-empty");

        assertNotNull(separatorCmp);
        assertEquals(" ", separatorCmp.getAppliedCssClasses());
    }

    @Test
    void isDecorative_whenStyled() {
        separatorCmp = getComponent("separator-medium-dark");

        assertNotNull(separatorCmp);
        assertEquals(Boolean.FALSE, separatorCmp.isDecorative());
    }

    @Test
    void getColor_whenStyled() {
        separatorCmp = getComponent("separator-medium-dark");

        assertNotNull(separatorCmp);
        assertEquals("dark", separatorCmp.getColor());
    }

    @Test
    void getSpacing_whenStyled() {
        separatorCmp = getComponent("separator-medium-dark");

        assertNotNull(separatorCmp);
        assertEquals("medium", separatorCmp.getSpacing());
    }

    @Test
    void getAppliedCssClasses_whenStyled() {
        separatorCmp = getComponent("separator-medium-dark");

        assertNotNull(separatorCmp);
        assertEquals("dark medium", separatorCmp.getAppliedCssClasses());
    }

    @Test
    void isDecorative_whenDecorative() {
        separatorCmp = getComponent("separator-decorative");

        assertNotNull(separatorCmp);
        assertEquals(Boolean.TRUE, separatorCmp.isDecorative());
    }

    @Test
    void getColor_whenDecorative() {
        separatorCmp = getComponent("separator-decorative");

        assertNotNull(separatorCmp);
        assertEquals("light", separatorCmp.getColor());
    }

    @Test
    void getSpacing_whenDecorative() {
        separatorCmp = getComponent("separator-decorative");

        assertNotNull(separatorCmp);
        assertEquals("small", separatorCmp.getSpacing());
    }

    SeparatorCmp getComponent(String component) {
        context.currentResource(SITE_PATH + "/jcr:content/root/container/container/" + component);
        return context.request().adaptTo(SeparatorCmp.class);
    }
}
