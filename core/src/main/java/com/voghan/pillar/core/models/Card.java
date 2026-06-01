package com.voghan.pillar.core.models;

import com.voghan.pillar.common.links.model.SimpleLink;
import java.util.List;

public interface Card {

  String getHeadline();

  String getShortDescription();

  List<SimpleLink> getCallToActions();

  boolean isCallToActionEnabled();

}
