package com.voghan.pillar.core.models;

import com.voghan.pillar.core.models.cfm.CardCfm;

import java.util.List;

public interface Accordion {

    String getHeadingElement();

    Boolean getSingleExpansion();

    List<Card> getItems();

    List<String> getExpandedItems();
}
