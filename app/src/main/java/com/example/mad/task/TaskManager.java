package com.example.mad.task;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private DatabaseReference usersRef;
    private DatabaseReference tasksRef;
    private String currentUserId;

    public TaskManager() {
        // Get the current user ID from Firebase Authentication (replace this with your method)
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }

        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        tasksRef = usersRef.child(currentUserId).child("tasks");
    }

    public void saveTask(Task task, OnCompleteListener<Void> onCompleteListener) {
        String taskId = tasksRef.push().getKey();
        task.setTaskId(taskId);

        tasksRef.child(taskId).setValue(task)
                .addOnCompleteListener(onCompleteListener);
    }

    public void updateTask(Task task) {
        String taskId = task.getTaskId();
        tasksRef.child(taskId).setValue(task);
    }

    public void deleteTask(Task task) {
        String taskId = task.getTaskId();
        tasksRef.child(taskId).removeValue();
    }

    public interface TaskListener {
        void onTaskDataChange(List<Task> tasks);
        void onCancelled(DatabaseError databaseError);
    }

    public void getAllTasks(TaskListener listener) {
        tasksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Task> tasks = new ArrayList<>();
                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        tasks.add(task);
                    }
                }
                listener.onTaskDataChange(tasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onCancelled(databaseError);
            }
        });
    }
}
