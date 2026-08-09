package com.voghan.pillar.core.models.cmp;

import com.adobe.cq.export.json.ComponentExporter;
import com.voghan.pillar.core.models.Text;
import com.voghan.pillar.core.models.cfm.TextCfm;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = {
                Text.class, ComponentExporter.class
        },
        resourceType = TextCmp.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TextCmp extends BaseModelCmp implements Text {
    static final String RESOURCE_TYPE = "pillar/components/text/v2/text";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Self
    private SlingHttpServletRequest slingHttpServletRequest;

    @ValueMapValue
    private String fragmentPath;

    @ValueMapValue
    private String variationName;

    private TextCfm textCfm;

    @PostConstruct
    protected void init() {
        logger.debug("Post Construct for {} text ... ", fragmentPath);

        if (fragmentPath != null) {
            if (StringUtils.isEmpty(variationName)) {
                variationName = "master";
            }
            Resource resource = slingHttpServletRequest.getResourceResolver().getResource(fragmentPath);
            if (resource != null && resource.getChild("jcr:content/data/" + variationName) != null) {
                textCfm = resource.getChild("jcr:content/data/" + variationName).adaptTo(TextCfm.class);
            }
        }
    }

    @Override
    public boolean isRichText() {
        return textCfm != null && textCfm.isRichText();
    }

    @Override
    public String getText() {
        return textCfm != null ? textCfm.getText() : StringUtils.EMPTY;
    }
}
