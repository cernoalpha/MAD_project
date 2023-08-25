package com.example.mad;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Random;

public class Register extends AppCompatActivity {
    TextInputEditText etEmail, etPassword, etFullName;
    ImageView IVprofile;
    Button btnReg;
    Bitmap bitmap;
    Uri filepath;
    FirebaseAuth mAuth;
    TextView browse;
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        IVprofile = findViewById(R.id.profilepic);
        browse = (TextView) findViewById(R.id.selectpp);


        etEmail = (TextInputEditText) findViewById(R.id.email);
        etPassword = (TextInputEditText) findViewById(R.id.password);
        etFullName = findViewById(R.id.fullname);
        btnReg = (Button) findViewById(R.id.btn_register);

        mAuth = FirebaseAuth.getInstance();


        browse.setOnClickListener(v -> {
            openImagePicker();
        });

        btnReg.setOnClickListener(v -> {
            String name = String.valueOf(etFullName.getText());
            String email = String.valueOf(etEmail.getText());
            String password = String.valueOf(etPassword.getText());

            signUp(name, email, password);
        });
    }




    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 123);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK && data != null) {
            filepath = data.getData();
            IVprofile.setImageURI(filepath);
        }
    }

    private void signUp(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        saveUserData(userId, name, filepath.toString());
                    } else {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(Register.this, "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(String userId, String name, String profileurl) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference currentUserRef = usersRef.child(userId);
        currentUserRef.child("fullname").setValue(name);

        if(profileurl!= null) {
            uploadImageToFirebaseStorage(userId,filepath);
        }else{
            currentUserRef.child("pimage").setValue(null)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                            // Intent to profile page
                            Intent intent = new Intent(getApplicationContext(), Profile.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(Register.this, "Failed to save user data: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        }


    private void uploadImageToFirebaseStorage(String userId, Uri filepath) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_images");
        StorageReference imageRef = storageRef.child(userId + "_" + new Random().nextInt(1000));

        imageRef.putFile(filepath)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                        DatabaseReference currentUserRef = usersRef.child(userId);
                        currentUserRef.child("pimage").setValue(uri.toString())
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Register.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                                        // Intent to profile page
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        String errorMessage = task.getException().getMessage();
                                        Toast.makeText(Register.this, "Failed to save user data: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Register.this, "Failed to upload profile image.", Toast.LENGTH_SHORT).show();
                });
     }
    }

