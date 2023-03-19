package com.example.deliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.deliveryapp.Models.Users;
import com.example.deliveryapp.databinding.ActivityForgotPasswordBinding;
import com.example.deliveryapp.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPassword extends AppCompatActivity {
    ActivityForgotPasswordBinding binding;
    EditText useremail;
    Button password;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        useremail=findViewById(R.id.enterEmailAddress);
        password=findViewById(R.id.sendpassword);
        auth=FirebaseAuth.getInstance();
        binding.sendpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!useremail.getText().toString().equals("")) {
                    auth.sendPasswordResetEmail(useremail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPassword.this, "A verification mail has been sent to your mail", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ForgotPassword.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(ForgotPassword.this,"Please enter the email",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}