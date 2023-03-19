package com.example.deliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.deliveryapp.Models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Objects;

public class ViewOrder extends AppCompatActivity {
    Button call,Edit,cancelOrder,Back;
    String userID, driverID, phone,orderlist="";
    DatabaseReference Ref, DelivRef;
    private TextView CustomerName,orderText;
    private ImageView mCustomerProfile;
    private LinearLayout mCustomerinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        mCustomerinfo = findViewById(R.id.Deliv_Info);
        CustomerName = findViewById(R.id.Customername);
        orderText=findViewById(R.id.Customerorder);
        mCustomerProfile = findViewById(R.id.Profile);
        Back=findViewById(R.id.Back);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewOrder.this,Custhomepage.class);
                startActivity(intent);
            }
        });
        cancelOrder=findViewById(R.id.Remove_order);
        call = findViewById(R.id.Call);
        Edit=findViewById(R.id.Edit_order);
        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewOrder.this,Shop.class);
                startActivity(intent);
            }
        });
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(userID).child("DriverID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.exists())) {
                    AlertDialog.Builder Alert = new AlertDialog.Builder(ViewOrder.this);
                    Alert.setTitle("Error");
                    Alert.setMessage("You don't have a pending order");
                    Alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ViewOrder.this, Custhomepage.class);
                            startActivity(intent);
                        }
                    });
                    Alert.show();

                }//checks if there is an order placed by the user
                else {
                    call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + phone));
                            startActivity(intent);
                        }
                    });//to call the delivery person
                    Ref = FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(userID).child("DriverID");
                    Ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                driverID = snapshot.getValue().toString();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    DelivRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Delivery");
                    DelivRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Users usersnapshot = snapshot.getValue(Users.class);
                            if (usersnapshot != null) {
                                mCustomerinfo.setVisibility(View.VISIBLE);
                                Map<String, Object> map = (Map<String, Object>) snapshot.child(driverID).getValue();
                                String username = (String) map.get("username");
                                phone = map.get("phone").toString();
                                if (map.get("username") != null) {
                                    CustomerName.setText("Driver name:" + username);//shows driver username
                                }
                                if (map.get("profileImageUri") != null) {
                                    Glide.with(getApplication()).load(map.get("profileImageUri").toString()).into(mCustomerProfile);//showing User profile
                                }

                            } else {
                                mCustomerinfo.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(userID).child("Order");
        orderRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot ds : task.getResult().getChildren()) {
                        String key = ds.getKey();
                        String value = ds.getValue(String.class);
                        orderlist=orderlist+key+"-->"+value+"\n";
                    }
                    orderText.setText(orderlist);//orderlist
                } else {
                    Log.d("TAG", task.getException().getMessage()); //potential errors!
                }
            }
        });
        cancelOrder.setOnClickListener(new View.OnClickListener() {//cancelling order
            @Override
            public void onClick(View v) {
                AlertDialog.Builder Alert=new AlertDialog.Builder(ViewOrder.this);
                Alert.setTitle("Are you Sure?");
                Alert.setMessage("Your order will be removed once You press Remove  ");
                Alert.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        orderRef.setValue(null);
                        FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(userID).child("DriverID").setValue(null);
                        FirebaseDatabase.getInstance().getReference("Users").child("Delivery").child(driverID).child("CustomerID").setValue(null);

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
        });
    }
}