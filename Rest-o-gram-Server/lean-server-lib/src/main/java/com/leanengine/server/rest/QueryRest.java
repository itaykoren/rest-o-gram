package com.leanengine.server.rest;

import com.leanengine.server.JsonUtils;
import com.leanengine.server.LeanException;
import com.leanengine.server.appengine.DatastoreUtils;
import com.leanengine.server.entity.LeanQuery;
import com.leanengine.server.entity.QueryFilter;
import com.leanengine.server.entity.QueryResult;
import com.leanengine.server.entity.QuerySort;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import javax.ws.rs.*;
import java.util.logging.Logger;

@Path("/v1/query")
@Produces("application/json;charset=UTF-8")
@Consumes("application/json;charset=UTF-8")
public class QueryRest {

    private static final Logger log = Logger.getLogger(QueryRest.class.getName());

    @POST
    @Path("/")
    public JsonNode query(String queryJson) throws LeanException {
        log.severe("QUERY REST");
        QueryResult result = DatastoreUtils.queryEntityPrivate(LeanQuery.fromJson(queryJson));
        ObjectNode jsonResult = JsonUtils.entityListToJson(result.getResult());
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
