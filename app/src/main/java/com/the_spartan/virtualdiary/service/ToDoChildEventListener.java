package com.the_spartan.virtualdiary.service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.the_spartan.virtualdiary.adapter.ToDoAdapter;
import com.the_spartan.virtualdiary.model.ToDo;

import java.util.ArrayList;

public class ToDoChildEventListener implements ChildEventListener {

    private ArrayList<ToDo> todos;
    private ToDoAdapter todoAdapter;

    public ToDoChildEventListener(ArrayList<ToDo> todos, ToDoAdapter todoAdapter) {
        this.todos = todos;
        this.todoAdapter = todoAdapter;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        ToDo todo = dataSnapshot.getValue(ToDo.class);
        todo.setKey(dataSnapshot.getKey());
        todos.add(todo);

        todoAdapter.notifyDataSetChanged();
        int count = todoAdapter.getCount();
        System.out.println("COUNT " + count);
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        ToDo changedTodo = dataSnapshot.getValue(ToDo.class);
        for (ToDo todo : todos) {
            if (todo.getKey().equals(dataSnapshot.getKey())) {
                todo.setSubject(changedTodo.getSubject());
                todo.setDueDate(changedTodo.getDueDate());
                todo.setTime(changedTodo.getTime());
                todo.setPriority(changedTodo.getPriority());
                todo.setDone(changedTodo.isDone());
            }
        }

        todoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        for (ToDo todo : todos) {
            if (todo.getKey().equals(dataSnapshot.getKey())) {
                todos.remove(todo);
            }
        }

        todoAdapter.notifyDataSetChanged();
//        setEmptyViewVisibility();
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
