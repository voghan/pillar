package com.voghan.pillar.common.servlets;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class FlushCacheServletTest {

  private FlushCacheServlet fixture;

  @BeforeEach
  void setup() {
    fixture = Mockito.spy(new com.voghan.pillar.common.servlets.FlushCacheServlet());
    com.voghan.pillar.common.servlets.FlushCacheServlet.Config config = mock(com.voghan.pillar.common.servlets.FlushCacheServlet.Config.class);
    when(config.dispatcherHost()).thenReturn("http://localhost");
    fixture.activate(config);
  }

  @Test
  void doPost_withValidPath_returnsOk(AemContext context) throws ServletException, IOException {
    doReturn(200).when(fixture).sendFlushRequest(anyString(), anyString());
    context.request().setParameterMap(Map.of("path", "/content/pillar/us/en"));

    MockSlingHttpServletRequest request = context.request();
    MockSlingHttpServletResponse response = context.response();

    fixture.doPost(request, response);

    assertEquals(200, response.getStatus());
    assertEquals("{\"status\":\"ok\",\"path\":\"/content/pillar/us/en\"}", response.getOutputAsString());
  }

  @Test
  void doPost_withMissingPath_returnsBadRequest(AemContext context) throws ServletException, IOException {
    MockSlingHttpServletRequest request = context.request();
    MockSlingHttpServletResponse response = context.response();

    fixture.doPost(request, response);

    assertEquals(400, response.getStatus());
    assertEquals("{\"status\":\"error\",\"message\":\"path parameter is required\"}", response.getOutputAsString());
  }

  @Test
  void doPost_whenDispatcherReturnsError_returnsInternalServerError(AemContext context)
      throws ServletException, IOException {
    doReturn(503).when(fixture).sendFlushRequest(anyString(), anyString());
    context.request().setParameterMap(Map.of("path", "/content/pillar/us/en"));

    MockSlingHttpServletRequest request = context.request();
    MockSlingHttpServletResponse response = context.response();

    fixture.doPost(request, response);

    assertEquals(500, response.getStatus());
    assertEquals("{\"status\":\"error\",\"message\":\"Dispatcher returned 503\"}", response.getOutputAsString());
  }

  @Test
  void doPost_whenDispatcherUnreachable_returnsInternalServerError(AemContext context)
      throws ServletException, IOException {
    doThrow(new IOException("Connection refused")).when(fixture).sendFlushRequest(anyString(), anyString());
    context.request().setParameterMap(Map.of("path", "/content/pillar/us/en"));

    MockSlingHttpServletRequest request = context.request();
    MockSlingHttpServletResponse response = context.response();

    fixture.doPost(request, response);

    assertEquals(500, response.getStatus());
    assertEquals("{\"status\":\"error\",\"message\":\"Failed to reach dispatcher\"}", response.getOutputAsString());
  }
}
