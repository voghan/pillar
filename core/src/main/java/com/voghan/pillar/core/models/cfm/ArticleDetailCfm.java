package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.common.links.SimpleLinkBuilder;
import com.voghan.pillar.common.links.model.SimpleLink;
import com.voghan.pillar.core.models.ArticleDetail;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ArticleDetailCfm extends CardCfm implements ArticleDetail {

  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("MMMM dd, yyyy");

  public static final String MODEL = "/conf/pillar/settings/dam/cfm/models/articledetail";
  public static final String ARTICLE_CTA_I18N = "article_cta";

  @Self
  private SimpleLinkBuilder linkBuilder;

  @ValueMapValue
  private String headline;

  @ValueMapValue
  private String shortDescription;

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

  private final List<SimpleLink> actions = new ArrayList<>();

  @PostConstruct
  protected void init() {
    if (url != null) {
      SimpleLink link = linkBuilder.withDynamicUrl(url).withText(ARTICLE_CTA_I18N).build();
      actions.add(link);
    }
  }

  @Override
  public String getHeadline() {
    return headline;
  }

  @Override
  public String getSubheadline() {
    return subheadline;
  }

  @Override
  public String getShortDescription() {
    return shortDescription;
  }

  @Override
  public List<SimpleLink> getCallToActions() {
    return new ArrayList<>(actions);
  }

  @Override
  public boolean isCallToActionEnabled() {
    return !actions.isEmpty();
  }

  @Override
  public String getBannerImageUrl() {
    return bannerImage;
  }

  @Override
  public String getPostDate() {
    return postDate != null
        ? postDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(DATE_FORMATTER)
        : "";
  }

  @Override
  public String getUrl() {
    return linkBuilder != null ? linkBuilder.withPath(url).build().getLinkPath() : url;
  }

  @Override
  public String getContent() {
    return content;
  }
}
