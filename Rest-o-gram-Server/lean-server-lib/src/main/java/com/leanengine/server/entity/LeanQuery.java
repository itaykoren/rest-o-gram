package com.leanengine.server.entity;

import com.google.appengine.api.datastore.Cursor;
import com.leanengine.server.JsonUtils;
import com.leanengine.server.LeanException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import rest.o.gram.entities.QueryReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LeanQuery {
    private final String kind;
    private List<QueryFilter> filters = new ArrayList<>();
    private List<QuerySort> sorts = new ArrayList<>();
    private Cursor cursor;
    private Integer offset;
    private Integer limit;
    private QueryReference reference;

    public LeanQuery(String kind) {
        this.kind = kind;
    }

    public void addFilter(String property, QueryFilter.FilterOperator operator, Object value) {
        filters.add(new QueryFilter(property, operator, value));
    }

    public void addSort(String property, QuerySort.SortDirection direction) {
        sorts.add(new QuerySort(property, direction));
    }

    public String getKind() {
        return kind;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public List<QuerySort> getSorts() {
        return sorts;
    }

    public List<QueryFilter> getFilters() {
        return filters;
    }

    public JsonNode toJson() throws LeanException {
        ObjectNode json = JsonUtils.getObjectMapper().createObjectNode();
        json.put("kind", kind);

        if (!filters.isEmpty()) {
            ObjectNode jsonFilters = JsonUtils.getObjectMapper().createObjectNode();
            for (QueryFilter filter : filters) {
                ObjectNode jsonFilter;
                if (jsonFilters.has(filter.getProperty())) {
                    jsonFilter = (ObjectNode) jsonFilters.get(filter.getProperty());
                } else {
                    jsonFilter = JsonUtils.getObjectMapper().createObjectNode();
                }
                JsonUtils.addTypedNode(jsonFilter, filter.getOperator().toJSON(), filter.getValue());
                jsonFilters.put(filter.getProperty(),    jsonFilter);
            }
            json.put("filter", jsonFilters);
        }

        if (!sorts.isEmpty()) {
            ObjectNode jsonSorts = JsonUtils.getObjectMapper().createObjectNode();
            for (QuerySort sort : sorts) {
                jsonSorts.put(sort.getProperty(), sort.getDirection().toJSON());
            }
            json.put("sort", jsonSorts);
        }

        if (this.cursor != null) {
           json.put("cursor", cursor.toWebSafeString());
        }

        if (this.reference != null)
        {
            final ObjectNode jsonReference = JsonUtils.getObjectMapper().createObjectNode();
            jsonReference.put("property", reference.getProperty());
            jsonReference.put("kind", reference.getKind());
            json.put("reference", jsonReference);
        }

        return json;
    }

    public static LeanQuery fromJson(String json) throws LeanException {
        ObjectNode jsonNode;
        try {
            jsonNode = (ObjectNode) JsonUtils.getObjectMapper().readTree(json);
        } catch (IOException e) {
            throw new LeanException(LeanException.Error.QueryJSON);
        } catch (ClassCastException cce) {
            throw new LeanException(LeanException.Error.QueryJSON, " Expected JSON object, instead got JSON array.");
        }

        // get the 'kind' of the query
        LeanQuery query = new LeanQuery(jsonNode.get("kind").getTextValue());
        if (query.getKind() == null) {
            throw new LeanException(LeanException.Error.QueryJSON, " Missing 'kind' property.");
        }

        // get 'filter'
        ObjectNode filters;
        try {
            filters = (ObjectNode) jsonNode.get("filter");
        } catch (ClassCastException cce) {
            throw new LeanException(LeanException.Error.QueryJSON, " Property 'filter' must be a JSON object.");
        }
        if (filters != null) {
            Iterator<String> filterIterator = filters.getFieldNames();
            while (filterIterator.hasNext()) {
                String filterProperty = filterIterator.next();
                ObjectNode filter;
                try {
                    filter = (ObjectNode) filters.get(filterProperty);
                } catch (ClassCastException cce) {
                    throw new LeanException(LeanException.Error.QueryJSON, " Filter value must be a JSON object.");
                }
                Iterator<String> operatorIterator = filter.getFieldNames();
                while (operatorIterator.hasNext()) {
                    String operator = operatorIterator.next();
                    Object filterValue = JsonUtils.propertyFromJson(filter.get(operator));
                    query.addFilter(
                            filterProperty,
                            QueryFilter.FilterOperator.create(operator),
                            filterValue);
                }
            }
        }

        // get 'sort'
        ObjectNode sorts;
        try {
            sorts = (ObjectNode) jsonNode.get("sort");
        } catch (ClassCastException cce) {
            throw new LeanException(LeanException.Error.QueryJSON, " Property 'sort' must be a JSON object.");
        }
        if (sorts != null) {
            Iterator<String> sortIterator = sorts.getFieldNames();
            while (sortIterator.hasNext()) {
                String sortProperty = sortIterator.next();
                query.addSort(sortProperty, QuerySort.SortDirection.create(sorts.get(sortProperty).getTextValue()));
            }
        }

        // get 'cursor'
        JsonNode cursorNode = jsonNode.get("cursor");
        if (cursorNode != null) {
            query.cursor = Cursor.fromWebSafeString(cursorNode.getTextValue());
        }

        // get 'cursor'
        JsonNode limitNode = jsonNode.get("limit");
        if (limitNode != null) {
            query.limit = limitNode.getIntValue();
        }

        // get 'cursor'
        JsonNode offsetNode = jsonNode.get("offset");
        if (offsetNode != null) {
            query.offset = offsetNode.getIntValue();
        }

        // get 'reference'
        ObjectNode reference;
        try {
            reference = (ObjectNode) jsonNode.get("reference");
        } catch (ClassCastException cce) {
            throw new LeanException(LeanException.Error.QueryJSON, " Property 'reference' must be a JSON object.");
        }
        if (reference != null)
        {
            JsonNode propertyNode = reference.get("property");
            if (propertyNode == null)
                throw new LeanException(LeanException.Error.QueryJSON, "'reference' object's 'property' property is not set");
            final String refProperty = propertyNode.getTextValue();
            JsonNode kindNode = reference.get("kind");
            if (kindNode == null)
                throw new LeanException(LeanException.Error.QueryJSON, "'reference' object's 'kind' property is not set");
            final String refKind = propertyNode.getTextValue();

            query.reference = new QueryReference(refProperty, refKind);
        }

        return query;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public QueryReference getReference() {
        return reference;
    }

    public void setReference(QueryReference reference) {
        this.reference = reference;
    }
}
