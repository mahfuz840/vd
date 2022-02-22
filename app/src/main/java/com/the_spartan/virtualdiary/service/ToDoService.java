package com.the_spartan.virtualdiary.service;

import com.the_spartan.virtualdiary.model.ToDo;

import java.util.List;

public class ToDoService {

    public void saveOrUpdate(ToDo todo) {
        System.out.println("Saved updated todo!");
    }

    public void delete(List<ToDo> list) {
        System.out.println("Deleted completed todos!");
    }
}
