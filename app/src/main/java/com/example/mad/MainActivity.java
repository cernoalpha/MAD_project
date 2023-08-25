package com.example.mad;

import static com.google.android.material.color.utilities.MaterialDynamicColors.error;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mad.task.Task;
import com.example.mad.task.TaskManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;


import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView textViewGreeting, progrsspercent;
    private ProgressBar progressBarOverall;
    private LinearLayout linearCategoryProgress ,linearpiechart, linearlinechart;
    private Button lolButton, retbutton, taskbutton;
    private CardView overall_progress ,categorycardview;
    private ImageView profile_button;
    DatabaseReference userRef;
    PieChart pieChart;
    LineChart lineChart;
    FirebaseAuth firebaseAuth;
    ValueEventListener valueEventListener;
    TextView notask;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI
        textViewGreeting = findViewById(R.id.textViewGreeting);
        linearCategoryProgress = findViewById(R.id.linearCategoryProgress);
        progressBarOverall = findViewById(R.id.progressBarOverall);
        linearpiechart = findViewById(R.id.linearpiechart);
        linearlinechart = findViewById(R.id.linearlinechart);
        progrsspercent =findViewById(R.id.progrsspercent);
        overall_progress =findViewById(R.id.overall_progress);
        profile_button =findViewById(R.id.profile_button);
        categorycardview =findViewById(R.id.CategoryCardView);
        taskbutton =findViewById(R.id.button_task);
        pieChart = findViewById(R.id.pieChart);
        lineChart = findViewById(R.id.lineChart);
        notask =findViewById(R.id.notask);
        notask.setVisibility(View.GONE);


        lolButton = findViewById(R.id.lolButton);
        retbutton = findViewById(R.id.retbutton);

        overall_progress.setVisibility(View.VISIBLE);
        categorycardview.setVisibility(View.VISIBLE);
        linearCategoryProgress.setVisibility(View.VISIBLE);
        linearpiechart.setVisibility(View.INVISIBLE);
        linearlinechart.setVisibility(View.INVISIBLE);



        lolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearCategoryProgress.setVisibility(View.INVISIBLE);
                categorycardview.setVisibility(View.INVISIBLE);
                overall_progress.setVisibility(View.INVISIBLE);
                setanimate();
                linearpiechart.setVisibility(View.VISIBLE);
                linearlinechart.setVisibility(View.VISIBLE);
            }
        });
        retbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearCategoryProgress.setVisibility(View.VISIBLE);
                categorycardview.setVisibility(View.VISIBLE);
                overall_progress.setVisibility(View.VISIBLE);
                linearpiechart.setVisibility(View.INVISIBLE);
                linearlinechart.setVisibility(View.INVISIBLE);
            }
        });

        taskbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DailyProgress.class);
                startActivity(intent);
            }
        });

        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
            }
        });


        setname();
        updateOverallProgress();
        fetchTasksAndShowChart();
        fetchAndPopulateUserTasks();
    }

    private void setanimate() {

        lineChart.animateY(500);
        pieChart.animateY(1000);
    }


    private void setname() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userId = currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        valueEventListener =userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isDestroyed()) {
                    return;
                }
                String uname = dataSnapshot.child("fullname").getValue(String.class);

                String greeting = "Hello, " + uname + "!";
                textViewGreeting.setText(greeting);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            int errorCode = error.getCode();
            String errorMessage = error.getMessage();

            // Handle the error based on the error code or message
            switch (errorCode) {
                case DatabaseError.PERMISSION_DENIED:
                    // Handle permission denied error
                    Toast.makeText(MainActivity.this, "Permission denied. Please check your database rules.", Toast.LENGTH_SHORT).show();
                    break;
                case DatabaseError.NETWORK_ERROR:
                    // Handle network error
                    Toast.makeText(MainActivity.this, "Network error. Please check your internet connection.", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    // Handle other errors
                    Toast.makeText(MainActivity.this, "Database error occurred: " + errorMessage, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        });

    }

    //------------------------------------------------------------------------------------------------
    private void fetchTasksAndShowChart() {
        TaskManager taskManager = new TaskManager();
        taskManager.getAllTasks(new TaskManager.TaskListener() {
            @Override
            public void onTaskDataChange(List<Task> tasks) {
                setchart(tasks);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error if needed
            }
        });
    }

    //------------------------------------------------------------------------------------------------

    private void setchart(List<Task> tasks) {

        setPiechart(tasks);
        setLinechart(tasks);

    }

    //---------------------------------------------------------------------
    private void setLinechart(List<Task> tasks) {

        List<Entry> entriesProgress = new ArrayList<>();
        List<String> xAxisLabels = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());

        if (tasks.isEmpty()) {
            lineChart.clear(); // Clear any existing data
            lineChart.setNoDataText("You need to make some tasks first");
            lineChart.setDescription("No Data Available"); // Set a description for the empty chart
            lineChart.invalidate(); // Refresh the chart
        } else {

            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                Date date = parseDate(task.getStartDate()); // Implement your date parsing logic
                float xValue = i; // X-axis value based on position
                float progressValue = calculateAverageProgressOfTasksOnDate(date, tasks);

                entriesProgress.add(new Entry(xValue, progressValue));

                xAxisLabels.add(dateFormat.format(date));
            }

            LineDataSet dataSetProgress = new LineDataSet(entriesProgress, "Progress");
            dataSetProgress.setColor(Color.BLUE);
            dataSetProgress.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataSetProgress.setDrawFilled(true); // Enable filling the area under the line
            dataSetProgress.setFillColor(Color.GREEN); // Set the fill color
            dataSetProgress.setFillAlpha(100); // Set the fill alpha (transparency)

            LineData lineData = new LineData(dataSetProgress);

            lineChart.setData(lineData);
            lineChart.notifyDataSetChanged();


            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);

            xAxis.setValueFormatter(new AxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    int index = (int) value;
                    if (index >= 0 && index < xAxisLabels.size()) {
                        return xAxisLabels.get(index);
                    }
                    return ""; // Return an empty label for out-of-range values
                }

                @Override
                public int getDecimalDigits() {
                    return 0;
                }
            });
            xAxis.setGranularity(1f); // Ensure each label is shown

            YAxis yAxisLeft = lineChart.getAxisLeft();
            yAxisLeft.setDrawGridLines(false);
            yAxisLeft.setEnabled(true);

            YAxis yAxisRight = lineChart.getAxisRight();
            yAxisRight.setEnabled(false);
            yAxisLeft.setAxisMinValue(0f); // Set the minimum value
            yAxisLeft.setAxisMaxValue(100f); // Set the maximum value

            lineChart.setDescription("");
            lineChart.getLegend().setEnabled(false); // Disable legend for progress chart


            lineChart.invalidate();

        }
    }
    //---------------------------------------------------------------------
    private void setPiechart(List<Task> tasks) {

        float notStartedCount = 0;
        float inProgressCount = 0;
        float completedCount = 0;


        if (tasks.isEmpty()) {
            pieChart.setCenterText("No Data Available");
            pieChart.setNoDataText("You need to make some tasks first");
            pieChart.invalidate();
        } else {

            for (Task task : tasks) {
                Log.d("TaskProgress", "Task progress: " + task.getProgress());
                if (task.getProgress() == 0) {
                    notStartedCount++;
                } else if (task.getProgress() < 100) {
                    inProgressCount++;
                } else {
                    completedCount++;
                }
            }


            List<PieEntry> entries = new ArrayList<>();

            entries.add(new PieEntry(notStartedCount, "Not Started"));
            entries.add(new PieEntry(inProgressCount, "In Progress"));
            entries.add(new PieEntry(completedCount, "Completed"));

            PieDataSet dataSet = new PieDataSet(entries, "Task Progress");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSet.setValueTextSize(14f);

            PieData pieData = new PieData(dataSet);
            pieChart.setData(pieData);

            // Customize chart settings
            pieChart.setUsePercentValues(true);
            pieChart.setDrawEntryLabels(false);
            pieChart.setDescription("");
            pieChart.setEntryLabelColor(Color.BLACK);
            pieChart.setHoleColor(Color.TRANSPARENT);



            // Add legend configuration
            Legend legend = pieChart.getLegend();
            legend.setForm(Legend.LegendForm.CIRCLE);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            legend.setOrientation(Legend.LegendOrientation.VERTICAL);
            legend.setDrawInside(false);
            String[] categoryLabels = {"Not Started", "In Progress", "Completed"};
            legend.setComputedLabels(Arrays.asList(categoryLabels));

            legend.setXOffset(-175f);
            legend.setYOffset(110f);
            legend.setYEntrySpace(5f);


            pieChart.invalidate(); // Refresh the chart


        }


    }

    //---------------------------------------------------------------------

    private Date parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(); // Return a default date in case of parsing error
        }
    }

    //------------------------------------------------------------------------------------------------


    private float calculateAverageProgressOfTasksOnDate(Date date, List<Task> tasks) {
        float totalProgress = 0;
        int taskCount = 0;
        for (Task task : tasks) {
            Date taskDate = parseDate(task.getStartDate());
            if (isSameDay(taskDate, date)) {
                totalProgress += task.getProgress();
                taskCount++;
            }
        }
        if (taskCount > 0) {
            return totalProgress / taskCount;
        }
        return 0;
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }



    //------------------------------------------------------------------------------------------------

    private void updateOverallProgress() {
        TaskManager taskManager = new TaskManager();
        taskManager.getAllTasks(new TaskManager.TaskListener() {
            @Override
            public void onTaskDataChange(List<Task> tasks) {
                OverallProgressCalculator progressCalculator = new OverallProgressCalculator();
                float overallProgress = progressCalculator.calculateOverallProgress(tasks);

                progrsspercent.setText((int)overallProgress+"%");
                progressBarOverall.setProgress((int) overallProgress);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error if needed
            }
        });
    }
    private void fetchAndPopulateUserTasks() {
        TaskManager taskManager = new TaskManager();
        taskManager.getAllTasks(new TaskManager.TaskListener() {
            @Override
            public void onTaskDataChange(List<Task> tasks) {
                // Categorize and populate category cards with user tasks
                populateCategoryCards(tasks);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error if needed
            }
        });
    }

    private void populateCategoryCards(List<Task> tasks) {
        Map<String, List<Task>> categorizedTasks = new HashMap<>();

        if (tasks.isEmpty()) {

             notask.setVisibility(View.VISIBLE);


        } else {
            notask.setVisibility(View.GONE);

            // Categorize tasks based on their category
            for (Task task : tasks) {
                String category = task.getCategory();
                if (!categorizedTasks.containsKey(category)) {
                    categorizedTasks.put(category, new ArrayList<>());
                }
                categorizedTasks.get(category).add(task);
            }

            // Get a reference to the linear layout
            LinearLayout linearCategoryProgress = findViewById(R.id.linearCategoryProgress);

            // Iterate through categorized tasks and add them in pairs to rows
            List<String> categories = new ArrayList<>(categorizedTasks.keySet());
            for (int i = 0; i < categories.size(); i += 2) {
                String category1 = categories.get(i);
                String category2 = (i + 1 < categories.size()) ? categories.get(i + 1) : null;

                // Inflate the category_card.xml layout for each card
                View cardView1 = getLayoutInflater().inflate(R.layout.category_card, null);
                View cardView2 = (category2 != null) ? getLayoutInflater().inflate(R.layout.category_card, null) : null;

                // Configure cardView1 with data from category1
                configureCardView(cardView1, categorizedTasks.get(category1));

                // Configure cardView2 if category2 is available
                if (cardView2 != null) {
                    configureCardView(cardView2, categorizedTasks.get(category2));
                }

                // Create a LinearLayout to hold the card views
                LinearLayout rowLayout = new LinearLayout(this);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(0, 0, 0, 30); // Add spacing between rows
                rowLayout.setLayoutParams(layoutParams);

                // Set equal weights for the card views to ensure they fill the width equally
                LinearLayout.LayoutParams cardLayoutParams = new LinearLayout.LayoutParams(
                        0, // Width set to 0
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1  // Equal weight for both card views
                );

                cardLayoutParams.setMargins(0, 0, 20, 0);
                cardView1.setLayoutParams(cardLayoutParams);

                if (cardView2 != null) {
                    cardView2.setLayoutParams(cardLayoutParams);
                    rowLayout.addView(cardView1);
                    rowLayout.addView(cardView2);
                } else {
                    rowLayout.addView(cardView1);
                }

                // Add the row layout to the main layout
                linearCategoryProgress.addView(rowLayout);
            }
        }
    }

    private void configureCardView(View cardView, List<Task> tasks) {
        TextView categoryNameTextView = cardView.findViewById(R.id.textViewCategoryName);
        ProgressBar progressBarCategory = cardView.findViewById(R.id.progressBarCategory);
        TextView tasksCountTextView = cardView.findViewById(R.id.textViewTasksCount);

        // Set category name
        categoryNameTextView.setText(tasks.get(0).getCategory());

        // Calculate category progress
        int totalProgress = 0;
        for (Task task : tasks) {
            totalProgress += task.getProgress();
        }
        int averageProgress = tasks.isEmpty() ? 0 : totalProgress / tasks.size();
        progressBarCategory.setProgress(averageProgress);

        // Set tasks count text
        tasksCountTextView.setText(tasks.size() + " tasks");

        // Add click listener to the card view
        cardView.setOnClickListener(v -> {
            // Handle click on card view
            Toast.makeText(this, "Clicked on category: " + tasks.get(0).getCategory(), Toast.LENGTH_SHORT).show();
        });
    }

}

