package com.example.mad;

import com.example.mad.task.Task;

import java.util.List;

public class OverallProgressCalculator {

    public float calculateOverallProgress(List<Task> tasks) {
        int totalProgress = 0;
        int totalTasks = tasks.size();

        for (Task task : tasks) {
            totalProgress += task.getProgress();
        }

        if (totalTasks == 0) {
            return 0; // To avoid division by zero
        }

        float overallProgress = (float) totalProgress / totalTasks;
        return overallProgress;
    }
}
