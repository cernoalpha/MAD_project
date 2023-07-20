package com.example.mad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    Button btnlogout;
    ImageButton btnback;
    TextView txtname, txtemail;
    CircleImageView imgpp;
    DatabaseReference userRef;
    FirebaseAuth firebaseAuth;
    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnback = (ImageButton) findViewById(R.id.btnback);
        btnlogout = findViewById(R.id.btn_logout);

        txtname = findViewById(R.id.display_name);
        txtemail = findViewById(R.id.display_email);

        imgpp = findViewById(R.id.display_pp);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userId = currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        valueEventListener =userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("fullname").getValue(String.class);
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

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}