package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.common.links.SimpleLinkBuilder;
import com.voghan.pillar.core.models.ArticleDetail;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ArticleDetailCfm extends BaseModelCfm implements ArticleDetail {

  @Self
  private SimpleLinkBuilder linkBuilder;

  @ValueMapValue
  private String headline;

  @ValueMapValue
  private String description;

  @ValueMapValue
  private String subheadline;

  @ValueMapValue
  private String bannerImage;

  @ValueMapValue
  private Date postDate;

  @ValueMapValue
  private String url;

  @ValueMapValue
  private String content;

  @Override
  public String getHeadline() {
    return headline;
  }

  @Override
  public String getSubheadline() {
    return subheadline;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getBannerImageUrl() {
    return bannerImage;
  }

  @Override
  public String getPostDate() {
    return postDate != null ? new SimpleDateFormat("MMMM dd, yyyy").format(postDate) : null;
  }

  @Override
  public String getUrl() {
    return linkBuilder != null ? linkBuilder.getLinkUrl(url) : null;
  }

  @Override
  public String getContent() {
    return content;
  }
}
