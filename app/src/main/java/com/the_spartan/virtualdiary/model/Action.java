package com.the_spartan.virtualdiary.model;

public enum Action {

    SEARCH_BY_MONTH,
    SEARCH_BY_QUERY,
    SEARCH_BY_LAST_30_DAYS;

    public boolean is(Action action) {
        return this == action;
    }
}
