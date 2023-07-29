package com.example.mad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mad.task.Task;
import com.example.mad.task.TaskAdapter;
import com.example.mad.task.TaskManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DailyProgress extends AppCompatActivity {

    EditText searchbar;
    ImageButton back, profile, add;
    RecyclerView recyclerView;

    TaskManager taskManager;
    TaskAdapter taskAdapter;
    List<Task> allTasks = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_progress);

        searchbar = findViewById(R.id.searchbar);
        back = findViewById(R.id.btn_back);
        profile = findViewById(R.id.btn_pp);
        add = findViewById(R.id.add_task);



        taskManager = new TaskManager();
        taskAdapter = new TaskAdapter(allTasks);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);
        retrieveTasksFromFirebase();


        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                taskAdapter.filterTasks(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                startActivity(intent);
            }
        });


    }

    private void retrieveTasksFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Handle the case when the current user is not logged in
            return;
        }

        String currentUserId = currentUser.getUid();
        DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId).child("tasks");
        tasksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allTasks.clear(); // Clear the existing list before adding new tasks
                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        allTasks.add(task);
                    }
                }
                taskAdapter.setTasks(allTasks); // Update the adapter with the new list of tasks
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error if needed
            }
        });
    }


    private void addTask() {
        Intent intent = new Intent(getApplicationContext(), Taskinput.class);
        startActivity(intent);
        }
}
