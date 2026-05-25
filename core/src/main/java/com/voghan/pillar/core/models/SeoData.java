package com.voghan.pillar.core.models;

import java.util.List;

public interface SeoData {

    String getTitle();

    String getDescription();

    String getCanonicalUrl();

    String getThumbnailUrl();

    List<String> getRobots();

}
