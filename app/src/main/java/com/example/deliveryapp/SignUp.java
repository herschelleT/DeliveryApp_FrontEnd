package com.example.deliveryapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deliveryapp.Models.Users;
import com.example.deliveryapp.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ActivitySignUpBinding binding;
    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    ProgressDialog progressdialog;
    private Spinner spinner;
    String selectedOption;
    String[] perms={"android.permission.ACCESS_FINE_LOCATION"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        spinner=findViewById(R.id.spinner3);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.Options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        mAuth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        progressdialog=new ProgressDialog(SignUp.this);
        progressdialog.setTitle("Creating Account");
        progressdialog.setMessage("please wait while we create your account");
        binding.SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressdialog.show();
                boolean name=binding.Username.getText().toString().equals("");
                boolean phone=binding.Call.getText().toString().equals("");
                boolean email=binding.Email.getText().toString().equals("");
                boolean password=binding.Password.getText().toString().equals("");
                String length= String.valueOf(binding.Call.getText().toString().length());
                if (!( name||phone||email||password )&&length.equals("8")){//checks if all criteria are satisfied to signup
                    mAuth.createUserWithEmailAndPassword(binding.Email.getText().toString()
                            , binding.Password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            progressdialog.dismiss();
                            if (task.isSuccessful() && !binding.Username.getText().toString().equals("")) {
                                Users user = new Users(binding.Username.getText().toString(), binding.Email.getText().toString(), binding.Call.getText().toString());
                                String id = task.getResult().getUser().getUid();
                                selectedOption = spinner.getSelectedItem().toString();
                                database.getReference().child("Users").child(selectedOption).child(id).setValue(user);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(perms, 80);
                                }
                            } else { //if there is error in signup
                                if (binding.Username.getText().toString().equals("")) {
                                    Toast.makeText(SignUp.this, "Please enter a username", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }


                    });//to check if value match
            }
                else {
                    progressdialog.dismiss();
                    Toast.makeText(SignUp.this,"Check if You have written all the info",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }





    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }//Ignore this

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }//Ignore this
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions, @NonNull  int[] grantResults) {//location permisson
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==80){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent intent;
                if (selectedOption.contains("Delivery")) {
                    intent = new Intent(SignUp.this, DriversMapsActivity.class);
                } else {
                    intent = new Intent(SignUp.this, Custhomepage.class);
                }
                startActivity(intent);
            }
            else{
                Toast.makeText(SignUp.this,"permission denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

}

