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
package com.voghan.pillar.core.servlets;

import com.voghan.pillar.core.jobs.PageImportJobConsumer;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class PageImportServletTest {

  @InjectMocks
  private PageImportServlet fixture;

  @Mock
  JobManager jobManager;

  @Test
  void doPost_validJson_returnsOk(AemContext context) throws ServletException, IOException {
    String body = "{"
        + "\"page\":{\"title\":\"Home\",\"name\":\"home\",\"template\":\"simple\",\"description\":\"desc\"},"
        + "\"body\":{\"sling:resourceType\":\"pillar/components/container/v1/container\"}"
        + "}";
    Job job = mock(Job.class);
    when(jobManager.addJob(eq(PageImportJobConsumer.JOB_TOPIC), anyMap())).thenReturn(job);
    when(job.getId()).thenReturn("job-id");

    MockSlingHttpServletResponse response = post(context, body);

    assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
    assertTrue(response.getContentType().startsWith("application/json"), response.getContentType());
    String out = response.getOutputAsString();
    assertTrue(out.contains("\"success\":true"), out);
    assertTrue(out.contains("Page import job started job-id"), out);
  }

  @Test
  void doPost_blankBody_returnsBadRequest(AemContext context) throws ServletException, IOException {
    MockSlingHttpServletResponse response = post(context, "   ");

    assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    String out = response.getOutputAsString();
    assertTrue(out.contains("\"success\":false"), out);
    assertTrue(out.contains("Body content is required."), out);
  }

  @Test
  void doPost_malformedJson_returnsBadRequest(AemContext context) throws ServletException, IOException {
    MockSlingHttpServletResponse response = post(context, "{ not json");

    assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    String out = response.getOutputAsString();
    assertTrue(out.contains("\"success\":false"), out);
    assertTrue(out.contains("Post data is not formatted correctly."), out);
  }

  @Test
  void doPost_missingRequiredField_returnsServiceUnavailable(AemContext context) throws ServletException, IOException {
    // "page" object present but required "title" scalar is absent -> NPE caught as 500
    MockSlingHttpServletResponse response = post(context, "{\"page\":{},\"body\":{}}");

    assertEquals(SlingHttpServletResponse.SC_SERVICE_UNAVAILABLE, response.getStatus());
    assertTrue(response.getOutputAsString().contains("\"success\":false"));
  }

  @Test
  void doPost_missingRequiredField_returnsServerError(AemContext context) throws ServletException, IOException {
    // "page" object present but required "title" scalar is absent -> NPE caught as 500
    String body = "{"
            + "\"page\":{\"title\":\"Home\",\"name\":\"home\",\"template\":\"simple\",\"description\":\"desc\"},"
            + "\"body\":{\"sling:resourceType\":\"pillar/components/container/v1/container\"}"
            + "}";
    Job job = mock(Job.class);
    when(jobManager.addJob(eq(PageImportJobConsumer.JOB_TOPIC), anyMap())).thenReturn(job);
    when(job.getId()).thenThrow(new RuntimeException("internal error"));

    MockSlingHttpServletResponse response = post(context, body);

    assertEquals(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    assertTrue(response.getOutputAsString().contains("\"success\":false"));
  }

  private MockSlingHttpServletResponse post(AemContext context, String body)
      throws ServletException, IOException {
    MockSlingHttpServletRequest request = context.request();
    request.setContentType("application/json");
    request.setContent(body.getBytes(StandardCharsets.UTF_8));

    MockSlingHttpServletResponse response = context.response();
    fixture.doPost(request, response);
    return response;
  }
}
