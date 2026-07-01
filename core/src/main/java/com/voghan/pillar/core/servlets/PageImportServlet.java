package com.voghan.pillar.core.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.voghan.pillar.core.jobs.PageImportJobConsumer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(service = {Servlet.class})
@SlingServletResourceTypes(
        resourceTypes = "pillar/components/page/v1/page",
        methods = HttpConstants.METHOD_POST,
        selectors = "page.import",
        extensions = "json")
@ServiceDescription("Simple Demo Servlet")
public class PageImportServlet extends SlingAllMethodsServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageImportServlet.class);
    private static final Gson GSON = new Gson();

    @Reference
    private JobManager jobManager;

    @Override
    protected void doPost(@NotNull SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject result = new JsonObject();

        try {
            // Read post data
            String body = IOUtils.toString(request.getReader());
            LOGGER.debug("Post request received for {}", body);

            // Validate input
            if (StringUtils.isBlank(body)) {
                response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
                result.addProperty("success", false);
                result.addProperty("error", "Body content is required.");
                response.getWriter().write(GSON.toJson(result));
                return;
            }
            JsonObject payload = GSON.fromJson(body, JsonObject.class);

            Map<String, Object> params = new HashMap<>();
            params.put(PageImportJobConsumer.PAGE_DATA, body);
            Job job = jobManager.addJob(PageImportJobConsumer.JOB_TOPIC, params);
            if (job == null) {
                String message = "Unable to create job to import page";
                LOGGER.info(message);
                response.setStatus(SlingHttpServletResponse.SC_SERVICE_UNAVAILABLE);
                result.addProperty("success", false);
                result.addProperty("message", message);
            } else {
                String message = "Page import job started " + job.getId();
                LOGGER.info(message);
                response.setStatus(SlingHttpServletResponse.SC_OK);
                result.addProperty("success", true);
                result.addProperty("message", message);
            }

        } catch (JsonSyntaxException e) {
            LOGGER.error("Error processing page import", e);
            response.setStatus(SlingHttpServletResponse.SC_BAD_REQUEST);
            result.addProperty("success", false);
            result.addProperty("error", "Post data is not formatted correctly.");
        } catch (Exception e) {
            LOGGER.error("Error processing page import", e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.addProperty("success", false);
            result.addProperty("error", "An internal error occurred.");
        }

        response.getWriter().write(GSON.toJson(result));
    }
}
