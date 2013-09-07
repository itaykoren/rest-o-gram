package com.leanengine.server.entity;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Entity;

import java.util.List;

public class QueryResult {
    private List<Entity> result;
    private Cursor cursor;

    public QueryResult(final List<Entity> result, final Cursor cursor) {
        this.result = result;
        this.cursor = cursor;
    }

    public List<Entity> getResult() {
        return result;
    }

    public void setResult(List<Entity> result) {
        this.result = result;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }
}
