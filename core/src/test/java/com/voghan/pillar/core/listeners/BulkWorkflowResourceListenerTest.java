package com.voghan.pillar.core.listeners;

import com.voghan.pillar.core.jobs.BulkWorkflowJobConsumer;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChange.ChangeType;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BulkWorkflowResourceListenerTest {

    private static final String BULK_WF_PATH = "/etc/acs-commons/bulk-workflow-manager/jcr:content/workspace";
    private static final String BULK_WF_OTHER_PATH = "/etc/acs-commons/bulk-workflow-manager/jcr:content/workspace/payload";

    @Mock
    private JobManager jobManager;

    @InjectMocks
    private BulkWorkflowResourceListener fixture;

    @Test
    void onChange_changed_addsJob() {
        ResourceChange change = mock(ResourceChange.class);
        when(change.getPath()).thenReturn(BULK_WF_PATH);
        when(change.getType()).thenReturn(ChangeType.CHANGED);
        when(jobManager.addJob(anyString(), anyMap())).thenReturn(mock(Job.class));

        fixture.onChange(List.of(change));

        ArgumentCaptor<Map<String, Object>> paramsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(jobManager, times(1)).addJob(eq(BulkWorkflowJobConsumer.JOB_TOPIC), paramsCaptor.capture());

        Map<String, Object> params = paramsCaptor.getValue();
        assertEquals(BULK_WF_PATH, params.get(BulkWorkflowJobConsumer.CONTENT_PATH));
    }

    @Test
    void onChange_addedEvent_doesNotAddJob() {
        ResourceChange change = mock(ResourceChange.class);
        when(change.getType()).thenReturn(ChangeType.ADDED);

        fixture.onChange(List.of(change));

        verify(jobManager, never()).addJob(any(), anyMap());
    }

    @Test
    void onChange_removedEvent_doesNotAddJob() {
        ResourceChange change = mock(ResourceChange.class);
        when(change.getType()).thenReturn(ChangeType.REMOVED);

        fixture.onChange(List.of(change));

        verify(jobManager, never()).addJob(any(), anyMap());
    }

    @Test
    void onChange_multipleChanges_onlyAddsJobForStatusChange() {
        ResourceChange change  = mock(ResourceChange.class);
        when(change.getPath()).thenReturn(BULK_WF_PATH);
        when(change.getType()).thenReturn(ChangeType.CHANGED);

        ResourceChange removed = mock(ResourceChange.class);
        when(removed.getPath()).thenReturn(BULK_WF_OTHER_PATH);
        when(removed.getType()).thenReturn(ChangeType.CHANGED);

        fixture.onChange(List.of(change, removed));

        verify(jobManager, times(1)).addJob(eq(BulkWorkflowJobConsumer.JOB_TOPIC), anyMap());
    }

    @Test
    void onChange_emptyChangeList_doesNotAddJob() {
        fixture.onChange(List.of());

        verify(jobManager, never()).addJob(any(), anyMap());
    }


}
