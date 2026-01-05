package com.voghan.pillar.core.models;

import java.util.List;

public interface CardListConfig {

    String getHeadline();

    String getShortDescription();

    String getSearchPath();

    Boolean getEnableSearch();

    List<String> getCardTags();

    List<String> getFilterTags();
}
