package com.leanengine.server.rest;

import com.google.appengine.api.datastore.Entity;
import com.leanengine.server.JsonUtils;
import com.leanengine.server.LeanException;
import com.leanengine.server.appengine.DatastoreUtils;
import com.leanengine.server.entity.*;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import rest.o.gram.entities.QueryReference;

import javax.ws.rs.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

@Path("/v1/query")
@Produces("application/json")
@Consumes("application/json")
public class QueryRest {

    private static final Logger log = Logger.getLogger(QueryRest.class.getName());

    @POST
    @Path("/")
    public JsonNode query(String queryJson) throws LeanException {
        final LeanQuery leanQuery = LeanQuery.fromJson(queryJson);
        final QueryResult result = DatastoreUtils.queryEntityPrivate(leanQuery);
        final List<Entity> entities = result.getResult();

        // handling cross(join) query
        final QueryReference queryReference = leanQuery.getReference();
        if (queryReference != null)
        {
            HashMap<String, Entity> refPropertyToEntity = new HashMap<>();
            final String[] keys = new String[entities.size()];
            for (int i = 0; i < entities.size(); ++i)
            {
                final Entity currEntity = entities.get(i);
                keys[i] = (String)currEntity.getProperty(queryReference.getProperty());
                refPropertyToEntity.put(keys[i], currEntity);
            }
            final Collection<Entity> joinedEntities =
                    DatastoreUtils.getPublicEntities(queryReference.getKind(), keys);
            // TODO: make sure private data is set(id, props other than ref prop)
            for (final Entity currEntity : joinedEntities)
                currEntity.setPropertiesFrom(refPropertyToEntity.get(currEntity.getProperty(queryReference.getProperty()))); // TODO: resets or overrides?
        }
        ObjectNode jsonResult = JsonUtils.entityListToJson(entities);
        if (result.getCursor() != null) {
            jsonResult.put("cursor", result.getCursor().toWebSafeString());
        }
        return jsonResult;
    }

    @GET
    @Path("/example")
    public JsonNode exampleQuery() throws LeanException {
        LeanQuery query = new LeanQuery("somekind");
        query.addFilter("prop1", QueryFilter.FilterOperator.EQUAL, "value1");
        query.addFilter("prop2", QueryFilter.FilterOperator.LESS_THAN_OR_EQUAL, 1.23);
        query.addFilter("prop2", QueryFilter.FilterOperator.GREATER_THAN_OR_EQUAL, 0.5);
        query.addSort("prop2", QuerySort.SortDirection.ASCENDING);
        query.addSort("prop3", QuerySort.SortDirection.DESCENDING);
        return query.toJson();
    }
}
