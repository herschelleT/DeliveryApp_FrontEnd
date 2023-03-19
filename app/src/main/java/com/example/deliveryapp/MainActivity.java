package com.example.deliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.deliveryapp.DriversMapsActivity.MY_PERMISSIONS_REQUEST_LOCATION;
import static com.example.deliveryapp.R.string.material_clock_display_divider;
import static com.example.deliveryapp.R.string.title_location_permission;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread thread = new Thread(){
            public void run(){
                try{
                    sleep(4000);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                finally{
                    Intent intent = new Intent(MainActivity.this, Login_Page.class);
                    startActivity(intent);
                }
            }
        };thread.start();

    }

}