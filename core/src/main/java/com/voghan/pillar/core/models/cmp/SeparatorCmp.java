package com.voghan.pillar.core.models.cmp;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.style.ComponentStyleInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.voghan.pillar.core.models.Separator;
import com.voghan.pillar.core.models.cfm.SeparatorCfm;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = {
                Separator.class, ComponentExporter.class
        },
        resourceType = SeparatorCmp.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = "json")
public class SeparatorCmp extends BaseModelCmp implements Separator {
    static final String RESOURCE_TYPE = "pillar/components/separator/v2/separator";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Self
    private SlingHttpServletRequest slingHttpServletRequest;

    @SlingObject
    private Resource resource;

    @ValueMapValue
    private String fragmentPath;

    @ValueMapValue
    private String variationName;

    private SeparatorCfm separatorCfm;

    private String appliedCssClasses;

    @PostConstruct
    protected void init() {
        logger.debug("Post Construct for {} separator ... ", fragmentPath);

        loadContent();

        applyStyles();
    }

    private void loadContent() {
        if (fragmentPath != null) {
            if (StringUtils.isEmpty(variationName)) {
                variationName = "master";
            }
            Resource resource = slingHttpServletRequest.getResourceResolver().getResource(fragmentPath);
            if (resource != null && resource.getChild("jcr:content/data/" + variationName) != null) {
                separatorCfm = resource.getChild("jcr:content/data/" + variationName).adaptTo(SeparatorCfm.class);
            }
        }
    }

    private void applyStyles() {
        if (resource == null) return;

        ComponentStyleInfo styleInfo =  resource.adaptTo(ComponentStyleInfo.class);
        if (styleInfo != null) {
            appliedCssClasses = styleInfo.getAppliedCssClasses();
        }
        if (StringUtils.isEmpty(appliedCssClasses)) {
            appliedCssClasses = getColor() + StringUtils.SPACE + getSpacing();
        }
    }

    @Override
    public @Nullable @JsonProperty("appliedCssClassNames")
    String getAppliedCssClasses() {
        return appliedCssClasses;
    }

    @Override
    public Boolean isDecorative() {
        return separatorCfm != null && separatorCfm.isDecorative();
    }

    @Override
    public String getSpacing() {
        return separatorCfm != null ? separatorCfm.getSpacing() : "";
    }

    @Override
    public String getColor() {
        return separatorCfm!= null ? separatorCfm.getColor() : "";
    }

    @Override
    public @NotNull String getExportedType() {
        return RESOURCE_TYPE;
    }
}
