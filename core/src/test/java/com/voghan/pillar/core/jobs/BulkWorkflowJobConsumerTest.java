package com.voghan.pillar.core.jobs;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.voghan.pillar.common.emails.EmailUtil;
import com.voghan.pillar.common.emails.SimpleEmailService;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static com.voghan.pillar.core.jobs.BulkWorkflowJobConsumer.CONTENT_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BulkWorkflowJobConsumerTest {

    @InjectMocks
    private BulkWorkflowJobConsumer fixture;

    @Mock
    private ResourceResolverFactory resourceResolverFactory;

    @Mock
    private SimpleEmailService simpleEmailService;

    @Mock
    private Externalizer externalizer;

    @Mock
    private ResourceResolver resourceResolver;

    @Test
    void process_workspaceCompletes_sendEmail() throws LoginException {
        Job job = mock(Job.class);
        String path = "/etc/acs-commons/bulk-workflow-manager/demo/publish-pages0/jcr:content/workspace";
        String userId = "billg";
        String email = "billg@ms.com";
        String pageTitle = "Page Title";
        Resource resource = mock(Resource.class);
        ValueMap workspaceVM = mock(ValueMap.class);
        PageManager pageManager = mock(PageManager.class);
        Page page = mock(Page.class);
        ValueMap pageVm = mock(ValueMap.class);


        try(MockedStatic<EmailUtil> mockedStatic = mockStatic(EmailUtil.class)) {
            mockedStatic.when(() -> EmailUtil.getUserEmail(resourceResolver, userId)).thenReturn(email);
            when(resourceResolverFactory.getServiceResourceResolver(anyMap())).thenReturn(resourceResolver);
            when(job.getProperty(CONTENT_PATH, String.class)).thenReturn(path);
            when(resourceResolver.getResource(path)).thenReturn(resource);
            when(resource.getName()).thenReturn("workspace");
            when(resource.getValueMap()).thenReturn(workspaceVM);
            when(workspaceVM.get("status", String.class)).thenReturn("COMPLETED");
            when(workspaceVM.get("totalCount", Long.class)).thenReturn(10L);
            when(workspaceVM.get("completeCount", Long.class)).thenReturn(10L);
            when(workspaceVM.get("failedCount", Long.class)).thenReturn(0L);
            when(workspaceVM.get("workflowInstanceId", String.class)).thenReturn("/var/workflow/instance/server0/mine");
            when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
            when(pageManager.getContainingPage(resource)).thenReturn(page);
            when(page.getLastModifiedBy()).thenReturn(userId);
            when(page.getTitle()).thenReturn(pageTitle);
            when(page.getProperties()).thenReturn(pageVm);
            when(pageVm.get("workflowModel", String.class)).thenReturn("publish-example");
            ArgumentCaptor<Map<String, String>> params = ArgumentCaptor.forClass(Map.class);

            JobConsumer.JobResult result =  fixture.process(job);

            assertEquals(JobConsumer.JobResult.OK, result);
            verify(simpleEmailService, times(1)).sendEmail(eq(email), anyString(), params.capture());
            assertEquals(7, params.getValue().size());
            assertEquals(userId, params.getValue().get("userId"));
            assertEquals(pageTitle, params.getValue().get("pageTitle"));
            assertEquals("10", params.getValue().get("totalCount"));
            assertEquals("10", params.getValue().get("completeCount"));
            assertEquals("0", params.getValue().get("failedCount"));
            assertEquals("publish-example", params.getValue().get("workflowModel"));
        }
    }

    @Test
    void process_workspaceRunning_doesNotSendEmail() throws LoginException {
        Job job = mock(Job.class);
        String path = "/etc/acs-commons/bulk-workflow-manager/demo/publish-pages0/jcr:content/workspace";
        Resource resource = mock(Resource.class);
        ValueMap workspaceVM = mock(ValueMap.class);

        when(resourceResolverFactory.getServiceResourceResolver(anyMap())).thenReturn(resourceResolver);
        when(job.getProperty(CONTENT_PATH, String.class)).thenReturn(path);
        when(resourceResolver.getResource(path)).thenReturn(resource);
        when(resource.getName()).thenReturn("workspace");
        when(resource.getValueMap()).thenReturn(workspaceVM);
        when(workspaceVM.get("status", String.class)).thenReturn("RUNNING");
        when(workspaceVM.get("totalCount", Long.class)).thenReturn(100L);
        when(workspaceVM.get("completeCount", Long.class)).thenReturn(10L);
        when(workspaceVM.get("workflowInstanceId", String.class)).thenReturn("/var/workflow/instance/server0/mine");
        ArgumentCaptor<Map<String, String>> params = ArgumentCaptor.forClass(Map.class);

        JobConsumer.JobResult result =  fixture.process(job);

        assertEquals(JobConsumer.JobResult.OK, result);
        verify(simpleEmailService, times(0)).sendEmail(anyString(), anyString(), params.capture());
    }

    @Test
    void process_payloadRunning_doesNotSendEmail() throws LoginException {
        Job job = mock(Job.class);
        String path = "/etc/acs-commons/bulk-workflow-manager/demo/publish-pages0/jcr:content/workspace/payload";
        Resource resource = mock(Resource.class);
        ValueMap workspaceVM = mock(ValueMap.class);

        when(resourceResolverFactory.getServiceResourceResolver(anyMap())).thenReturn(resourceResolver);
        when(job.getProperty(CONTENT_PATH, String.class)).thenReturn(path);
        when(resourceResolver.getResource(path)).thenReturn(resource);
        when(resource.getName()).thenReturn("payload");
        when(resource.getValueMap()).thenReturn(workspaceVM);
        when(workspaceVM.get("status", String.class)).thenReturn("RUNNING");
        when(workspaceVM.get("totalCount", Long.class)).thenReturn(null);
        when(workspaceVM.get("completeCount", Long.class)).thenReturn(null);
        when(workspaceVM.get("workflowInstanceId", String.class)).thenReturn("/var/workflow/instance/server0/mine");
        ArgumentCaptor<Map<String, String>> params = ArgumentCaptor.forClass(Map.class);

        JobConsumer.JobResult result =  fixture.process(job);

        assertEquals(JobConsumer.JobResult.OK, result);
        verify(simpleEmailService, times(0)).sendEmail(anyString(), anyString(), params.capture());
    }

    @Test
    void process_loginException_returnCancel() throws LoginException {
        Job job = mock(Job.class);
        when(resourceResolverFactory.getServiceResourceResolver(anyMap())).thenThrow(new LoginException("Login Error"));

        JobConsumer.JobResult result =  fixture.process(job);

        assertEquals(JobConsumer.JobResult.CANCEL, result);
    }

}
