package com.voghan.pillar.common.links;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.voghan.pillar.common.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junitx.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class SimpleLinkBuilderTest {
    private static final AemContext context = AppAemContext.newAemContext();
    private static final String ROOT_CONTENT = "/content/pillar/us/en";

    @Mock
    ResourceResolver resourceResolver;

    @Mock
    PageManager pageManager;

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

    @Disabled
    @Test
    void getLinkUrl_default() {

        linkBuilder = getComponent();
        String path = ROOT_CONTENT;
        String expected = "/content/pillar/us/en.html";
        Page page = mock(Page.class);
        ValueMap valueMap = mock(ValueMap.class);
        when(pageManager.getPage(path)).thenReturn(page);
        when(page.getPath()).thenReturn(path);
        when(page.getProperties()).thenReturn(valueMap);
        when(valueMap.containsKey(any())).thenReturn(false);

        String actual = linkBuilder.getLinkUrl(path);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void getLinkUrl_externalLink() {
        String expected = "http://www.adobe.com";
        linkBuilder = getComponent();

        String actual = linkBuilder.getLinkUrl(expected);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void isExternalLink_internalLink() {
        String path = ROOT_CONTENT;
        linkBuilder = getComponent();
        boolean actual = linkBuilder.isExternalLink(path);

        assertFalse(actual);
    }

    @Test
    void isExternalLink_externalLink() {
        String path = "http://www.adobe.com";
        linkBuilder = getComponent();
        boolean actual = linkBuilder.isExternalLink(path);

        assertTrue(actual);
    }

    @Test
    void isAssetLink_pageLink() {
        String path = ROOT_CONTENT;
        linkBuilder = getComponent();
        boolean actual = linkBuilder.isAssetLink(path);

        assertFalse(actual);
    }

    @Test
    void isAssetLink_assetLink() {
        String path = "/content/dam/pillar/us/en";
        linkBuilder = getComponent();
        boolean actual = linkBuilder.isAssetLink(path);

        assertTrue(actual);
    }

    @Test
    void findTargetPage_() {
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
    void formatUrl_assetLink() {
        String path = "/content/dam/pillar/us/en";
        String expected = path + ".html";
        linkBuilder = getComponent();
        String actual = linkBuilder.formatUrl(path);

        assertEquals(expected, actual);
    }

    private SimpleLinkBuilder getComponent() {
        return context.currentResource().adaptTo(SimpleLinkBuilder.class);
    }

}
