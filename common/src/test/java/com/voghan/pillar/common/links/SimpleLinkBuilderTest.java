package com.voghan.pillar.common.links;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junitx.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.voghan.pillar.common.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class SimpleLinkBuilderTest {

  private static final AemContext context = AppAemContext.newAemContext();
  private static final String ROOT_CONTENT = "/content/pillar/us/en";

  @Mock PageManager pageManager;

  @InjectMocks
  SimpleLinkBuilder linkBuilder;

  @BeforeEach
  void setup() {
    context.registerAdapter(ResourceResolver.class, PageManager.class, pageManager);
    if (context.resourceResolver().getResource(ROOT_CONTENT) == null) {
      context.build().resource(ROOT_CONTENT).commit();
    }
    context.currentResource(ROOT_CONTENT);
  }

  // -------------------------------------------------------------------------
  // getLinkUrl
  // -------------------------------------------------------------------------

  @Test
  void getLinkUrl_internalPagePath_returnsFormattedUrl() {
    linkBuilder = getComponent();
    String path = ROOT_CONTENT;
    Page page = mock(Page.class);
    ValueMap valueMap = mock(ValueMap.class);
    lenient().when(pageManager.getPage(path)).thenReturn(page);
    lenient().when(page.getPath()).thenReturn(path);
    lenient().when(page.getProperties()).thenReturn(valueMap);
    lenient().when(valueMap.containsKey(any())).thenReturn(false);

    String actual = linkBuilder.getLinkUrl(path);

    assertNotNull(actual);
  }

  @Test
  void getLinkUrl_externalLink_returnsUnchanged() {
    String expected = "http://www.adobe.com";
    linkBuilder = getComponent();

    String actual = linkBuilder.getLinkUrl(expected);

    assertNotNull(actual);
    assertEquals(expected, actual);
  }

  @Test
  void getLinkUrl_assetPath_returnsUnchanged() {
    // Asset links bypass page resolution and are returned as-is by getLinkUrl
    String path = "/content/dam/pillar/image.png";
    linkBuilder = getComponent();

    String actual = linkBuilder.getLinkUrl(path);

    assertEquals(path, actual);
  }

  @Test
  void getLinkUrl_blankPath_returnsBlank() {
    linkBuilder = getComponent();

    String actual = linkBuilder.getLinkUrl("");

    assertEquals("", actual);
  }

  // -------------------------------------------------------------------------
  // isExternalLink
  // -------------------------------------------------------------------------

  @Test
  void isExternalLink_internalPath_returnsFalse() {
    linkBuilder = getComponent();
    assertFalse(linkBuilder.isExternalLink(ROOT_CONTENT));
  }

  @Test
  void isExternalLink_httpUrl_returnsTrue() {
    linkBuilder = getComponent();
    assertTrue(linkBuilder.isExternalLink("http://www.adobe.com"));
  }

  @Test
  void isExternalLink_httpsUrl_returnsTrue() {
    linkBuilder = getComponent();
    assertTrue(linkBuilder.isExternalLink("https://www.adobe.com"));
  }

  @Test
  void isExternalLink_blankString_returnsFalse() {
    linkBuilder = getComponent();
    assertFalse(linkBuilder.isExternalLink(""));
  }

  @Test
  void isExternalLink_nullString_returnsFalse() {
    linkBuilder = getComponent();
    assertFalse(linkBuilder.isExternalLink(null));
  }

  // -------------------------------------------------------------------------
  // isAssetLink
  // -------------------------------------------------------------------------

  @Test
  void isAssetLink_damPath_returnsTrue() {
    linkBuilder = getComponent();
    assertTrue(linkBuilder.isAssetLink("/content/dam/pillar/us/en"));
  }

  @Test
  void isAssetLink_pagePath_returnsFalse() {
    linkBuilder = getComponent();
    assertFalse(linkBuilder.isAssetLink(ROOT_CONTENT));
  }

  @Test
  void isAssetLink_blankString_returnsFalse() {
    linkBuilder = getComponent();
    assertFalse(linkBuilder.isAssetLink(""));
  }

  @Test
  void isAssetLink_nullString_returnsFalse() {
    linkBuilder = getComponent();
    assertFalse(linkBuilder.isAssetLink(null));
  }

  // -------------------------------------------------------------------------
  // formatUrl
  // -------------------------------------------------------------------------

  @Test
  void formatUrl_internalPath_appendsHtmlExtension() {
    String path = "/content/dam/pillar/us/en";
    linkBuilder = getComponent();

    String actual = linkBuilder.formatUrl(path);

    assertEquals(path + ".html", actual);
  }

  @Test
  void formatUrl_externalUrl_returnsUnchanged() {
    String url = "https://www.adobe.com";
    linkBuilder = getComponent();

    String actual = linkBuilder.formatUrl(url);

    assertEquals(url, actual);
  }

  // -------------------------------------------------------------------------
  // getPageUrl
  // -------------------------------------------------------------------------

  @Test
  void getPageUrl_pageExists_returnsFormattedPath() {
    String path = "/content/page/source";
    Page page = mock(Page.class);
    lenient().when(pageManager.getPage(path)).thenReturn(page);
    when(page.getProperties()).thenReturn(mock(ValueMap.class));

    String actual = linkBuilder.getPageUrl(path);

    assertEquals(path, actual);
  }

  @Test
  void getPageUrl_pageNotFound_returnsOriginalPath() {
    String path = "/content/page/missing";
    when(pageManager.getPage(path)).thenReturn(null);
    // Use @InjectMocks instance — pageManager is already injected

    String actual = linkBuilder.getPageUrl(path);

    assertEquals(path, actual);
  }

  @Test
  void getPageUrl_exceptionThrown_returnsOriginalPath() {
    String path = "/content/page/broken";
    when(pageManager.getPage(path)).thenThrow(new RuntimeException("unexpected"));
    // Use @InjectMocks instance — pageManager is already injected

    String actual = linkBuilder.getPageUrl(path);

    assertEquals(path, actual);
  }

  // -------------------------------------------------------------------------
  // findTargetPage
  // -------------------------------------------------------------------------

  @Test
  void findTargetPage_withRedirectToExternalUrl_returnsExternalUrl() {
    String url = "/content/page/original";
    String redirect = "https://www.page.com/redirect";
    Page page = mock(Page.class);
    ValueMap valueMap = mock(ValueMap.class);
    when(page.getPath()).thenReturn(url);
    when(page.getProperties()).thenReturn(valueMap);
    when(valueMap.containsKey(NameConstants.PN_REDIRECT_TARGET)).thenReturn(true);
    lenient().when(valueMap.get(NameConstants.PN_REDIRECT_TARGET, String.class))
        .thenReturn(redirect);
    linkBuilder = getComponent();

    String actual = linkBuilder.findTargetPage(page);

    assertEquals(redirect, actual);
  }

  @Test
  void findTargetPage_withRedirectToInternalPage_returnsRedirectPath() {
    linkBuilder = getComponent();
    String url = "/content/page/original";
    String redirect = "/content/page/redirect";
    Page page = mock(Page.class);
    Page target = mock(Page.class);
    ValueMap valueMap = mock(ValueMap.class);
    when(page.getPath()).thenReturn(url);
    when(page.getProperties()).thenReturn(valueMap);
    when(valueMap.containsKey(NameConstants.PN_REDIRECT_TARGET)).thenReturn(true);
    lenient().when(pageManager.getPage(redirect)).thenReturn(target);
    lenient().when(valueMap.get(NameConstants.PN_REDIRECT_TARGET, String.class))
        .thenReturn(redirect);
    lenient().when(target.getPath()).thenReturn(redirect);
    lenient().when(target.getProperties()).thenReturn(mock(ValueMap.class));

    String actual = linkBuilder.findTargetPage(page);

    assertEquals(redirect, actual);
  }

  @Test
  void findTargetPage_withExplicitRedirectTarget_returnsTarget() {
    String expected = "https://www.adobe.com";
    Page page = mock(Page.class);
    ValueMap valueMap = mock(ValueMap.class);
    when(page.getPath()).thenReturn(ROOT_CONTENT);
    when(page.getProperties()).thenReturn(valueMap);
    when(valueMap.containsKey(NameConstants.PN_REDIRECT_TARGET)).thenReturn(true);
    when(valueMap.get(NameConstants.PN_REDIRECT_TARGET, String.class)).thenReturn(expected);
    linkBuilder = getComponent();

    String actual = linkBuilder.findTargetPage(page);

    assertEquals(expected, actual);
  }

  @Test
  void findTargetPage_noRedirectKey_returnsPagePath() {
    String path = ROOT_CONTENT;
    Page page = mock(Page.class);
    ValueMap valueMap = mock(ValueMap.class);
    when(page.getPath()).thenReturn(path);
    when(page.getProperties()).thenReturn(valueMap);
    when(valueMap.containsKey(NameConstants.PN_REDIRECT_TARGET)).thenReturn(false);
    linkBuilder = getComponent();

    String actual = linkBuilder.findTargetPage(page);

    assertEquals(path, actual);
  }

  @Test
  void findTargetPage_blankRedirectTarget_returnsPagePath() {
    String path = ROOT_CONTENT;
    Page page = mock(Page.class);
    ValueMap valueMap = mock(ValueMap.class);
    when(page.getPath()).thenReturn(path);
    when(page.getProperties()).thenReturn(valueMap);
    when(valueMap.containsKey(NameConstants.PN_REDIRECT_TARGET)).thenReturn(true);
    when(valueMap.get(NameConstants.PN_REDIRECT_TARGET, String.class)).thenReturn("  ");
    linkBuilder = getComponent();

    String actual = linkBuilder.findTargetPage(page);

    // Blank redirect target → falls back to page's own path
    assertEquals(path, actual);
  }

  // -------------------------------------------------------------------------
  // Helper
  // -------------------------------------------------------------------------

  private SimpleLinkBuilder getComponent() {
    return context.currentResource().adaptTo(SimpleLinkBuilder.class);
  }
}
