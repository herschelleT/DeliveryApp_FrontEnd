package com.example.deliveryapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
import android.view.View;

public class Shop extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
      Button b1=findViewById(R.id.button1);
      Button b2=findViewById(R.id.button2);
      Button b3=findViewById(R.id.button3);
      Button b4=findViewById(R.id.button4);
      Button b5=findViewById(R.id.Done);

      b1.setOnClickListener(new View.OnClickListener() {

        public void onClick(View v) {
            Intent myintent1= new Intent(Shop.this,Grocery.class);//opens different pages
            startActivity(myintent1);
            }
        });

      b2.setOnClickListener(new View.OnClickListener() {

        public void onClick(View v) {
            Intent myintent1= new Intent(Shop.this, FreshFood.class);
            startActivity(myintent1);
            }
        });

      b3.setOnClickListener(new View.OnClickListener() {

        public void onClick(View v) {
            Intent myintent1= new Intent(Shop.this,Electronics.class);
            startActivity(myintent1);
            }
        });

      b4.setOnClickListener(new View.OnClickListener() {

        public void onClick(View v) {
            Intent myintent1= new Intent(Shop.this,Lifestyle.class);
            startActivity(myintent1);
            }
        });
      b5.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent=new Intent(Shop.this,Custhomepage.class);
              startActivity(intent);
          }
      });
    }
}