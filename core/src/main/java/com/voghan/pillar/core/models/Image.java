package com.voghan.pillar.core.models;

public interface Image {

    String getFileReference();

    Boolean isLazyEnabled();

    Boolean isDecorative();

    String getCaption();

    String getAltText();
}
