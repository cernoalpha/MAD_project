package com.example.mad.task;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad.R;
import com.example.mad.TaskDetailsActivity;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks;
    private List<Task> filteredTasks;

    public TaskAdapter(List<Task> tasks) {
        this.tasks = tasks;
        this.filteredTasks = new ArrayList<>(tasks);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = filteredTasks.get(position);
        holder.bind(task);

        holder.itemView.setOnClickListener(v -> {
            // Launch TaskDetailsActivity and pass the selected task
            Intent intent = new Intent(v.getContext(), TaskDetailsActivity.class);
            intent.putExtra("task", task);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredTasks.size();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        applyFilter("");
    }

    public void filterTasks(String searchText) {
        applyFilter(searchText);
    }

    private void applyFilter(String searchText) {
        filteredTasks.clear();
        if (searchText.isEmpty()) {
            filteredTasks.addAll(tasks); // If search text is empty, show all tasks
        } else {
            for (Task task : tasks) {
                if (task.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                    filteredTasks.add(task); // Add filtered tasks
                }
            }
        }
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
        }

        public void bind(Task task) {
            titleTextView.setText(task.getTitle());
        }
    }
}

