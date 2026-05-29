package com.voghan.pillar.core.models;

import java.util.List;

public interface CardListConfig {

  String getHeadline();

  String getShortDescription();

  String getSearchPath();

  boolean isEnableSearch();

  List<String> getCardTags();

  List<String> getFilterTags();

  boolean isEnablePostDate();
}
