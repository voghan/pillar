package com.voghan.pillar.core.models.cmp;

import com.adobe.cq.wcm.core.components.models.Component;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.Nullable;

public class AbstractContentFragmentModel implements Component {

    @Self
    private Resource resource;

    @Override
    public @Nullable String getId() {
        return Component.super.getId();
    }

    @Override
    public @Nullable ComponentData getData() {
        return Component.super.getData();
    }

    @Override
    public @Nullable String getAppliedCssClasses() {
        return Component.super.getAppliedCssClasses();
    }

    Resource getCfm(String version) {

        return resource.getChild("/jcr:content/data/" + version);
    }
}
