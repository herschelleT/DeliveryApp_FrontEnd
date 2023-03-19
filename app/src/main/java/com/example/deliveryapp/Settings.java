package com.example.deliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Settings extends AppCompatActivity {
    Button Delete,profile,logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        logout=findViewById(R.id.Logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance ();
                mAuth.signOut();
                finish();
                Intent intent=new Intent(Settings.this,Login_Page.class);
                startActivity(intent);
            }

        });//to logout
        Delete=findViewById(R.id.Delete_ACCOUNT);
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder Alert=new AlertDialog.Builder(Settings.this);
                Alert.setTitle("Are you Sure?");
                Alert.setMessage("Your account will be deleted permanently once You press Delete  ");
                Alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("Users").child("Delivery").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    FirebaseDatabase.getInstance().getReference().child("Users").child("Delivery").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(null);
                                }
                                else{
                                    FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(null);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(Settings.this,"Account Deleted",Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(Settings.this,SignUp.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(Settings.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                Alert.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog=Alert.create();
                alertDialog.show();
            }
        });//to delete the account
        profile=findViewById(R.id.Profile);//to view profile
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Settings.this,Profile2.class);
                startActivity(intent);
            }
        });
    }
}