package com.example.deliveryapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deliveryapp.databinding.ActivityCusthomepageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Custhomepage extends AppCompatActivity {

    Button BtnCircle,View,Setting,locationbtn;
    //FirebaseAuth auth;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custhomepage);
        //binding = ActivityCusthomepageBinding.inflate(getLayoutInflater());

        //
        Setting = findViewById(R.id.Setting);
        Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent = new Intent(Custhomepage.this, Settings.class);
                startActivity(intent);
            }
        });
        View = findViewById(R.id.Viewod);
        View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent = new Intent(Custhomepage.this, ViewOrder.class);
                startActivity(intent);
            }
        });
        BtnCircle = findViewById(R.id.PlaceOd);
        BtnCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent intent = new Intent(Custhomepage.this, Shop.class);
                startActivity(intent);
            }
        });
        //

/*        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userid= FirebaseAuth.getInstance().getCurrentUser().getUid();

            auth = FirebaseAuth.getInstance();
            imageView = findViewById(R.id.imageview);
            *//*Setting=findViewById(R.id.Setting);
            Setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    Intent intent=new Intent(Custhomepage.this,Settings.class);
                    startActivity(intent);
                }
            });
            View = findViewById(R.id.Viewod);
            View.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    Intent intent = new Intent(Custhomepage.this, ViewOrder.class);
                    startActivity(intent);
                }
            });
            BtnCircle = findViewById(R.id.PlaceOd);
            BtnCircle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    Intent intent=new Intent(Custhomepage.this,Shop.class);
                    startActivity(intent);
                }
            });*//*

            FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(userid).child("DriverID").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull  DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        driverID = snapshot.getValue().toString();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            FirebaseDatabase.getInstance().getReference().child("Pending Verification").child(userid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull  DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        String Str = snapshot.child(driverID).getValue().toString();
                        Boolean check=(Str.equals("Waiting"));
                        if (check) {
                            androidx.appcompat.app.AlertDialog.Builder Alert = new androidx.appcompat.app.AlertDialog.Builder(Custhomepage.this);
                            Alert.setTitle("ORDER COMPLETED");
                            Alert.setMessage("YOUR ORDER HAS ARRIVED");
                            Alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(userid).child("DriverID").setValue(null);
                                    FirebaseDatabase.getInstance().getReference().child("Users").child("Delivery").child(driverID).child("CustomerID").setValue(null);
                                    FirebaseDatabase.getInstance().getReference().child("Pending Verification").child(userid).setValue(null);
                                    FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(userid).child("Order").setValue(null);
                                    driverID=null;
                                }
                            });
                            Alert.show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull  DatabaseError error) {

                }
            });



        }
        else{
            Intent intent=new Intent(Custhomepage.this,Login_Page.class);
            startActivity(intent);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode== Activity.RESULT_OK){
            final Uri imguri=data.getData();
            resultUri=imguri;
            imageView.setImageURI(resultUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==80){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent intent=new Intent(Custhomepage.this,CustomerMapsActivity.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(Custhomepage.this,"permission denied",Toast.LENGTH_SHORT).show();
            }
        }
    }*/
    }
}