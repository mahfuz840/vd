package com.the_spartan.virtualdiary.model;

public enum Action {

    SEARCH_BY_MONTH,
    SEARCH_BY_QUERY;

    public boolean is(Action action) {
        return this == action;
    }
}
