package com.example.mad;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;
import java.util.List;

public class DailyProgress extends AppCompatActivity {

    EditText searchbar;
    ListView listView;
    ImageButton back, profile, add;

    List<String> tasks;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_progress);

        searchbar = findViewById(R.id.searchbar);
        listView = findViewById(R.id.listview);
        back = findViewById(R.id.btn_back);
        profile = findViewById(R.id.btn_pp);
        add = findViewById(R.id.add_task);

        tasks = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasks);
        listView.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String task = tasks.get(position);
                Toast.makeText(DailyProgress.this, "Clicked: " + task, Toast.LENGTH_SHORT).show();
            }
        });

        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used in this example
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTasks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used in this example
            }
        });
    }

    private void filterTasks(String query) {
        List<String> filteredTasks = new ArrayList<>();
        for (String task : tasks) {
            if (task.toLowerCase().contains(query.toLowerCase())) {
                filteredTasks.add(task);
            }
        }
        adapter.clear();
        adapter.addAll(filteredTasks);
        adapter.notifyDataSetChanged();
    }


    private void addTask() {
        String newTask = searchbar.getText().toString().trim();
        if (!newTask.isEmpty()) {
            tasks.add(newTask);
            adapter.notifyDataSetChanged();
            searchbar.setText("");
        }
    }
}