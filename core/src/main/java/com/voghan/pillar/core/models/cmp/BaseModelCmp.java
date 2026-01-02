package com.voghan.pillar.core.models.cmp;

import com.adobe.cq.wcm.core.components.models.Component;
import com.adobe.cq.wcm.core.components.models.Page;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.Nullable;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BaseModelCmp implements Component {

    @ScriptVariable
    private Page currentPage;

    @ValueMapValue
    private String id;

    @Override
    public @Nullable String getId() {
        return id;
    }

    @Override
    public @Nullable ComponentData getData() {
        return Component.super.getData();
    }

    @Override
    public @Nullable String getAppliedCssClasses() {
        return Component.super.getAppliedCssClasses();
    }

    protected Page getCurrentPage() {
        return currentPage;
    }

}
