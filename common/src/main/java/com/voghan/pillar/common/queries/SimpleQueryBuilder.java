package com.voghan.pillar.common.queries;

import java.util.List;
import java.util.Map;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

public interface SimpleQueryBuilder {

  List<Resource> search(SlingHttpServletRequest request, Map<String, String> predicatesMap);
}
