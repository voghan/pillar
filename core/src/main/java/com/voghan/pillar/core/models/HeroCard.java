package com.voghan.pillar.core.models;

import java.util.List;

public interface HeroCard extends Card {

    List<Link> getBreadcrumbs();

    String getBackgroundImage();
}
