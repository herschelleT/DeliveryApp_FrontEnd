package com.example.deliveryapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.deliveryapp.Models.Users;
import com.example.deliveryapp.databinding.ActivityDriversMapsBinding;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public class DriversMapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;private ActivityDriversMapsBinding binding;GoogleApiClient mGoogleapiclient;Location lastlocation;LatLng driverLatLng,driverLatLong;LocationRequest LocReq;LocationManager locationManager;String provider,phone;Button OrderPlace,call;double locationLat=0;double locationLon=0;ValueEventListener CustomerrefVal;private LinearLayout mCustomerinfo;private ImageView mCustomerProfile;private TextView CustomerName;private String CustomerID="";String userID;float distance;private boolean Loggingout=false;DatabaseReference custref;
    String userId;
    boolean order=true,Free=true;
    int check=0;
    DatabaseReference Order;
    Location loc1,loc2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDriversMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference database=FirebaseDatabase.getInstance().getReference("Users").child("Delivery").child(userId).child("CustomerID");//To get customer ID
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull  DataSnapshot snapshot) {
                    if(snapshot.exists()){//checks if customer is given to the person
                        CustomerID= Objects.requireNonNull(snapshot.getValue()).toString();
                        getAssignedCustomerLoc();
                        Order=FirebaseDatabase.getInstance().getReference().child("Pending Verification").child(CustomerID).child(userID);
                        Order.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    ProgressDialog Alert = new ProgressDialog(DriversMapsActivity.this);
                                    Alert.setTitle("Please Wait");
                                    Alert.setMessage("We are checking with the user if order is completed");
                                    Alert.setButton("Call", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Intent.ACTION_DIAL);
                                            intent.setData(Uri.parse("tel:" + phone));
                                            startActivity(intent);
                                        }
                                    });
                                    Alert.show();
                                    Order.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(!snapshot.exists()){
                                                Free=false;
                                                CustomerID="";
                                                check=1;
                                                Alert.dismiss();
                                                Intent intent=new Intent(DriversMapsActivity.this,BufferPageActivity.class);
                                                startActivity(intent);
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
                        });//checks if there is a order yet to be verified
                        custref = FirebaseDatabase.getInstance().getReference("Users").child("Customer").child(CustomerID);
                        custref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    List<Object> map1 = (List<Object>) snapshot.child("Location").child("l").getValue();
                                    double locationLat = 0;
                                    double locationLng = 0;
                                    assert map1 != null;
                                    if (map1.get(0) != null) {
                                        locationLat = Double.parseDouble(map1.get(0).toString());
                                    }
                                    if (map1.get(1) != null) {
                                        locationLng = Double.parseDouble(map1.get(1).toString());
                                    }
                                    driverLatLng = new LatLng(locationLat, locationLng);
                                    loc1 = new Location("");
                                    loc1.setLatitude(driverLatLng.latitude);
                                    loc1.setLongitude(driverLatLng.longitude);
                                }
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }//
                    else {
                        CustomerID="";

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            call = findViewById(R.id.Call);
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone));
                    startActivity(intent);
                }
            });//TO call the customer
            OrderPlace = findViewById(R.id.Orderdone);

            OrderPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!CustomerID.equals("")) {

                        DatabaseReference Driverlocref = FirebaseDatabase.getInstance().getReference().child("DeliveryWorking").child(userID);
                        Driverlocref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    List<Object> map = (List<Object>) snapshot.child("l").getValue();
                                    if (map != null) {
                                        double locationLatit = 0;
                                        double locationLong = 0;
                                        if (map.get(0) != null) {
                                            locationLatit = Double.parseDouble(map.get(0).toString());
                                        }
                                        if (map.get(1) != null) {
                                            locationLong = Double.parseDouble(map.get(1).toString());
                                        }
                                        driverLatLong = new LatLng(locationLatit, locationLong);
                                        loc2 = new Location("");
                                        loc2.setLatitude(driverLatLong.latitude);
                                        loc2.setLongitude(driverLatLong.longitude);
                                        distance = loc1.distanceTo(loc2);
                                        if (order) {
                                            if (distance > 100) {//checks if order is completed
                                                Toast.makeText(DriversMapsActivity.this, "Please complete order", Toast.LENGTH_SHORT).show();
                                            } else {
                                                ProgressDialog Alert = new ProgressDialog(DriversMapsActivity.this);
                                                Alert.setTitle("Please Wait");
                                                Alert.setMessage("We are checking with the user if order is completed");//waits if the order is completed
                                                Alert.setButton("Call", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                                        intent.setData(Uri.parse("tel:" + phone));
                                                        startActivity(intent);
                                                    }
                                                });
                                                Alert.show();
                                                Order.setValue("Waiting");
                                                ValueEventListener orderref=Order.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if(!(snapshot.exists())){//if order is completed
                                                            Free=false;
                                                            CustomerID="";
                                                            check=1;
                                                            Alert.dismiss();
                                                            Intent intent=new Intent(DriversMapsActivity.this,BufferPageActivity.class);
                                                            startActivity(intent);

                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                                order = false;

                                            }
                                        }


                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });






                    }
                }
            });
            mCustomerinfo = findViewById(R.id.Customerinfo);
            mCustomerProfile = findViewById(R.id.Profile);
            CustomerName = findViewById(R.id.Customername);
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            assert mapFragment != null;
            mapFragment.getMapAsync(this);
            getAssignedCustomer();
        }
        else{
            Intent intent=new Intent(DriversMapsActivity.this,Login_Page.class);
            startActivity(intent);
        }
    }
    private void getAssignedCustomerinfo(){
        mCustomerinfo.setVisibility(View.VISIBLE);
        if(!CustomerID.equals("")) {
            DatabaseReference Customerref = FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(CustomerID);
            Customerref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        Users usersnapshot = snapshot.getValue(Users.class);
                    if (usersnapshot != null && (!CustomerID.equals("")) && Free) {
                        Customerref.child("DriverID").setValue(userID);//Adds Driver ID To customerID child
                        Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                        String username = usersnapshot.getUsername();
                        CustomerName.setText(username);
                        phone = usersnapshot.getphone();
                        if (map.get("profileImageUri") != null) {
                            Glide.with(getApplication()).load(map.get("profileImageUri").toString()).into(mCustomerProfile);//showing User profile
                        }
                        if (map.get("Location") != null) {

                        }
                    }
                    else {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(CustomerID);
                        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    reference.setValue(null);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    private void getAssignedCustomer(){
        String driverID=FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference CustomRef=FirebaseDatabase.getInstance().getReference().child("Users").child("Delivery").child(driverID).child("CustomerID");
        CustomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if(snapshot.exists()){//Checking if customer was  assigned to  person
                    CustomerID=snapshot.getValue().toString();
                    getAssignedCustomerinfo();
                    getAssignedCustomerLoc();
                }
                else{
                    CustomerID="";//Once order cancelled Customer ID is made back to null
                    if(pickup_marker !=null){
                        pickup_marker.remove();
                    }

                    mCustomerinfo.setVisibility(View.GONE);
                    CustomerName.setText("");
                    mCustomerProfile.setImageResource(R.mipmap.ic_launcher);
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }
    Marker pickup_marker;
    DatabaseReference CustomPickupRef;

    private void getAssignedCustomerLoc(){
        CustomPickupRef=FirebaseDatabase.getInstance().getReference("Users").child("Customer").child(CustomerID).child("Location").child("l");
        CustomPickupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if(snapshot.exists()){
                    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Customer request");
                    reference.child(CustomerID).removeValue();//Removing customer from customer request
                    List<Object> map=(List<Object>) snapshot.getValue();

                    if(map.get(0)!=null){
                        locationLat=Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1)!=null){
                        locationLon=Double.parseDouble(map.get(1).toString());
                    }
                    driverLatLng=new LatLng(locationLat,locationLon);
                    pickup_marker =mMap.addMarker(new MarkerOptions().position(driverLatLng).title("pickupLocation"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(driverLatLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                }

            }


            @Override
            public void onCancelled(@NonNull  DatabaseError error) {}

        });

    }



    //Building Map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mMap = googleMap;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            } else {
            }
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);

        }
    }
    protected synchronized void buildGoogleApiClient() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mGoogleapiclient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleapiclient.connect();
        }
    }
    //Checking location
    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (getApplicationContext() != null) {

                lastlocation = location;
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if(driverLatLng==null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(21));
                }
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Delivery Available");
                DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("DeliveryWorking");
                GeoFire geoFire = new GeoFire(reference);
                GeoFire geoFire2 = new GeoFire(refWorking);
                switch (CustomerID) {
                    case "":
                        geoFire2.removeLocation(userID);
                        geoFire.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));
                        break;
                    default:
                        geoFire.removeLocation(userID);
                        geoFire2.setLocation(userID, new GeoLocation(location.getLatitude(), location.getLongitude()));
                        break;

                }

            }


        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            LocReq = new LocationRequest();
            LocReq.setInterval(4000);
            LocReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//Checking permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleapiclient, LocReq, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull  ConnectionResult connectionResult) {

    }

    private void disconnect_driver(){
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleapiclient, this);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Delivery Available");
            ref.child(userId).removeValue();
        }

    }
    @Override
    protected void onStop() {
        super.onStop();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (!Loggingout) {
                disconnect_driver();
            }
        }
    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //Request location updates:
                        locationManager.requestLocationUpdates(provider, 400, 1, (LocationListener) this);
                    }

                } else {
                    Toast.makeText(this,"Access denied",Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile:
                Intent intent=new Intent(DriversMapsActivity.this,Profile.class);
                startActivity(intent);
                break;

            case R.id.logout:
                Loggingout=true;
                disconnect_driver();
                FirebaseAuth mAuth = FirebaseAuth.getInstance ();
                mAuth.signOut();
                finish();
                Intent intents =new Intent(DriversMapsActivity.this,Login_Page.class);
                startActivity(intents);
                break;
            case R.id.Delete_ACCOUNT:
                AlertDialog.Builder Alert=new AlertDialog.Builder(DriversMapsActivity.this);
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
                        FirebaseAuth mAuth = FirebaseAuth.getInstance ();
                        mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(DriversMapsActivity.this,"Account Deleted",Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(DriversMapsActivity.this,SignUp.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(DriversMapsActivity.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
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
                break;
            case R.id.ViewOrder:
                DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(CustomerID).child("Order");
                orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            Intent intent=new Intent(DriversMapsActivity.this,DriverViewOrder.class);
                            startActivity(intent);
                        }
                        else{
                            AlertDialog.Builder Alert=new AlertDialog.Builder(DriversMapsActivity.this);
                            Alert.setTitle("ERROR");
                            Alert.setMessage("You haven't received any order");
                            Alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            Alert.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        }
        return super.onOptionsItemSelected(item);
    }



}