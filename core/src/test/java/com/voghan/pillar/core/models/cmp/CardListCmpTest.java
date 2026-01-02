package com.voghan.pillar.core.models.cmp;

import com.voghan.pillar.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@ExtendWith(AemContextExtension.class)
public class CardListCmpTest {
    private static final AemContext context = AppAemContext.newAemContext();

    CardListCmp cardListCmp;

    @Test
    void getCards_default() {
        //cardListCmp = getComponent("/content/card-list", "cardList");
        cardListCmp = new CardListCmp();

        assertNotNull(cardListCmp);
        assertEquals(true, cardListCmp.getCards().isEmpty());
    }

    @Test
    void getExportedType_expected() {
        //cardListCmp = getComponent("/content/card-list", "cardList");
        cardListCmp = new CardListCmp();

        assertNotNull(cardListCmp);
        assertEquals(CardListCmp.RESOURCE_TYPE, cardListCmp.getExportedType());
    }

    CardListCmp getComponent(String path, String component) {
        context.currentResource(path + "/jcr:content/root/container/container/" + component);
        return context.request().adaptTo(CardListCmp.class);
    }
}
