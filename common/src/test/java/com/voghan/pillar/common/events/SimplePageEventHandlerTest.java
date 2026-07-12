package com.voghan.pillar.common.events;

import com.day.cq.wcm.api.PageEvent;
import com.day.cq.wcm.api.PageModification;
import com.voghan.pillar.common.jobs.SimpleJobConsumer;
import org.apache.sling.event.jobs.JobManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.event.Event;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SimplePageEventHandlerTest {

    private static final String PAGE_PATH = "/content/pillar/language-head/en/test-page";
    private static final String DEST_PATH  = "/content/pillar/language-head/en/moved-page";

    private final TestLogger logger = TestLoggerFactory.getTestLogger(SimplePageEventHandler.class);

    @Mock
    private JobManager jobManager;

    @InjectMocks
    private SimplePageEventHandler fixture;

    @BeforeEach
    void setup() {
        TestLoggerFactory.clear();
    }

    // --- CREATED ---

    @Test
    void handleEvent_pageCreated_addsJobWithAddedType() {
        Event event = pageEvent(PageModification.created(PAGE_PATH, null, null));

        fixture.handleEvent(event);

        Map<String, Object> params = captureJobParams();
        assertAll(
            () -> assertEquals(PAGE_PATH, params.get(SimpleJobConsumer.JOB_PATH)),
            () -> assertEquals(SimpleJobConsumer.JobType.ADDED.toString(), params.get(SimpleJobConsumer.JOB_TYPE))
        );
    }

    @Test
    void handleEvent_pageModified_loggedEvent() {
        Event event = pageEvent(PageModification.modified(PAGE_PATH, null, null, null));

        fixture.handleEvent(event);

        assertAll(
                () -> assertEquals(2, logger.getLoggingEvents().size()),
                () -> assertEquals(Level.INFO, logger.getLoggingEvents().getFirst().getLevel()),
                () -> assertEquals("Page modified: {}", logger.getLoggingEvents().getFirst().getMessage())
        );
    }

    @Test
    void handleEvent_pageDeleted_loggedEvent() {
        Event event = pageEvent(PageModification.deleted(PAGE_PATH, null));

        fixture.handleEvent(event);

        assertAll(
                () -> assertEquals(2, logger.getLoggingEvents().size()),
                () -> assertEquals(Level.INFO, logger.getLoggingEvents().getFirst().getLevel()),
                () -> assertEquals("Page deleted: {}", logger.getLoggingEvents().getFirst().getMessage())
        );
    }

    @Test
    void handleEvent_pageMoved_loggedEvent() {
        Event event = pageEvent(PageModification.moved(PAGE_PATH, null, null, null));

        fixture.handleEvent(event);

        assertAll(
                () -> assertEquals(2, logger.getLoggingEvents().size()),
                () -> assertEquals(Level.INFO, logger.getLoggingEvents().getFirst().getLevel()),
                () -> assertEquals("Page moved: {} -> {}", logger.getLoggingEvents().getFirst().getMessage())
        );
    }

    @Test
    void handleEvent_pageRolledOut_loggedEvent() {
        Event event = pageEvent(PageModification.rolledout(PAGE_PATH, null));

        fixture.handleEvent(event);

        assertAll(
                () -> assertEquals(1, logger.getLoggingEvents().size()),
                () -> assertEquals(Level.INFO, logger.getLoggingEvents().getFirst().getLevel()),
                () -> assertEquals("Page event {}: {}", logger.getLoggingEvents().getFirst().getMessage())
        );
    }


    // --- OTHER types (VERSION_CREATED, ROLLEDOUT, etc.) hit the default branch ---

    @Test
    void handleEvent_pageVersionCreated_doesNotAddJob() {
        Event event = pageEvent(PageModification.versionCreated(PAGE_PATH, null, null));

        fixture.handleEvent(event);

        verify(jobManager, times(0)).addJob(eq(SimpleJobConsumer.JOB_TOPIC), org.mockito.ArgumentMatchers.anyMap());
    }


    // --- helpers ---

    /** Wraps a single PageModification into an OSGi Event via PageEvent. */
    private static Event pageEvent(PageModification modification) {
        return new PageEvent(modification).toEvent();
    }

    /** Captures and returns the params map passed to the first addJob invocation. */
    @SuppressWarnings("unchecked")
    private Map<String, Object> captureJobParams() {
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(jobManager, times(1)).addJob(eq(SimpleJobConsumer.JOB_TOPIC), captor.capture());
        return captor.getValue();
    }
}
