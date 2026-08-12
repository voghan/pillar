package com.voghan.pillar.core.models.cmp;

import com.adobe.cq.export.json.ComponentExporter;
import com.day.cq.commons.Externalizer;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.voghan.pillar.core.models.Image;
import com.voghan.pillar.core.models.cfm.ImageCfm;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = {
                Image.class, ComponentExporter.class
        },
        resourceType = ImageCmp.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ImageCmp extends BaseModelCmp implements Image {
    static final String RESOURCE_TYPE = "pillar/components/image/v2/image";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Self
    private SlingHttpServletRequest slingHttpServletRequest;

    @OSGiService
    private Externalizer externalizer;

    @ValueMapValue
    private String fragmentPath;

    @ValueMapValue
    private String variationName;

    private ImageCfm imageCfm;

    private Asset image;
    private String src;
    private String width;
    private String height;
    private String uuid;

    @PostConstruct
    protected void init() {
        logger.debug("Post Construct for {} image ... ", fragmentPath);

        if (fragmentPath != null) {
            if (StringUtils.isEmpty(variationName)) {
                variationName = "master";
            }
            Resource resource = slingHttpServletRequest.getResourceResolver().getResource(fragmentPath);
            if (resource != null && resource.getChild("jcr:content/data/" + variationName) != null) {
                imageCfm = resource.getChild("jcr:content/data/" + variationName).adaptTo(ImageCfm.class);
            }

            if (imageCfm != null) {
                Resource r = slingHttpServletRequest.getResourceResolver().getResource(imageCfm.getFileReference());
                if (r != null) {
                    image = r.adaptTo(Asset.class);
                    if (image != null) {
                        width = image.getMetadataValue(DamConstants.TIFF_IMAGEWIDTH);
                        height = image.getMetadataValue(DamConstants.TIFF_IMAGELENGTH);
                        uuid = image.getID();
                        src = image.getPath();
                        //TODO Externalize asset paths
//                        if (externalizer != null) {
//                            src = externalizer.relativeLink(request, image.getPath());
//                        }
                    }
                }
            }
        }
    }

    @Override
    public String getFileReference() {
        return imageCfm != null ? imageCfm.getFileReference() : null;
    }

    @Override
    public @NotNull String getExportedType() {
        return RESOURCE_TYPE;
    }

    @Override
    public Boolean isLazyEnabled() {
        return imageCfm != null ? imageCfm.isLazyEnabled() : false;
    }

    @Override
    public Boolean isDecorative() {
        return imageCfm != null ? imageCfm.isDecorative() : false;
    }

    @Override
    public String getCaption() {
        return imageCfm != null ? imageCfm.getCaption() : StringUtils.EMPTY;
    }

    @Override
    public String getAltText() {
        return imageCfm != null ? imageCfm.getAltText() : StringUtils.EMPTY;
    }

    public String getSrc() {
        return src;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public String getUuid() {
        return uuid;
    }
}
