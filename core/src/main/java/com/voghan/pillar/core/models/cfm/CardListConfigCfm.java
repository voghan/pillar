package com.voghan.pillar.core.models.cfm;

import com.voghan.pillar.core.models.CardListConfig;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CardListConfigCfm implements CardListConfig {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  public static final String MODEL = "/conf/pillar/settings/dam/cfm/models/cardlistconfig";

  @Self
  private Resource resource;

  @ValueMapValue
  private String headline;

  @ValueMapValue
  private String shortDescription;

  @ValueMapValue
  private String searchPath;

  @ValueMapValue
  private Boolean enableSearch;

  @ValueMapValue
  private Boolean enablePostDate;

  @ValueMapValue
  private List<String> cardTags = new ArrayList<>();

  @ValueMapValue
  private List<String> filterTags = new ArrayList<>();

  @PostConstruct
  protected void init() {
    logger.debug("Building a Card List Config mode for {}", resource.getPath());

    buildSearchFilters();
  }

  private void buildSearchFilters() {
    if (isEnableSearch()) {
      //TODO build filter options
    }
  }

  @Override
  public String getHeadline() {
    return headline;
  }

  @Override
  public String getShortDescription() {
    return shortDescription;
  }

  public String getSearchPath() {
    return searchPath;
  }

  public boolean isEnableSearch() {
    return Boolean.TRUE.equals(enableSearch);
  }

  public List<String> getCardTags() {
    return new ArrayList<>(cardTags);
  }

  public List<String> getFilterTags() {
    return new ArrayList<>(filterTags);
  }

  @Override
  public boolean isEnablePostDate() {
    return Boolean.TRUE.equals(enablePostDate);
  }
}
