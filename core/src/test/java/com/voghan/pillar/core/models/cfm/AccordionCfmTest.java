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
public class AccordionCfmTest {
    private static final AemContext context = AppAemContext.newAemContext();

    private static final String DEMO_ACCORDION_PATH = "/pillar-core/model/cfm/accordions.json";
    private static final String DEMO_CARD_PATH = "/pillar-core/model/cfm/simpleCards.json";

    private AccordionCfm accordionCfm;

    @BeforeAll
    static void setup() {
        // Load context content once
        context.addModelsForClasses(CardCfm.class);
        context.load().json(DEMO_ACCORDION_PATH, "/content/dam/accordions");
        context.load().json(DEMO_CARD_PATH, "/content/dam/simple-cards");
    }

    @Test
    void getVersion_default() {
        accordionCfm = getComponent("/content/dam/accordions/accordion-demo", "master");

        assertNotNull(accordionCfm);
        assertEquals("master", accordionCfm.getVersion());
    }

    @Test
    void getSingleExpansion_default() {
        accordionCfm = getComponent("/content/dam/accordions/accordion-demo", "master");

        assertNotNull(accordionCfm);
        assertEquals(Boolean.TRUE, accordionCfm.getSingleExpansion());
    }

    @Test
    void getId_default() {
        accordionCfm = getComponent("/content/dam/accordions/accordion-demo", "master");

        assertNotNull(accordionCfm);
        assertNotNull(accordionCfm.getId());
    }

    @Test
    void getName_default() {
        accordionCfm = getComponent("/content/dam/accordions/accordion-demo", "master");

        assertNotNull(accordionCfm);
        assertNotNull(accordionCfm.getName());
        assertEquals("/content/dam/accordions/accordion-demo",accordionCfm.getName());
    }

    @Test
    void getExpandedItems_default() {
        accordionCfm = getComponent("/content/dam/accordions/accordion-demo", "master");

        assertNotNull(accordionCfm);
        assertEquals(0, accordionCfm.getExpandedItems().size());
    }

    @Test
    void getItems_default() {
        accordionCfm = getComponent("/content/dam/accordions/accordion-demo", "master");

        assertNotNull(accordionCfm);
        assertEquals(3, accordionCfm.getItems().size());
    }

    @Test
    void getHeadingElement_missing_returnNull() {
        accordionCfm = getComponent("/content/dam/accordions/accordion-demo", "master");

        assertNotNull(accordionCfm);
        assertEquals(null, accordionCfm.getHeadingElement());
    }

    @Test
    void getSingleExpansion_whenMultiExpand_returnFalse() {
        accordionCfm = getComponent("/content/dam/accordions/multi-expand", "en");

        assertNotNull(accordionCfm);
        assertEquals(Boolean.FALSE, accordionCfm.getSingleExpansion());
    }

    @Test
    void getExpandedItems_whenExpended_returnItems() {
        accordionCfm = getComponent("/content/dam/accordions/multi-expand", "en");

        assertNotNull(accordionCfm);
        assertEquals(1, accordionCfm.getExpandedItems().size());
        assertEquals("/content/dam/simple-cards/option2", accordionCfm.getExpandedItems().getFirst());
    }

    @Test
    void getHeadingElement_whenH3_returnExpected() {
        accordionCfm = getComponent("/content/dam/accordions/multi-expand", "master");

        assertNotNull(accordionCfm);
        assertEquals("h3", accordionCfm.getHeadingElement());
    }

    AccordionCfm getComponent(String path, String version) {
        Resource resource = context.currentResource(path + "/jcr:content/data/" + version);
        return resource != null ? resource.adaptTo(AccordionCfm.class) : null;
    }
}
