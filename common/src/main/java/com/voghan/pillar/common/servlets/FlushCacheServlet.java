package com.voghan.pillar.common.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Component(service = {Servlet.class})
@SlingServletPaths("/bin/pillar/flushcache")
@ServiceDescription("Pillar - Dispatcher Cache Flush Servlet")
@Designate(ocd = FlushCacheServlet.Config.class)
public class FlushCacheServlet extends SlingAllMethodsServlet {

  private static final Logger LOGGER = LoggerFactory.getLogger(FlushCacheServlet.class);
  private static final String DISPATCHER_FLUSH_PATH = "/dispatcher/invalidate.cache";

  @ObjectClassDefinition(name = "Pillar - Flush Cache Servlet")
  public @interface Config {

    @AttributeDefinition(name = "Dispatcher Host",
        description = "Host URL of the dispatcher (e.g. http://localhost)")
    String dispatcherHost() default "http://localhost";
  }

  private String dispatcherHost;

  @Activate
  protected void activate(Config config) {
    dispatcherHost = config.dispatcherHost();
  }

  /**
   * Example curl command:
   * curl -X POST "http://localhost:4502/bin/pillar/flushcache?path=/content/pillar/us/en" \
   *   -u admin:admin \
   *   -H "Content-Type: application/json"
   */

  @Override
  protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    String path = request.getParameter("path");
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    if (path == null || path.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().write("{\"status\":\"error\",\"message\":\"path parameter is required\"}");
      return;
    }

    try {
      int statusCode = sendFlushRequest(dispatcherHost, path);
      if (statusCode == HttpServletResponse.SC_OK) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"status\":\"ok\",\"path\":\"" + path + "\"}");
      } else {
        LOGGER.warn("Dispatcher flush returned status {} for path {}", statusCode, path);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("{\"status\":\"error\",\"message\":\"Dispatcher returned " + statusCode + "\"}");
      }
    } catch (IOException e) {
      LOGGER.error("Failed to send flush request for path {}", path, e);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.getWriter().write("{\"status\":\"error\",\"message\":\"Failed to reach dispatcher\"}");
    }
  }

  protected int sendFlushRequest(String host, String path) throws IOException {
    URL url = new URL(host + DISPATCHER_FLUSH_PATH);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    try {
      connection.setRequestMethod("POST");
      connection.setRequestProperty("CQ-Action", "Activate");
      connection.setRequestProperty("CQ-Handle", path);
      connection.setRequestProperty("Content-Length", "0");
      return connection.getResponseCode();
    } finally {
      connection.disconnect();
    }
  }
}
