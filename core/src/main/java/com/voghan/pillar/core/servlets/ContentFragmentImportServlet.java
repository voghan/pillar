package com.voghan.pillar.core.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.voghan.pillar.core.jobs.ContentFragmentImportJobConsumer;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
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
        resourceTypes = "pillar/endpoints/cfm-import/v1",
        methods = HttpConstants.METHOD_POST,
        extensions = "json")
@ServiceDescription("Service creates content fragments from JSON posted to the servlet")
public class ContentFragmentImportServlet extends AbstractImportServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentFragmentImportServlet.class);
    private static final Gson GSON = new Gson();

    private static final List<String> REQUIRED_FIELDS =
            List.of("title", "name", "model", "data", "path");

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
                LOGGER.info(CONTENT_FAILED_VALIDATION);
                handleErrorResponse(response, result, SlingHttpServletResponse.SC_BAD_REQUEST, CONTENT_FAILED_VALIDATION);
                response.getWriter().write(GSON.toJson(result));
                return;
            }

            Map<String, Object> params = new HashMap<>();
            params.put(ContentFragmentImportJobConsumer.IMPORT_DATA, body);
            Job job = jobManager.addJob(ContentFragmentImportJobConsumer.JOB_TOPIC, params);
            if (job == null) {
                LOGGER.info(JOB_CREATION_ERROR_MESSAGE);
                handleErrorResponse(response, result, SlingHttpServletResponse.SC_SERVICE_UNAVAILABLE, JOB_CREATION_ERROR_MESSAGE);
            } else {
                String message = "Content Fragment import job started " + job.getId();
                LOGGER.info(message);
                response.setStatus(SlingHttpServletResponse.SC_OK);
                result.addProperty("success", true);
                result.addProperty("message", message);
            }
        } catch (
        JsonSyntaxException e) {
            LOGGER.error("Error processing content fragment import", e);
            handleErrorResponse(response, result, SlingHttpServletResponse.SC_BAD_REQUEST, CONTENT_FAILED_VALIDATION);
        } catch (Exception e) {
            LOGGER.error("Error processing content fragment import", e);
            handleErrorResponse(response, result, SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR, INTERNAL_ERROR_OCCURRED);
        }

        response.getWriter().write(GSON.toJson(result));
    }

    private static boolean isValidRequest(@NotNull String body) {
        JsonObject payload = GSON.fromJson(body, JsonObject.class);
        if (payload == null
                || !hasRequiredObjects(payload, List.of("content"))) {
            return false;
        }

        JsonObject content = payload.getAsJsonObject("content");
        if (!hasRequiredScalars(content, REQUIRED_FIELDS) ||
                !hasRequiredObjects(content, List.of("data"))){
            return false;
        }

        return true;
    }
}
