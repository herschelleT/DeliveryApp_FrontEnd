package com.example.deliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DriverViewOrder extends AppCompatActivity {
    String orderlist="",CustomerID;
    private LinearLayout mCustomerinfo;
    private TextView orderText;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_view_order);
        FirebaseDatabase.getInstance().getReference().child("Users").child("Delivery").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("CustomerID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    CustomerID = snapshot.getValue().toString();
                    DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(CustomerID).child("Order");
                    orderRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                mCustomerinfo.setVisibility(View.VISIBLE);
                                for (DataSnapshot ds : task.getResult().getChildren()) {
                                    String key = ds.getKey();
                                    String value = ds.getValue(String.class);
                                    orderlist=orderlist+key+"-->"+value+"\n\n";
                                }
                                orderText.setText(orderlist);
                            }
                            else {
                                Log.d("TAG", task.getException().getMessage()); //Don't ignore potential errors!
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mCustomerinfo = findViewById(R.id.Deliv_Info);
        orderText=findViewById(R.id.Customerorder);
        button=findViewById(R.id.button5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DriverViewOrder.this,DriversMapsActivity.class);
                startActivity(intent);
            }
        });
    }
}