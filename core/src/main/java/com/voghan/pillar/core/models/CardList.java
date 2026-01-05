package com.voghan.pillar.core.models;

import java.util.List;

public interface CardList {

    String getHeadline();

    String getShortDescription();

    List<Card> getCards();

    Boolean isEnableSearch();

    List<String> getFilterOptions();
}
