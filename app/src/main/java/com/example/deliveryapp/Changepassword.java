package com.example.deliveryapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deliveryapp.Models.Users;
import com.example.deliveryapp.databinding.ActivityChangepasswordBinding;
import com.example.deliveryapp.databinding.ActivityLoginPageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.icu.text.DisplayContext.LENGTH_SHORT;
import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class Changepassword extends AppCompatActivity {
    ActivityChangepasswordBinding binding;
    FirebaseAuth auth;
    private DatabaseReference reference;
    EditText Fixmail,password;
    String uid;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangepasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth=FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();
        button=findViewById(R.id.button2);
        Fixmail = findViewById(R.id.fixedemail);
        password=findViewById(R.id.editTextTextPassword);
        reference = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference uidRef = reference.child("Delivery").child(uid);
        DatabaseReference finalUidRef = uidRef;
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {//TODO find how to change uid
                if (!snapshot.exists()) {
                    uidRef.removeValue();
                    reference.child("Customer").child(uid).addListenerForSingleValueEvent(this);

                }
                Users usersnapshot = snapshot.getValue(Users.class);
                if (usersnapshot != null) {
                    String Email = usersnapshot.getMail();
                    Fixmail.setText(Email);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.editTextTextPassword.getText().toString().equals("")){
                    auth.signInWithEmailAndPassword(binding.fixedemail.getText().toString(), binding.editTextTextPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        auth.sendPasswordResetEmail(Fixmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(Changepassword.this, "A verification mail has been sent to your mail", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(Changepassword.this, "Check if the password is wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
            }
                else {
                    Toast.makeText(Changepassword.this,"Please write your password",Toast.LENGTH_LONG).show();
                }

        }
        });

    }



}