package com.the_spartan.virtualdiary.interfacing;

import com.the_spartan.virtualdiary.model.ToDoItem;

import java.util.ArrayList;

public interface DeleteListCollector {

    void updateDeleteList(ArrayList<ToDoItem> deleteList);
}
