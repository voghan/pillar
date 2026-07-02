package com.voghan.pillar.core.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.voghan.pillar.core.jobs.PageImportJobConsumer;
import org.apache.commons.io.IOUtils;
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
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = {Servlet.class})
@SlingServletResourceTypes(
        resourceTypes = "pillar/components/page/v1/page",
        methods = HttpConstants.METHOD_POST,
        selectors = "page.import",
        extensions = "json")
@ServiceDescription("Service creates pages from JSON posted to the servlet")
public class PageImportServlet extends SlingAllMethodsServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageImportServlet.class);
    private static final Gson GSON = new Gson();
    public static final String CONTENT_FAILED_VALIDATION = "Post content failed validation.";
    public static final String INTERNAL_ERROR_OCCURRED = "An internal error occurred.";
    public static final String JOB_CREATION_ERROR_MESSAGE = "Unable to create job to import page";

    // Scalar fields the consumer reads unconditionally; a payload missing any of them
    // would enqueue successfully and then silently fail in the job, so reject it up front.
    private static final List<String> REQUIRED_PAGE_FIELDS =
            List.of("title", "name", "template", "description", "path");
    private static final List<String> REQUIRED_BODY_FIELDS = List.of("sling:resourceType");

    @Reference
    private JobManager jobManager;

    @Override
    protected void doPost(@NotNull SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject result = new JsonObject();

        try {
            // Read post data
            String body = IOUtils.toString(request.getReader());
            LOGGER.debug("Post request received for {}", body);

            // Validate input
            if (!isValidRequest(body)) {
                handleErrorResponse(response, result, SlingHttpServletResponse.SC_BAD_REQUEST, CONTENT_FAILED_VALIDATION);
                response.getWriter().write(GSON.toJson(result));
                return;
            }

            Map<String, Object> params = new HashMap<>();
            params.put(PageImportJobConsumer.PAGE_DATA, body);
            Job job = jobManager.addJob(PageImportJobConsumer.JOB_TOPIC, params);
            if (job == null) {
                handleErrorResponse(response, result, SlingHttpServletResponse.SC_SERVICE_UNAVAILABLE, JOB_CREATION_ERROR_MESSAGE);
            } else {
                String message = "Page import job started " + job.getId();
                LOGGER.info(message);
                response.setStatus(SlingHttpServletResponse.SC_OK);
                result.addProperty("success", true);
                result.addProperty("message", message);
            }

        } catch (JsonSyntaxException e) {
            LOGGER.error("Error processing page import", e);
            handleErrorResponse(response, result, SlingHttpServletResponse.SC_BAD_REQUEST, CONTENT_FAILED_VALIDATION);
        } catch (Exception e) {
            LOGGER.error("Error processing page import", e);
            handleErrorResponse(response, result, SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, INTERNAL_ERROR_OCCURRED);
        }

        response.getWriter().write(GSON.toJson(result));
    }

    private static void handleErrorResponse(SlingHttpServletResponse response, JsonObject result, int statusCode, String message) {
        LOGGER.info(message);
        response.setStatus(statusCode);
        result.addProperty("success", false);
        result.addProperty("error", message);
    }

    private static boolean isValidRequest(@NotNull String body) {
        JsonObject payload = GSON.fromJson(body, JsonObject.class);
        if (payload == null) {
            return false;
        }

        if (!payload.has("page") || !payload.has("body")) {
            return false;
        }

        if (!payload.get("page").isJsonObject() || !payload.get("body").isJsonObject()) {
            return false;
        }

        return hasRequiredScalars(payload.getAsJsonObject("page"), REQUIRED_PAGE_FIELDS)
                && hasRequiredScalars(payload.getAsJsonObject("body"), REQUIRED_BODY_FIELDS);
    }

    private static boolean hasRequiredScalars(JsonObject json, List<String> fields) {
        for (String field : fields) {
            if (!json.has(field) || !json.get(field).isJsonPrimitive()) {
                return false;
            }
        }
        return true;
    }
}
