package com.the_spartan.virtualdiary.interfacing;

import com.the_spartan.virtualdiary.model.ToDoItem;

import java.util.ArrayList;
import java.util.List;

public interface DeleteListCollector {

    void updateActiveItemDeleteList(ArrayList<ToDoItem> deleteList);

    void updateOldItemDeleteList(ArrayList<ToDoItem> deleteList);
}
