package com.example.mad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mad.task.Task;
import com.example.mad.task.TaskManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TaskDetailsActivity extends AppCompatActivity {
    TextView titleTextView;
    TextView descriptionTextView;
    TextView progressTextView;
    SimpleDateFormat dateFormat;
    TextView startDateTextView;
    TextView endDateTextView, target, daysleft;
    SeekBar seekbar;
    private boolean isEditing = false;

    private EditText editTextTitle;
    private EditText editTextDescription;
    private TextView editTextProgress;
    private SeekBar seekBaredit;
    Button editButton,saveButton,deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        Task task = (Task) getIntent().getSerializableExtra("task");

        // Initialize TextViews
        titleTextView = findViewById(R.id.textViewTitle);
        descriptionTextView = findViewById(R.id.textViewDescription);
        progressTextView = findViewById(R.id.textViewProgress);
        startDateTextView = findViewById(R.id.textViewStartDate);
        endDateTextView = findViewById(R.id.textViewEndDate);
        seekbar =findViewById(R.id.seekBar);
        target =findViewById(R.id.target);
        daysleft = findViewById(R.id.daysleft);
        seekbar.setEnabled(false);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextProgress = findViewById(R.id.editTextProgress);
        seekBaredit = findViewById(R.id.seekBaredit);


        seekBaredit.setProgress(task.getProgress());
        seekBaredit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the TextView with the progress percentage
                editTextProgress.setText(progress + "%");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        initializeDateFormat();
        displayTaskDetails(task);

         editButton = findViewById(R.id.buttonEdit);
        editButton.setOnClickListener(v -> toggleEditMode(task));

         saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(v -> saveChanges(task));

        deleteButton = findViewById(R.id.buttonDelete);
        deleteButton.setOnClickListener(v -> deleteTask(task));


    }

    private void saveChanges(Task task) {

        String newTitle,newDesc;
        newTitle=editTextTitle.getText().toString();
        newDesc =editTextDescription.getText().toString();
        if(newTitle.isEmpty() || newDesc.isEmpty()){
            Toast.makeText(TaskDetailsActivity.this,"No empty fields",Toast.LENGTH_SHORT).show();
        }
        else {

            task.setTitle(newTitle);
            task.setDescription(newDesc);
            String progressText = editTextProgress.getText().toString();
            progressText = progressText.replace("%", "");
            int progress = Integer.parseInt(progressText);
            task.setProgress(progress);

            // TODO: Update task details in Firebase using TaskManager.updateTask()
            TaskManager taskManager = new TaskManager();
            taskManager.updateTask(task);

            displayTaskDetails(task); // Update displayed details

            toggleEditMode(task);
        }
    }

    private void toggleEditMode(Task task) {
        isEditing = !isEditing;
        if (isEditing) {
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);

            findViewById(R.id.card).setVisibility(View.INVISIBLE);
            findViewById(R.id.cardedit).setVisibility(View.VISIBLE);

            editTextTitle.setText(task.getTitle());
            editTextDescription.setText(task.getDescription());
            editTextProgress.setText(String.valueOf(task.getProgress()));
            seekBaredit.setProgress(task.getProgress());
        } else {
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.GONE);

            findViewById(R.id.card).setVisibility(View.VISIBLE);
            findViewById(R.id.cardedit).setVisibility(View.INVISIBLE);
        }
    }

    private void displayTaskDetails(Task task) {
        titleTextView.setText(task.getTitle());
        descriptionTextView.setText(task.getDescription());
        progressTextView.setText(task.getProgress() + "%");
        seekbar.setProgress(task.getProgress());
        startDateTextView.setText(task.getStartDate());
        endDateTextView.setText(task.getEndDate());


        long daysLeft = calculateDaysLeft();
        daysleft.setText("Days left: " + daysLeft);
        double remainingPercentagePerDay = (100 - task.getProgress()) / (double) daysLeft;
        target.setText(String.format("Target: %.2f%% per day", remainingPercentagePerDay));

    }

    private void initializeDateFormat() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    long calculateDaysLeft() {
        String startDateStr = startDateTextView.getText().toString();
        String endDateStr = endDateTextView.getText().toString();

        try {
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);

            long difference = endDate.getTime() - startDate.getTime();
            return TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    private void deleteTask(Task taskToDelete) {
        if (taskToDelete != null) {
            TaskManager taskManager = new TaskManager();
            taskManager.deleteTask(taskToDelete);
            finish();
        }
        }


}
