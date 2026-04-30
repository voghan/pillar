package com.voghan.pillar.common;

import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.ResourceResolverFactory;

public class AuthUtil {

  public static Map<String, Object> getAuthInfo(String serviceName) {
    final Map<String, Object> authInfo = new HashMap<>();
    authInfo.put(ResourceResolverFactory.SUBSERVICE, serviceName);
    return authInfo;
  }
}
