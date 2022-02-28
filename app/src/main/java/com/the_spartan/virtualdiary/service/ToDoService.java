package com.the_spartan.virtualdiary.service;

import static com.the_spartan.virtualdiary.model.Note.NOTE;
import static com.the_spartan.virtualdiary.model.ToDo.TODO;

import com.the_spartan.virtualdiary.adapter.ToDoAdapter;
import com.the_spartan.virtualdiary.data.FirebaseHelper;
import com.the_spartan.virtualdiary.model.ToDo;

import java.util.ArrayList;
import java.util.List;

public class ToDoService {

    public void findAll(ArrayList<ToDo> todos, ToDoAdapter todoAdapter) {
        FirebaseHelper.getQueryForTodos()
                .addChildEventListener(
                        new ToDoChildEventListener(todos, todoAdapter)
                );
    }

    public void saveOrUpdate(ToDo todo) {
        if (todo.isNew()) {
            FirebaseHelper.getReference().child(TODO).push().setValue(todo);

        } else {
            FirebaseHelper.getReference().child(TODO).child(todo.getKey()).setValue(todo);
        }
    }

    public void delete(ToDo todo) {
        FirebaseHelper.getReference().child(TODO).child(todo.getKey()).removeValue();
    }

    public void delete(List<ToDo> list) {
        for (ToDo todo : list) {
            delete(todo);
        }
    }
}
