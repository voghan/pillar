package com.voghan.pillar.common.queries.impl;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.voghan.pillar.common.queries.SimpleQueryBuilder;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(
    immediate = true,
    service = SimpleQueryBuilder.class,
    property = {
        Constants.SERVICE_ID + "=Simple Query Builder",
        Constants.SERVICE_DESCRIPTION + "=Pillar service to query the JCR"
    }
)
public class SimpleQueryBuilderImpl implements SimpleQueryBuilder {
    private static final Logger logger = LoggerFactory.getLogger(SimpleQueryBuilderImpl.class);

    final static String AEM_DATE_FORMAT = ("YYYY-MM-DD'T'HH:mm:ss.SSSZ");

    @Reference
    private QueryBuilder queryBuilder;

    @Override
    public List<Resource> search(SlingHttpServletRequest request, Map<String, String> predicatesMap) {
        logger.info("Performing search for {}", predicatesMap.values());
        final List<Resource> results = new ArrayList<>();
        ResourceResolver resourceResolver = request.getResourceResolver();
        PredicateGroup predicateGroup = PredicateGroup.create(predicatesMap);
        Query query = queryBuilder.createQuery(predicateGroup, resourceResolver.adaptTo(Session.class));
        SearchResult result = query.getResult();

        // QueryBuilder has a leaking ResourceResolver, so the following workaround is required.
        ResourceResolver leakingResourceResolver = null;
        try {
            for (final Hit hit : result.getHits()) {
                if (leakingResourceResolver == null) {
                    // Get a reference to QB's leaking ResourceResolver
                    leakingResourceResolver = hit.getResource().getResourceResolver();
                }
                results.add(resourceResolver.getResource(hit.getPath()));
            }

        } catch (RepositoryException e) {
            logger.error("Error collecting search results", e);
        } finally {
            if (leakingResourceResolver != null) {
                leakingResourceResolver.close();
            }
        }

        return results;
    }
}
