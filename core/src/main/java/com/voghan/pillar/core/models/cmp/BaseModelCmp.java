package com.voghan.pillar.core.models.cmp;

import com.adobe.cq.wcm.core.components.models.Component;
import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BaseModelCmp extends AbstractComponentImpl implements Component {
    @Override
    protected Page getCurrentPage() {
        return super.getCurrentPage();
    }
}
