/*
 *  Copyright 2018 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.voghan.pillar.common.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

@ExtendWith(AemContextExtension.class)
class SimpleLoggingFilterTest {

  private final SimpleLoggingFilter fixture = new SimpleLoggingFilter();

  private final TestLogger logger = TestLoggerFactory.getTestLogger(fixture.getClass());

  @BeforeEach
  void setup() {
    TestLoggerFactory.clear();
  }

  @Test
  void doFilter(AemContext context) throws IOException, ServletException {
    MockSlingHttpServletRequest request = context.request();
    MockSlingHttpServletResponse response = context.response();

    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setResourcePath("/content/test");
    requestPathInfo.setSelectorString("selectors");

    fixture.init(mock(FilterConfig.class));
    fixture.doFilter(request, response, mock(FilterChain.class));
    fixture.destroy();

    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(1, events.size());
    LoggingEvent event = events.get(0);
    assertEquals(Level.INFO, event.getLevel());
    assertEquals(4, event.getArguments().size());
    assertEquals("/content/test", event.getArguments().get(1));
    assertEquals("selectors", event.getArguments().get(2));
  }

  // -------------------------------------------------------------------------
  // getParametersAsString
  // -------------------------------------------------------------------------

  @Test
  void getParametersAsString_emptyMap_returnsEmptyString() {
    String result = fixture.getParametersAsString(new LinkedHashMap<>());
    assertEquals("", result);
  }

  @Test
  void getParametersAsString_singleParam_singleValue() {
    Map<String, String[]> params = new LinkedHashMap<>();
    params.put("page", new String[]{"1"});

    String result = fixture.getParametersAsString(params);

    assertEquals("{ param page values [1] }", result);
  }

  @Test
  void getParametersAsString_singleParam_multipleValues() {
    Map<String, String[]> params = new LinkedHashMap<>();
    params.put("tag", new String[]{"news", "sport", "tech"});

    String result = fixture.getParametersAsString(params);

    assertEquals("{ param tag values [news, sport, tech] }", result);
  }

  @Test
  void getParametersAsString_multipleParams_containsBothEntries() {
    Map<String, String[]> params = new LinkedHashMap<>();
    params.put("q", new String[]{"aem"});
    params.put("page", new String[]{"2"});

    String result = fixture.getParametersAsString(params);

    assertTrue(result.contains("{ param q values [aem] }"));
    assertTrue(result.contains("{ param page values [2] }"));
  }

  @Test
  void getParametersAsString_paramWithEmptyValueArray() {
    Map<String, String[]> params = new LinkedHashMap<>();
    params.put("flag", new String[]{});

    String result = fixture.getParametersAsString(params);

    assertEquals("{ param flag values [] }", result);
  }
}
