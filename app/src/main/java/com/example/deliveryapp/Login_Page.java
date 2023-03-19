package com.example.deliveryapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deliveryapp.databinding.ActivityLoginPageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login_Page extends AppCompatActivity {

    Button next;

    //ActivityLoginPageBinding binding;
    //ProgressDialog progressdialog,progressDialog1;
    //FirebaseAuth auth;
    Button button;
    Button forgot;
    //String[] perms={"android.permission.ACCESS_FINE_LOCATION"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding = ActivityLoginPageBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());
        setContentView(R.layout.activity_login_page);

        forgot = findViewById(R.id.ForgotPassword);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//when forgot password is pressed
                Intent intent = new Intent(Login_Page.this, ForgotPassword.class);
                startActivity(intent);
            }
        });//if u forgot the password

        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_Page.this, Custhomepage.class);
                startActivity(intent);
            }
        });

        button = findViewById(R.id.SignUpnButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_Page.this, SignUp.class);
                startActivity(intent);
            } //taking the user to signup page
        });//to go to signup page

    }

}


