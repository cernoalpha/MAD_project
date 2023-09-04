package com.example.mad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    Button btnlogout;
    TextView txtname, txtemail;
    CircleImageView imgpp;
    DatabaseReference userRef;
    FirebaseAuth firebaseAuth;
    String name;
    ValueEventListener valueEventListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnlogout = findViewById(R.id.btn_logout);

        txtname = findViewById(R.id.display_name);
        txtemail = findViewById(R.id.display_email);

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


        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                userRef.removeEventListener(valueEventListener);
                startActivity(new Intent(Profile.this, Login.class));
                finish();
            }
        });

    }
}
