package com.voghan.pillar.core.servlets;

import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import java.util.List;

public class AbstractImportServlet extends SlingAllMethodsServlet {

    public static final String CONTENT_FAILED_VALIDATION = "Post content failed validation.";
    public static final String INTERNAL_ERROR_OCCURRED = "An internal error occurred.";
    public static final String JOB_CREATION_ERROR_MESSAGE = "Unable to create job to import data";

    public static void handleErrorResponse(SlingHttpServletResponse response, JsonObject result, int statusCode, String message) {
        response.setStatus(statusCode);
        result.addProperty("success", false);
        result.addProperty("error", message);
    }

    public static boolean hasRequiredScalars(JsonObject json, List<String> fields) {
        for (String field : fields) {
            if (!json.has(field) || json.get(field).isJsonNull()) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasRequiredPrimitives(JsonObject json, List<String> fields) {
        for (String field : fields) {
            if (!json.has(field) || !json.get(field).isJsonPrimitive()) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasRequiredObjects(JsonObject json, List<String> fields) {
        for (String field : fields) {
            if (!json.has(field) || !json.get(field).isJsonObject()) {
                return false;
            }
        }
        return true;
    }
}
