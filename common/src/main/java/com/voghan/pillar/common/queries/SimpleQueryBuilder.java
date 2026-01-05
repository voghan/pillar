package com.voghan.pillar.common.queries;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import java.util.List;
import java.util.Map;

public interface SimpleQueryBuilder {
    List<Resource> search(SlingHttpServletRequest request, Map<String, String> predicatesMap);
}
