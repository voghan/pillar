package com.voghan.pillar.core.models;

import java.util.List;

public interface CardList {

    String getHeadline();

    String getShortDescription();

    boolean isCardsFound();

    List<Card> getCards();

    boolean isEnableSearch();

    List<String> getFilterOptions();
}
