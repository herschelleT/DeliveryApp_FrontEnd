package com.example.deliveryapp;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Grocery extends AppCompatActivity {
    Button mShowDialog,mShowDialog1,mShowDialog2,mShowDialog3,mShowDialog4,mShowDialog5;
    String uid,mytext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery);
        /*uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mShowDialog = (Button) findViewById(R.id.number);
        function(mShowDialog,"Tomatoes");
        mShowDialog1 = (Button) findViewById(R.id.number1);
        function(mShowDialog1,"Potatoes");
        mShowDialog2 = (Button) findViewById(R.id.number2);
        function(mShowDialog2,"Carrots");
        mShowDialog3 = (Button) findViewById(R.id.number3);
        function(mShowDialog3,"onion");
        mShowDialog4 = (Button) findViewById(R.id.number4);
        function(mShowDialog4,"Beans");
        mShowDialog5 = (Button) findViewById(R.id.number5);
        function(mShowDialog5,"Lettuce");Button mShowdialog6 = (Button) findViewById(R.id.Other);
        mShowdialog6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myDialog=new AlertDialog.Builder(Grocery.this);
                myDialog.setTitle("Other Items");
                myDialog.setMessage("Please write the name of the item you want");

                View mView = getLayoutInflater().inflate(R.layout.buttonlayoutorder, null);
                EditText otherorder=(EditText) mView.findViewById(R.id.Otherorder);
                Spinner mSpinner = (Spinner) mView.findViewById(R.id.spinner1);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Grocery.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.SelectNumber));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(adapter);
                myDialog.setView(mView);
                myDialog.setPositiveButton("done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mytext=otherorder.getText().toString();
                        if(!mSpinner.getSelectedItem().toString().equalsIgnoreCase("Choose number of items")) {
                            if (!mSpinner.getSelectedItem().toString().equalsIgnoreCase("0")) {
                                String selecteditem = mSpinner.getSelectedItem().toString();
                                FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(uid).child("Order").child(mytext).setValue(selecteditem);
                            }
                            else{
                                FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(uid).child("Order").child(mytext).setValue(null);
                            }
                            dialog.dismiss();
                        }
                    }
                });
                myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                myDialog.show();
            }
        });*/
    }
    public void function(Button button,String string){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Grocery.this);
                View mView = getLayoutInflater().inflate(R.layout.activity_buttonlayout, null);
                mBuilder.setTitle("Choose Number of items");
                Spinner mSpinner = (Spinner) mView.findViewById(R.id.spinner1);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Grocery.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.SelectNumber));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(adapter);
                mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!mSpinner.getSelectedItem().toString().equalsIgnoreCase("Choose number of items")) {
                            if (!mSpinner.getSelectedItem().toString().equalsIgnoreCase("0")) {
                                String selecteditem = mSpinner.getSelectedItem().toString();
                                FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(uid).child("Order").child(string).setValue(selecteditem);
                            }
                            else{
                                FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(uid).child("Order").child(string).setValue(null);
                            }
                            dialog.dismiss();
                        }
                    }
                });
                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });
    }



















}