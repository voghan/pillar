package com.voghan.pillar.core.models.cmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.voghan.pillar.common.queries.SimpleQueryBuilder;
import com.voghan.pillar.core.models.Card;
import com.voghan.pillar.core.models.cfm.ArticleDetailCfm;
import com.voghan.pillar.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import java.util.ArrayList;
import java.util.List;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
public class CardListCmpTest {

  private static final AemContext context = AppAemContext.newAemContext();
  private static final SimpleQueryBuilder simpleQueryBuilder = mock(SimpleQueryBuilder.class);

  private static final String DEMO_CARD_LIST_PAGE_PATH = "/pillar-core/model/cmp/cardListCmps.json";
  private static final String DEMO_CARD_LIST_PATH = "/pillar-core/model/cfm/cardListConfigs.json";
  private static final String DEMO_BASIC_CARD_PATH = "/pillar-core/model/cfm/basicCards.json";
  private static final String DEMO_SIMPLE_CARD_PATH = "/pillar-core/model/cfm/simpleCards.json";
  private static final String DEMO_LINKS_PATH = "/pillar-core/model/cfm/links.json";

  CardListCmp cardListCmp;

  final List<Resource> cards = new ArrayList<>();

  static ContentFragment contentFragment = mock(ContentFragment.class);

  @BeforeAll
  static void setupAll() {
    context.addModelsForClasses(CardListCmp.class);
    context.addModelsForPackage("com.voghan.pillar.core.models.cfm");
    context.load().json(DEMO_CARD_LIST_PAGE_PATH, "/content/card-list");
    context.load().json(DEMO_CARD_LIST_PATH, "/content/dam/card-list");
    context.load().json(DEMO_BASIC_CARD_PATH, "/content/dam/basic-cards");
    context.load().json(DEMO_SIMPLE_CARD_PATH, "/content/dam/simple-cards");
    context.load().json(DEMO_LINKS_PATH, "/content/dam/links");
    context.registerService(SimpleQueryBuilder.class, simpleQueryBuilder);
    context.registerAdapter(Resource.class, ContentFragment.class, contentFragment);
  }

  @BeforeEach
  void setup() {
    reset(simpleQueryBuilder);
    reset(contentFragment);

    cards.clear();
    Resource option1 = context.resourceResolver().getResource("/content/dam/simple-cards/option1");
    Resource option2 = context.resourceResolver().getResource("/content/dam/simple-cards/option2");
    cards.add(option1.getChild("jcr:content"));
    cards.add(option2.getChild("jcr:content"));

  }

  @Test
  void getCards_default() {
    mockQueryResults("/path");

    cardListCmp = getComponent("card_list");

    assertNotNull(cardListCmp);
    assertFalse(cardListCmp.getCards().isEmpty());
    assertTrue(cardListCmp.getCards().getFirst().isCallToActionEnabled());
  }

  @Test
  void getHeadline_default() {
    String expected = "Title for component";
    cardListCmp = getComponent("card_list");

    assertNotNull(cardListCmp);
    assertEquals(expected, cardListCmp.getHeadline());
  }

  @Test
  void getShortDescription_default() {
    String expected = "<p>Short description about the content returned.</p>\n";
    cardListCmp = getComponent("card_list");

    assertNotNull(cardListCmp);
    assertEquals(expected, cardListCmp.getShortDescription());
  }

  @Test
  void isEnableSearch_default() {
    cardListCmp = getComponent("card_list");

    assertNotNull(cardListCmp);
    assertTrue(cardListCmp.isEnableSearch());
  }

  @Test
  void isCardsFound_default() {
    mockQueryResults("/path");

    cardListCmp = getComponent("card_list");

    assertNotNull(cardListCmp);
    assertTrue(cardListCmp.isCardsFound());
  }

  @Test
  void getExportedType_expected() {
    cardListCmp = getComponent("card_list");

    assertNotNull(cardListCmp);
    assertEquals(CardListCmp.RESOURCE_TYPE, cardListCmp.getExportedType());
  }

  @Test
  void isCardsFound_noCards() {
    cardListCmp = getComponent("card_list");

    assertNotNull(cardListCmp);
    assertFalse(cardListCmp.isCardsFound());
    assertTrue(cardListCmp.getCards().isEmpty());
  }

  @Test
  void getCards_returnsDefensiveCopy() {
    when(simpleQueryBuilder.search(any(), anyMap())).thenReturn(cards);

    cardListCmp = getComponent("card_list");

    assertNotNull(cardListCmp);
    List<Card> snapshot = cardListCmp.getCards();
    int initialSize = snapshot.size();
    snapshot.clear();
    assertEquals(initialSize, cardListCmp.getCards().size());
  }

  @Test
  void getFilterOptions_default() {
    cardListCmp = getComponent("card_list");

    assertNotNull(cardListCmp);
    assertTrue(cardListCmp.getFilterOptions().isEmpty());
  }

  @Test
  void getHeadline_noFragmentPath() {
    cardListCmp = getComponent("card_list_no_fragment");

    assertNotNull(cardListCmp);
    assertNull(cardListCmp.getHeadline());
  }

  @Test
  void getShortDescription_noFragmentPath() {
    cardListCmp = getComponent("card_list_no_fragment");

    assertNotNull(cardListCmp);
    assertNull(cardListCmp.getShortDescription());
  }

  @Test
  void isEnableSearch_noFragmentPath() {
    cardListCmp = getComponent("card_list_no_fragment");

    assertNotNull(cardListCmp);
    assertFalse(cardListCmp.isEnableSearch());
  }

  @Test
  void isCardsFound_noFragmentPath() {
    cardListCmp = getComponent("card_list_no_fragment");

    assertNotNull(cardListCmp);
    assertFalse(cardListCmp.isCardsFound());
  }

  @Test
  void getCards_whenArticleType_returnsArticle() {
    mockQueryResults(ArticleDetailCfm.MODEL);

    cardListCmp = getComponent("article_list");

    assertNotNull(cardListCmp);
    List<Card> snapshot = cardListCmp.getCards();
    assertEquals(cards.size(), cardListCmp.getCards().size());
  }

  CardListCmp getComponent(String component) {
    context.currentResource("/content/card-list" + "/jcr:content/root/container/container/" + component);
    return context.request().adaptTo(CardListCmp.class);
  }

  private void mockQueryResults(String model) {
    when(simpleQueryBuilder.search(any(), anyMap())).thenReturn(cards);
    FragmentTemplate template = mock(FragmentTemplate.class);
    Resource resource = mock(Resource.class);
    Resource parent  = mock(Resource.class);
    when(contentFragment.getTemplate()).thenReturn(template);
    when(template.adaptTo(Resource.class)).thenReturn(resource);
    when(resource.getParent()).thenReturn(parent);
    when(parent.getPath()).thenReturn(model);
  }
}
