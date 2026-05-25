package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.core.models.SeoData;
import java.util.ArrayList;
import java.util.List;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SeoDataCfm extends BaseModelCfm implements SeoData {

  @ValueMapValue
  private String title;

  @ValueMapValue
  private String description;

  @ValueMapValue
  private String canonicalUrl;

  @ValueMapValue
  private String thumbnail;

  @ValueMapValue
  private List<String> robots;


  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getCanonicalUrl() {
    return canonicalUrl;
  }

  @Override
  public String getThumbnailUrl() {
    return thumbnail;
  }

  @Override
  public List<String> getRobots() {
    return robots != null ? new ArrayList<>(robots) : new ArrayList<>();
  }
}
