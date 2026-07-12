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
package com.voghan.pillar.common.listeners;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.voghan.pillar.common.jobs.SimpleJobConsumer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChange.ChangeType;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

@ExtendWith(MockitoExtension.class)
class SimpleResourceListenerTest {

  private static final TestLogger logger = TestLoggerFactory.getTestLogger(
      SimpleResourceListener.class);
  public static final String CONTENT_PATH = "/content/test";

  @Mock
  private JobManager jobManager;

  @InjectMocks
  private SimpleResourceListener fixture;

  @BeforeEach
  void setup() {
    TestLoggerFactory.clear();
  }

  @Test
  void handleEvent_changeTypeAdded_createsJob() {

    ResourceChange change = new ResourceChange(ChangeType.ADDED, CONTENT_PATH, false);

    when(jobManager.addJob(anyString(), anyMap())).thenReturn(mock(Job.class));
    fixture.onChange(List.of(change));

    Map<String, Object> params = captureJobParams();
    assertAll(
            () -> assertEquals(CONTENT_PATH, params.get(SimpleJobConsumer.JOB_PATH)),
            () -> assertEquals(SimpleJobConsumer.JobType.ADDED.toString(), params.get(SimpleJobConsumer.JOB_TYPE))
    );

    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(1, events.size());
    LoggingEvent event = events.getFirst();

    assertAll(
        () -> assertEquals(Level.DEBUG, event.getLevel()),
        () -> assertEquals(3, event.getArguments().size()),
        () -> assertEquals(ChangeType.ADDED, event.getArguments().get(0)),
        () -> assertEquals(CONTENT_PATH, event.getArguments().get(1)),
        () -> assertEquals(Boolean.FALSE, event.getArguments().get(2))
    );
  }

  @Test
  void handleEvent_changeTypeChanged_createsJob() {

    ResourceChange change = new ResourceChange(ChangeType.CHANGED, CONTENT_PATH, false);

    when(jobManager.addJob(anyString(), anyMap())).thenReturn(mock(Job.class));
    fixture.onChange(List.of(change));

    Map<String, Object> params = captureJobParams();
    assertAll(
            () -> assertEquals(CONTENT_PATH, params.get(SimpleJobConsumer.JOB_PATH)),
            () -> assertEquals(SimpleJobConsumer.JobType.CHANGED.toString(), params.get(SimpleJobConsumer.JOB_TYPE))
    );

    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(1, events.size());
    LoggingEvent event = events.getFirst();

    assertAll(
            () -> assertEquals(Level.DEBUG, event.getLevel()),
            () -> assertEquals(3, event.getArguments().size()),
            () -> assertEquals(ChangeType.CHANGED, event.getArguments().get(0)),
            () -> assertEquals(CONTENT_PATH, event.getArguments().get(1)),
            () -> assertEquals(Boolean.FALSE, event.getArguments().get(2))
    );
  }

  @Test
  void handleEvent_changeTypeRemoved_createsJob() {

    ResourceChange change = new ResourceChange(ChangeType.REMOVED, CONTENT_PATH, false);

    when(jobManager.addJob(anyString(), anyMap())).thenReturn(mock(Job.class));
    fixture.onChange(List.of(change));

    Map<String, Object> params = captureJobParams();
    assertAll(
            () -> assertEquals(CONTENT_PATH, params.get(SimpleJobConsumer.JOB_PATH)),
            () -> assertEquals(SimpleJobConsumer.JobType.REMOVED.toString(), params.get(SimpleJobConsumer.JOB_TYPE))
    );

    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(1, events.size());
    LoggingEvent event = events.getFirst();

    assertAll(
            () -> assertEquals(Level.DEBUG, event.getLevel()),
            () -> assertEquals(3, event.getArguments().size()),
            () -> assertEquals(ChangeType.REMOVED, event.getArguments().get(0)),
            () -> assertEquals(CONTENT_PATH, event.getArguments().get(1)),
            () -> assertEquals(Boolean.FALSE, event.getArguments().get(2))
    );
  }

  @Test
  void handleEvent_changeTypeProvidedAdded_doesNotCreateJob() {

    ResourceChange change = new ResourceChange(ChangeType.PROVIDER_ADDED, CONTENT_PATH, false);

    fixture.onChange(Arrays.asList(change));

    verify(jobManager, times(0)).addJob(anyString(), anyMap());

    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(2, events.size());
    LoggingEvent event = events.getFirst();

    assertAll(
            () -> assertEquals(Level.DEBUG, event.getLevel()),
            () -> assertEquals(3, event.getArguments().size()),
            () -> assertEquals(ChangeType.PROVIDER_ADDED, event.getArguments().getFirst()),
            () -> assertEquals(CONTENT_PATH, event.getArguments().get(1)),
            () -> assertEquals(Boolean.FALSE, event.getArguments().get(2))
    );
  }

  @Test
  void handleEvent_changeTypeAdded_nullJob() {

    ResourceChange change = new ResourceChange(ChangeType.ADDED, CONTENT_PATH, false);

    fixture.onChange(List.of(change));

    Map<String, Object> params = captureJobParams();
    assertAll(
            () -> assertEquals(CONTENT_PATH, params.get(SimpleJobConsumer.JOB_PATH)),
            () -> assertEquals(SimpleJobConsumer.JobType.ADDED.toString(), params.get(SimpleJobConsumer.JOB_TYPE))
    );

    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(2, events.size());
    LoggingEvent event = events.get(1);

    assertAll(
            () -> assertEquals(Level.WARN, event.getLevel()),
            () -> assertEquals(2, event.getArguments().size()),
            () -> assertEquals(SimpleJobConsumer.JobType.ADDED.toString(), event.getArguments().get(1)),
            () -> assertEquals(CONTENT_PATH, event.getArguments().getFirst())
    );
  }

  private Map<String, Object> captureJobParams() {
    ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
    verify(jobManager, times(1)).addJob(eq(SimpleJobConsumer.JOB_TOPIC), captor.capture());
    return captor.getValue();
  }
}