package com.voghan.pillar.core.models;

import java.util.List;

public interface Card {

    String getHeadline();

    String getShortDescription();

    List<Link> getCallToActions();

}
