package com.the_spartan.virtualdiary.util;

import com.the_spartan.virtualdiary.model.ToDoItem;

import java.util.List;

public interface DeleteListCollector {

    void updateDeleteList(List<ToDoItem> deleteList);
}
