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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

@ExtendWith(AemContextExtension.class)
public class AccordionCmpTest {

    private static final AemContext context = AppAemContext.newAemContext();

    private static final String DEMO_PAGE_PATH = "/pillar-core/model/cmp/accordionsCmps.json";
    private static final String DEMO_ACCORDION_PATH = "/pillar-core/model/cfm/accordions.json";
    private static final String DEMO_CARD_PATH = "/pillar-core/model/cfm/simpleCards.json";

    static ContentFragment contentFragment = mock(ContentFragment.class);

    private AccordionCmp accordionCmp;

    @BeforeAll
    static void setupAll() {
        context.load().json(DEMO_PAGE_PATH, "/content/accordions");
        context.load().json(DEMO_ACCORDION_PATH, "/content/dam/accordions");
        context.load().json(DEMO_CARD_PATH, "/content/dam/simple-cards");
        context.addModelsForPackage("com.voghan.pillar.core.models.cfm");
        context.registerAdapter(Resource.class, ContentFragment.class, contentFragment);
    }

    @Test
    void getSingleExpansion_default() {
        accordionCmp = getComponent("accordion_single");

        assertNotNull(accordionCmp);
        assertEquals(Boolean.TRUE, accordionCmp.getSingleExpansion());
    }

    @Test
    void getId_default() {
        accordionCmp = getComponent("accordion_single");

        assertNotNull(accordionCmp);
        assertNotNull(accordionCmp.getId());
    }

    @Test
    void getExpandedItems_default() {
        accordionCmp = getComponent("accordion_single");

        assertNotNull(accordionCmp);
        assertEquals(0, accordionCmp.getExpandedItems().size());
    }

    @Test
    void getItems_default() {
        accordionCmp = getComponent("accordion_single");

        assertNotNull(accordionCmp);
        assertEquals(3, accordionCmp.getItems().size());
    }

    @Test
    void getSingleExpansion_whenMultiExpand_returnFalse() {
        accordionCmp = getComponent("accordion_multi");

        assertNotNull(accordionCmp);
        assertEquals(Boolean.FALSE, accordionCmp.getSingleExpansion());
    }

    @Test
    void getExpandedItems_whenExpended_returnItems() {
        accordionCmp = getComponent("accordion_multi");

        assertNotNull(accordionCmp);
        assertEquals(1, accordionCmp.getExpandedItems().size());
        assertEquals("/content/dam/simple-cards/option2", accordionCmp.getExpandedItems().getFirst());
    }

    @Test
    void getHeadingElement_missing_returnNull() {
        accordionCmp = getComponent("accordion_single");

        assertNotNull(accordionCmp);
        assertNull(accordionCmp.getHeadingElement());
    }

    @Test
    void getHeadingElement_whenH3_returnExpected() {
        accordionCmp = getComponent("accordion_multi");

        assertNotNull(accordionCmp);
        assertEquals("h2", accordionCmp.getHeadingElement());
    }

    @Test
    void getSingleExpansion_whenMissingCfm() {
        accordionCmp = getComponent("accordion_null");

        assertNotNull(accordionCmp);
        assertEquals(Boolean.TRUE, accordionCmp.getSingleExpansion());
    }

    @Test
    void getId_whenMissingCfm() {
        accordionCmp = getComponent("accordion_null");

        assertNotNull(accordionCmp);
        assertNotNull(accordionCmp.getId());
    }

    @Test
    void getExpandedItems_whenMissingCfm() {
        accordionCmp = getComponent("accordion_null");

        assertNotNull(accordionCmp);
        assertEquals(0, accordionCmp.getExpandedItems().size());
    }

    @Test
    void getItems_whenMissingCfm() {
        accordionCmp = getComponent("accordion_null");

        assertNotNull(accordionCmp);
        assertEquals(0, accordionCmp.getItems().size());
    }

    @Test
    void getHeadingElement_whenMissingCfm() {
        accordionCmp = getComponent("accordion_single");

        assertNotNull(accordionCmp);
        assertNull(accordionCmp.getHeadingElement());
    }

    AccordionCmp getComponent(String component) {
        context.currentResource("/content/accordions" + "/jcr:content/root/container/container/" + component);
        return context.request().adaptTo(AccordionCmp.class);
    }
}
