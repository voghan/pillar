package com.voghan.pillar.core.models;

import com.voghan.pillar.common.links.model.SimpleLink;
import java.util.List;

public interface HeroCard extends Card {

  List<SimpleLink> getBreadcrumbs();

  String getBackgroundImage();
}
