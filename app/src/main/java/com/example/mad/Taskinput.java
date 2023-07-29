package com.example.mad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mad.task.Task;
import com.example.mad.task.TaskManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Taskinput extends AppCompatActivity {

    SeekBar seekBar;
    TextView txtpnt;
    TextView  StartDate,EndDate,dLeft,target;
    SimpleDateFormat dateFormat;
    TaskManager taskManager;
    EditText Title,desc;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskinput);

        taskManager = new TaskManager();

        Title = findViewById(R.id.title_task);
        desc= findViewById(R.id.description);

        save = findViewById(R.id.btn_save);


        StartDate = findViewById(R.id.startdate);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        String currentDate = dateFormat.format(calendar.getTime());
        StartDate.setText(currentDate);



        seekBar = findViewById(R.id.seekBar);
        txtpnt = findViewById(R.id.txtpnt);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the TextView with the progress percentage
                txtpnt.setText(progress + "%");

                // Recalculate days left and target percentage
                long daysLeft = calculateDaysLeft();
                //double targetPercentagePerDay = (double) progress / daysLeft;

                // Update the target TextView
                double remainingPercentagePerDay = (100 - progress) / (double) daysLeft;
                target.setText(String.format("Target: %.2f%% per day", remainingPercentagePerDay));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        EndDate = findViewById(R.id.enddate);
        dLeft =findViewById(R.id.daysleft);
        target = findViewById(R.id.target);

        EndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Taskinput.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                        EndDate.setText(selectedDate);

                        // Recalculate days left and target percentage
                        long daysLeft = calculateDaysLeft();
                        int progress = seekBar.getProgress();
                        double remainingPercentagePerDay = (100 - progress) / (double) daysLeft;

                        // Update the TextViews
                        txtpnt.setText(progress + "%");
                        dLeft.setText("Days left: " + daysLeft);
                        target.setText(String.format("Target: %.2f%% per day", remainingPercentagePerDay));
                    }
                }, year, month, day);

                datePickerDialog.show();
            }
        });



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTaskToFirebase();
            }
        });


    }


    private void saveTaskToFirebase() {
        String title = Title.getText().toString();
        String description = desc.getText().toString();
        int progress = seekBar.getProgress();
        String startDate = StartDate.getText().toString();
        String endDate = EndDate.getText().toString();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Handle the case when the current user is not logged in
            return;
        }

        String currentUserId = currentUser.getUid();
        Task task = new Task(currentUserId, title, description, progress, startDate, endDate);

        taskManager.saveTask(task, taskSaveTask -> {
            if (taskSaveTask.isSuccessful()) {
                Toast.makeText(Taskinput.this, "Task saved successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), DailyProgress.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(Taskinput.this, "Failed to save task. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }




    long calculateDaysLeft() {
        String startDateStr = StartDate.getText().toString();
        String endDateStr = EndDate.getText().toString();

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

}