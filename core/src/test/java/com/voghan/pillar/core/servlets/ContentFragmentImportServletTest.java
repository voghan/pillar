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

import com.voghan.pillar.core.jobs.ContentFragmentImportJobConsumer;
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

import static com.voghan.pillar.core.servlets.AbstractImportServlet.CONTENT_FAILED_VALIDATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ContentFragmentImportServletTest {

  private static final String VALID_PAYLOAD =
          "{\n" +
          "  \"content\": {\n" +
          "    \"title\": \"downhill-skiing-snowbird\",\n" +
          "    \"name\": \"downhill-skiing-snowbird\",\n" +
          "    \"path\": \"/content/dam/pillar/cfm/basic-cards/imports\",\n" +
          "    \"model\": \"/conf/pillar/settings/dam/cfm/models/card\",\n" +
          "    \"data\": {\n" +
          "      \"master\": {\n" +
          "        \"headline\": \"Downhill Skiing Snowbird\",\n" +
          "        \"shortDescription\": \"\\u003Cp\\u003ESituated just 29 miles from Salt Lake City in Utah's rugged Little Cottonwood Canyon, the resort shares a massive ridge with its neighbor, Alta, and spans 2,500 acres of skiable terrain.\\u003C/p\\u003E\\n\",\n" +
          "        \"callToActions\": [\n" +
          "          {\n" +
          "            \"linkText\":\"Learn more\",\n" +
          "            \"linkPath\": \"/content/page/learn\"\n" +
          "          }\n" +
          "        ]\n" +
          "      }\n" +
          "    }\n" +
          "  }\n" +
          "}";

  @InjectMocks
  private ContentFragmentImportServlet fixture;

  @Mock
  JobManager jobManager;

  @Test
  void doPost_validJson_returnsOk(AemContext context) throws ServletException, IOException {
    String body = VALID_PAYLOAD;
    Job job = mock(Job.class);
    when(jobManager.addJob(eq(ContentFragmentImportJobConsumer.JOB_TOPIC), anyMap())).thenReturn(job);
    when(job.getId()).thenReturn("job-id");

    MockSlingHttpServletResponse response = post(context, body);

    assertEquals(SlingHttpServletResponse.SC_OK, response.getStatus());
    assertTrue(response.getContentType().startsWith("application/json"), response.getContentType());
    String out = response.getOutputAsString();
    assertTrue(out.contains("\"success\":true"), out);
    assertTrue(out.contains("Content Fragment import job started job-id"), out);
  }

  @Test
  void doPost_blankBody_returnsBadRequest(AemContext context) throws ServletException, IOException {
    MockSlingHttpServletResponse response = post(context, "   ");

    assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    String out = response.getOutputAsString();
    assertTrue(out.contains("\"success\":false"), out);
    assertTrue(out.contains(CONTENT_FAILED_VALIDATION), out);
  }

  @Test
  void doPost_malformedJson_returnsBadRequest(AemContext context) throws ServletException, IOException {
    MockSlingHttpServletResponse response = post(context, "{ not json");

    assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    String out = response.getOutputAsString();
    assertTrue(out.contains("\"success\":false"), out);
    assertTrue(out.contains(CONTENT_FAILED_VALIDATION), out);
  }

  @Test
  void doPost_missingPageJson_returnsBadRequest(AemContext context) throws ServletException, IOException {
    MockSlingHttpServletResponse response = post(context, "{\"body\":{} }");

    assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    String out = response.getOutputAsString();
    assertTrue(out.contains("\"success\":false"), out);
    assertTrue(out.contains(CONTENT_FAILED_VALIDATION), out);
  }

  @Test
  void doPost_missingBodyJson_returnsBadRequest(AemContext context) throws ServletException, IOException {
    MockSlingHttpServletResponse response = post(context, "{\"page\":{} }");

    assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    String out = response.getOutputAsString();
    assertTrue(out.contains("\"success\":false"), out);
    assertTrue(out.contains(CONTENT_FAILED_VALIDATION), out);
  }

  @Test
  void doPost_primitiveBodyJson_returnsBadRequest(AemContext context) throws ServletException, IOException {
    MockSlingHttpServletResponse response = post(context, "{\"page\":{},\"body\":\"primative\"}");

    assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    String out = response.getOutputAsString();
    assertTrue(out.contains("\"success\":false"), out);
    assertTrue(out.contains(CONTENT_FAILED_VALIDATION), out);
  }

  @Test
  void doPost_primitivePageJson_returnsBadRequest(AemContext context) throws ServletException, IOException {
    MockSlingHttpServletResponse response = post(context, "{\"page\":\"primative\",\"body\":{}}");

    assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    String out = response.getOutputAsString();
    assertTrue(out.contains("\"success\":false"), out);
    assertTrue(out.contains(CONTENT_FAILED_VALIDATION), out);
  }

  @Test
  void doPost_pageMissingRequiredField_returnsBadRequest(AemContext context) throws ServletException, IOException {
    // "page" is an object but lacks the required "path"/"title"/... scalars.
    MockSlingHttpServletResponse response = post(context, "{\"page\":{\"name\":\"home\"},\"body\":{}}");

    assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    String out = response.getOutputAsString();
    assertTrue(out.contains("\"success\":false"), out);
    assertTrue(out.contains(CONTENT_FAILED_VALIDATION), out);
  }

  @Test
  void doPost_bodyMissingResourceType_returnsBadRequest(AemContext context) throws ServletException, IOException {
    String body = "{"
        + "\"page\":{\"title\":\"Home\",\"name\":\"home\",\"template\":\"simple\","
        + "\"description\":\"desc\",\"path\":\"/content/pillar/language-head/en/demo\"},"
        + "\"body\":{\"layout\":\"responsiveGrid\"}"
        + "}";

    MockSlingHttpServletResponse response = post(context, body);

    assertEquals(SlingHttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    String out = response.getOutputAsString();
    assertTrue(out.contains("\"success\":false"), out);
    assertTrue(out.contains(CONTENT_FAILED_VALIDATION), out);
  }

  @Test
  void doPost_jobNotCreated_returnsServiceUnavailable(AemContext context) throws ServletException, IOException {
    // Valid payload, but the job manager cannot enqueue (addJob returns null on the unstubbed mock).
    MockSlingHttpServletResponse response = post(context, VALID_PAYLOAD);

    assertEquals(SlingHttpServletResponse.SC_SERVICE_UNAVAILABLE, response.getStatus());
    assertTrue(response.getOutputAsString().contains("\"success\":false"));
  }

  @Test
  void doPost_jobIdThrows_returnsServerError(AemContext context) throws ServletException, IOException {
    Job job = mock(Job.class);
    when(jobManager.addJob(eq(ContentFragmentImportJobConsumer.JOB_TOPIC), anyMap())).thenReturn(job);
    when(job.getId()).thenThrow(new RuntimeException("internal error"));

    MockSlingHttpServletResponse response = post(context, VALID_PAYLOAD);

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
