package com.example.mad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mad.task.Task;
import com.example.mad.task.TaskManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    Button btnlogout;
    TextView txtname, txtemail;
    CircleImageView imgpp;
    DatabaseReference userRef;
    FirebaseAuth firebaseAuth;
    String name;
    ValueEventListener valueEventListener;
    private Switch switchNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnlogout = findViewById(R.id.btn_logout);

        txtname = findViewById(R.id.display_name);
        txtemail = findViewById(R.id.display_email);
        switchNotify = findViewById(R.id.button_notify);

        imgpp = findViewById(R.id.display_pp);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userId = currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        valueEventListener = userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isDestroyed()) {
                    return;
                }
                name = dataSnapshot.child("fullname").getValue(String.class);
                String image = dataSnapshot.child("pimage").getValue(String.class);


                txtname.setText(name);
                txtemail.setText(currentUser.getEmail());

                RequestOptions requestOptions = new RequestOptions()
                        .placeholder(R.drawable.defaultpp)
                        .error(R.drawable.defaultpp);

                Glide.with(Profile.this)
                        .setDefaultRequestOptions(requestOptions)
                        .load(image)
                        .into(imgpp);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Get the error details
                int errorCode = error.getCode();
                String errorMessage = error.getMessage();

                // Handle the error based on the error code or message
                switch (errorCode) {
                    case DatabaseError.PERMISSION_DENIED:
                        // Handle permission denied error
                        Toast.makeText(Profile.this, "Permission denied. Please check your database rules.", Toast.LENGTH_SHORT).show();
                        break;
                    case DatabaseError.NETWORK_ERROR:
                        // Handle network error
                        Toast.makeText(Profile.this, "Network error. Please check your internet connection.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        // Handle other errors
                        Toast.makeText(Profile.this, "Database error occurred: " + errorMessage, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });


        switchNotify.setChecked(true);

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                userRef.removeEventListener(valueEventListener);
                startActivity(new Intent(Profile.this, Login.class));
                finish();
            }
        });


        switchNotify.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                TaskManager taskManager = new TaskManager();
                taskManager.getAllTasks(new TaskManager.TaskListener() {
                    @Override
                    public void onTaskDataChange(List<Task> tasks) {
                        scheduleNotifications(tasks);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error if needed
                    }
                });

            } else {
                cancelNotifications();
            }
        });

    }

    private void cancelNotifications() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancelAll();
    }

    private void scheduleNotifications(List<Task> taskList) {
        Date twelveHoursLater = getTwelveHoursLater();

        for (Task task : taskList) {
            if (isTaskEndingWithinOneDay(task)) {
                showNotification(task);
            }
        }
    }

    private boolean isTaskEndingWithinOneDay(Task task) {
        Date currentDate = new Date(); // Current time
        Date taskEndDate = parseStringToDate(task.getEndDate()); // Implement this
        if (taskEndDate != null) {
            long timeDifferenceInMillis = taskEndDate.getTime() - currentDate.getTime();
            long oneDayInMillis = 24 * 60 * 60 * 1000; // 24 hours in milliseconds
            return timeDifferenceInMillis <= oneDayInMillis;
        }
        return false;
    }

    private Date getTwelveHoursLater() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 24);
        return calendar.getTime();
    }


    Date parseStringToDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showNotification(Task task) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Notification.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.notify)
                .setContentTitle("Task Reminder")
                .setContentText("Task \"" + task.getTitle() + "\" is ending soon.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        int notificationId = generateNotificationId();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(notificationId, builder.build());
    }

    private int generateNotificationId() {
        SharedPreferences preferences = getSharedPreferences("notification_prefs", MODE_PRIVATE);
        int currentId = preferences.getInt("notification_id", 0);
        int newId = currentId + 1;
        preferences.edit().putInt("notification_id", newId).apply();
        return newId;
    }
}
