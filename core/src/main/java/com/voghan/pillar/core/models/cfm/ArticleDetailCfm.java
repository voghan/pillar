package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.common.links.SimpleLinkBuilder;
import com.voghan.pillar.core.models.ArticleDetail;
import com.voghan.pillar.core.models.Link;
import com.voghan.pillar.core.models.impl.PillarLink;
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

  private final List<Link> actions = new ArrayList<>();

  @PostConstruct
  protected void init() {
    if (url != null) {
      Link link = new PillarLink(ARTICLE_CTA_I18N, url);
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
  public List<Link> getCallToActions() {
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
    return linkBuilder != null ? linkBuilder.getLinkUrl(url) : url;
  }

  @Override
  public String getContent() {
    return content;
  }
}
