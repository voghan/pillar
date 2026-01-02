package com.voghan.pillar.common.links;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.drew.lang.annotations.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SimpleLinkBuilder {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Self
    private Resource resource;

    private PageManager pageManager;
    private ResourceResolver resolver;

    public String getLinkUrl(String path) {
        String url = path;

        if(!(isExternalLink(path) || isAssetLink(path)) ) {
            url = getPageUrl(path);
        }
        return url;
    }

    protected boolean isExternalLink(String url) {
        return StringUtils.isNotBlank(url) && !url.startsWith("/");
    }

    protected boolean isAssetLink(String url) {
        return StringUtils.isNotBlank(url) && url.startsWith("/content/dam/");
    }

    protected String getPageUrl(String path) {
        String url = path;
        try {
            resolver = resource.getResourceResolver();
            pageManager = resolver.adaptTo(PageManager.class);

            if (pageManager != null && pageManager.getPage(path) != null) {
                Page page = pageManager.getPage(path);
                url = formatUrl(findTargetPage(page));
            }

        } catch (Exception e) {
            logger.info("Error occurred transforming {}", path, e);
        }

        return url;
    }

    protected String findTargetPage(@NotNull Page page) {
        String url = page.getPath();
        ValueMap valueMap = page.getProperties();
        if (valueMap.containsKey(NameConstants.PN_REDIRECT_TARGET)) {
            String redirectTarget = valueMap.get(NameConstants.PN_REDIRECT_TARGET, String.class);
            Page targetPage = pageManager.getPage(redirectTarget);
            if (targetPage != null) {
                url = findTargetPage(targetPage);
            }
        }
        return url;
    }

    protected String formatUrl(String url) {
        String formattedUrl = url;
        if (!isExternalLink(url)) {
            formattedUrl = resolver.map(url) + ".html";
        }

        return formattedUrl;
    }
}
