package com.voghan.pillar.common.links;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.voghan.pillar.common.links.model.SimpleLink;
import com.voghan.pillar.common.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class SimpleLinkBuilderTest {

  private static final AemContext context = AppAemContext.newAemContext();
  private static final String ROOT_CONTENT = "/content/pillar/us/en";

  @Mock PageManager pageManager;

  @BeforeAll
  static void setupAll() {
    context.registerService(SimpleLinkBuilderConfig.class, (SimpleLinkBuilderConfig) () -> "/content/pillar");
    context.addModelsForClasses(SimpleLinkBuilder.class);
    context.build().resource(ROOT_CONTENT).commit();
  }

  @BeforeEach
  void setup() {
    context.registerAdapter(ResourceResolver.class, PageManager.class, pageManager);
    context.currentResource(ROOT_CONTENT);
  }

  // -------------------------------------------------------------------------
  // withPage + build
  // -------------------------------------------------------------------------

  @Test
  void withPage_setsNavigationTitleAndUrlFromLinkManager() {
    Resource res = context.resourceResolver().getResource(ROOT_CONTENT);
    Page page = mock(Page.class);
    when(page.getNavigationTitle()).thenReturn("Home");
    when(page.getPath()).thenReturn(ROOT_CONTENT);

    SimpleLink result = getComponent().withResource(res).withPage(page).build();

    assertNotNull(result);
    assertEquals("Home", result.getLinkText());
    assertEquals(ROOT_CONTENT + ".html", result.getLinkPath());
  }

  @Test
  void withPage_noNavigationTitle_fallsBackToPageTitle() {
    Resource res = context.resourceResolver().getResource(ROOT_CONTENT);
    Page page = mock(Page.class);
    when(page.getNavigationTitle()).thenReturn(null);
    when(page.getTitle()).thenReturn("Page Title");
    when(page.getPath()).thenReturn(ROOT_CONTENT);

    SimpleLink result = getComponent().withResource(res).withPage(page).build();

    assertEquals("Page Title", result.getLinkText());
  }

  // -------------------------------------------------------------------------
  // withResource + build
  // -------------------------------------------------------------------------

  @Test
  void withResource_setsUrlFromLinkManager() {
    Resource res = context.resourceResolver().getResource("/content/pillar/us");

    SimpleLink result = getComponent().withResource(res).build();

    assertNotNull(result);
    assertEquals("/content/pillar/us.html", result.getLinkPath());
  }

  // -------------------------------------------------------------------------
  // withPath + build
  // -------------------------------------------------------------------------

  @Test
  void withPath_assetPath_returnsUnchanged() {
    String path = "/content/dam/pillar/image.png";
    SimpleLink result = getComponent().withPath(path).build();

    assertNotNull(result);
    assertEquals(path, result.getLinkPath());
  }

  @Test
  void withPath_externalUrl_returnsUnchanged() {
    String url = "https://www.adobe.com";
    SimpleLink result = getComponent().withPath(url).build();

    assertEquals(url, result.getLinkPath());
  }

  @Test
  void withRelativeUrl_assetRelativePath_returnsAbsolutePath() {
    String expected ="/content/pillar/us/en/articles.html";
    String path = "/us/en/articles";

    SimpleLink result = getComponent().withRelativeUrl(path).build();

    assertNotNull(result);
    assertEquals(expected, result.getLinkPath());
  }

  // -------------------------------------------------------------------------
  // withText + build
  // -------------------------------------------------------------------------

  @Test
  void build_afterWithText_returnsLinkWithText() {
    SimpleLink result = getComponent()
        .rest()
        .withPath("/content/dam/pillar/image.png")
        .withText("Click Here")
        .build();

    assertNotNull(result);
    assertEquals("Click Here", result.getLinkText());
    assertEquals("/content/dam/pillar/image.png", result.getLinkPath());
  }

  @Test
  void build_withNullText_returnsLinkWithNullText() {
    SimpleLink result = getComponent().withText(null).build();

    assertNotNull(result);
    assertNull(result.getLinkText());
  }

  @Test
  void build_afterWithReset_returnsLinkWithText() {
    SimpleLinkBuilder builder = getComponent();
    SimpleLink result = builder
        .withRelativeUrl("/us/en/home")
        .withText("Click Here")
        .build();

    assertNotNull(result);
    assertEquals("Click Here", result.getLinkText());
    assertEquals("/content/pillar/us/en/home.html", result.getLinkPath());

    result = builder
        .rest()
        .withPath("/content/pilar/us/en/articles")
        .withText("Click again")
        .build();

    assertNotNull(result);
    assertEquals("Click again", result.getLinkText());
    assertEquals("/content/pilar/us/en/articles", result.getLinkPath());

  }

  // -------------------------------------------------------------------------
  // getAbsolutePath
  // -------------------------------------------------------------------------

  @Test
  void getAbsolutePath_absoluteContentPath_returnsUnchanged() {
    String path = "/content/pillar/us/en/page";
    assertEquals(path, getComponent().getAbsolutePath(path));
  }

  @Test
  void getAbsolutePath_relativePath_prefixesPillarContent() {
    assertEquals("/content/pillar/us/en", getComponent().getAbsolutePath("/us/en"));
  }

  @Test
  void getAbsolutePath_externalUrl_returnsUnchanged() {
    String url = "https://www.adobe.com";
    assertEquals(url, getComponent().getAbsolutePath(url));
  }

  @Test
  void getAbsolutePath_damPath_returnsUnchanged() {
    String path = "/content/dam/pillar/image.png";
    assertEquals(path, getComponent().getAbsolutePath(path));
  }

  // -------------------------------------------------------------------------
  // getPageUrl
  // -------------------------------------------------------------------------

  @Test
  void getPageUrl_externalUrl_returnsUnchanged() {
    String url = "https://www.adobe.com";
    assertEquals(url, getComponent().getPageUrl(url));
  }

  @Test
  void getPageUrl_assetPath_returnsUnchanged() {
    String path = "/content/dam/pillar/image.png";
    assertEquals(path, getComponent().getPageUrl(path));
  }

  @Test
  void getPageUrl_pageNotFound_returnsOriginalPath() {
    String path = "/content/pillar/us/en/missing";
    Resource res = context.resourceResolver().getResource(ROOT_CONTENT);

    // Real AEM mock PageManager returns null for non-existent paths
    assertEquals(path, getComponent().withResource(res).getPageUrl(path));
  }

  // -------------------------------------------------------------------------
  // findTargetPage
  // -------------------------------------------------------------------------

  @Test
  void findTargetPage_noRedirect_returnsPagePath() {
    Page page = mock(Page.class);
    ValueMap valueMap = mock(ValueMap.class);
    when(page.getPath()).thenReturn(ROOT_CONTENT);
    when(page.getProperties()).thenReturn(valueMap);
    when(valueMap.containsKey(NameConstants.PN_REDIRECT_TARGET)).thenReturn(false);

    assertEquals(ROOT_CONTENT, getComponent().findTargetPage(page));
  }

  @Test
  void findTargetPage_blankRedirect_returnsPagePath() {
    Page page = mock(Page.class);
    ValueMap valueMap = mock(ValueMap.class);
    when(page.getPath()).thenReturn(ROOT_CONTENT);
    when(page.getProperties()).thenReturn(valueMap);
    when(valueMap.containsKey(NameConstants.PN_REDIRECT_TARGET)).thenReturn(true);
    when(valueMap.get(NameConstants.PN_REDIRECT_TARGET, String.class)).thenReturn("  ");

    assertEquals(ROOT_CONTENT, getComponent().findTargetPage(page));
  }

  @Test
  void findTargetPage_withExternalRedirect_returnsRedirectUrl() {
    String redirect = "https://www.adobe.com";
    Resource res = context.resourceResolver().getResource(ROOT_CONTENT);
    Page page = mock(Page.class);
    ValueMap valueMap = mock(ValueMap.class);
    when(page.getPath()).thenReturn(ROOT_CONTENT);
    when(page.getProperties()).thenReturn(valueMap);
    when(valueMap.containsKey(NameConstants.PN_REDIRECT_TARGET)).thenReturn(true);
    when(valueMap.get(NameConstants.PN_REDIRECT_TARGET, String.class)).thenReturn(redirect);

    // Real AEM mock PageManager returns null for the external redirect → URL returned as-is
    assertEquals(redirect, getComponent().withResource(res).findTargetPage(page));
  }

  @Test
  void findTargetPage_withInternalRedirect_returnsRedirectPath() {
    String redirect = "/content/pillar/us/en/target";
    Resource res = context.resourceResolver().getResource(ROOT_CONTENT);
    Page page = mock(Page.class);
    ValueMap valueMap = mock(ValueMap.class);
    when(page.getPath()).thenReturn(ROOT_CONTENT);
    when(page.getProperties()).thenReturn(valueMap);
    when(valueMap.containsKey(NameConstants.PN_REDIRECT_TARGET)).thenReturn(true);
    when(valueMap.get(NameConstants.PN_REDIRECT_TARGET, String.class)).thenReturn(redirect);

    // Real AEM mock PageManager returns null for non-existent target → redirect path returned directly
    assertEquals(redirect, getComponent().withResource(res).findTargetPage(page));
  }

  // -------------------------------------------------------------------------
  // formatUrl
  // -------------------------------------------------------------------------

  @Test
  void formatUrl_internalPath_appendsHtmlExtension() {
    Resource res = context.resourceResolver().getResource(ROOT_CONTENT);
    assertEquals(ROOT_CONTENT + ".html",
        getComponent().withResource(res).formatUrl(ROOT_CONTENT));
  }

  @Test
  void formatUrl_externalUrl_returnsUnchanged() {
    String url = "https://www.adobe.com";
    assertEquals(url, getComponent().formatUrl(url));
  }

  // -------------------------------------------------------------------------
  // isExternalLink
  // -------------------------------------------------------------------------

  @Test
  void isExternalLink_httpUrl_returnsTrue() {
    assertTrue(getComponent().isExternalLink("http://www.adobe.com"));
  }

  @Test
  void isExternalLink_httpsUrl_returnsTrue() {
    assertTrue(getComponent().isExternalLink("https://www.adobe.com"));
  }

  @Test
  void isExternalLink_internalPath_returnsFalse() {
    assertFalse(getComponent().isExternalLink(ROOT_CONTENT));
  }

  @Test
  void isExternalLink_null_returnsFalse() {
    assertFalse(getComponent().isExternalLink(null));
  }

  @Test
  void isExternalLink_blankString_returnsFalse() {
    assertFalse(getComponent().isExternalLink(""));
  }

  // -------------------------------------------------------------------------
  // isAssetLink
  // -------------------------------------------------------------------------

  @Test
  void isAssetLink_damPath_returnsTrue() {
    assertTrue(getComponent().isAssetLink("/content/dam/pillar/image.png"));
  }

  @Test
  void isAssetLink_pagePath_returnsFalse() {
    assertFalse(getComponent().isAssetLink(ROOT_CONTENT));
  }

  @Test
  void isAssetLink_null_returnsFalse() {
    assertFalse(getComponent().isAssetLink(null));
  }

  @Test
  void isAssetLink_blankString_returnsFalse() {
    assertFalse(getComponent().isAssetLink(""));
  }

  // -------------------------------------------------------------------------
  // Helpers
  // -------------------------------------------------------------------------

  /** Adapted from AEM context — no linkManager or resource injected internally. */
  private SimpleLinkBuilder getComponent() {
    return context.currentResource().adaptTo(SimpleLinkBuilder.class);
  }

}
