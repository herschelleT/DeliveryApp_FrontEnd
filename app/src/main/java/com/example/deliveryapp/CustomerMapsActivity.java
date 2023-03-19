package com.example.deliveryapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.deliveryapp.databinding.ActivityCustomerMapsBinding;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class CustomerMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private GoogleMap mMap;
    private ActivityCustomerMapsBinding binding;
    GoogleApiClient mGoogleapiclient;
    Location lastlocation;
    LocationRequest LocReq;
    LocationManager locationManager;
    String provider,userid ,driverID="";
    Button button, Submit;
    LatLng pickuploc;
    DatabaseReference reference;
    Boolean  Notcancelled = true,orderexist=false;
    Marker Pickupmarker;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            userid=FirebaseAuth.getInstance().getCurrentUser().getUid();
            assert mapFragment != null;
            mapFragment.getMapAsync(this);
            FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(userid).child("Order").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        orderexist=true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            Submit = findViewById(R.id.bt_items);
            button = findViewById(R.id.Call);
            button.setOnClickListener(new View.OnClickListener() {//To find the delivery man
                @Override
                public void onClick(View v) {
                    if(orderexist) {
                        DatabaseReference driverref=FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(userid).child("DriverID");
                        driverref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    AlertDialog.Builder Alert=new AlertDialog.Builder(CustomerMapsActivity.this);
                                    Alert.setTitle("Order is already placed");
                                    Alert.setMessage("Please wait for you order to be completed");
                                    Alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent=new Intent(CustomerMapsActivity.this,Custhomepage.class);
                                            startActivity(intent);
                                        }
                                    });
                                    Alert.show();

                                }//checks if order is there
                                else{
                                    Notcancelled = true;
                                    String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Customer request");
                                    GeoFire geoFire = new GeoFire(reference);
                                    geoFire.setLocation(userid, new GeoLocation(lastlocation.getLatitude(), lastlocation.getLongitude()));//adds user in customer request
                                    pickuploc = new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude());
                                    Pickupmarker = mMap.addMarker(new MarkerOptions().position(pickuploc).title("Pickup Here"));
                                    progressDialog = new ProgressDialog(CustomerMapsActivity.this);
                                    progressDialog.setTitle("Please wait");
                                    progressDialog.setMessage("We  are searching ");
                                    progressDialog.setButton("Cancel", new DialogInterface.OnClickListener() {//incase the user decides to cancel
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Notcancelled = false;
                                            FirebaseDatabase.getInstance().getReference().child("Customer request").child(userid).removeValue();//removing from db
                                            if (driverID != null) {
                                                DatabaseReference Driverref = FirebaseDatabase.getInstance().getReference().child("Users").child("Delivery").child(driverID).child("CustomerID");
                                                Driverref.setValue(null);
                                                if (FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(userid).child("DriverID") != null) {
                                                    FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(userid).child("DriverID").setValue(null);
                                                }
                                                driverID = null;
                                            }
                                            geoQuery.removeAllListeners();
                                            found = false;
                                            radius = 0;//
                                            if (DriverlocrefEventlistener != null) {
                                                Driverlocref.removeEventListener(DriverlocrefEventlistener);
                                                GeoFire geoFire = new GeoFire(reference);
                                                geoFire.removeLocation(userid);
                                            }
                                            if (Pickupmarker != null) {
                                                Pickupmarker.remove();
                                            }
                                            if (mdrivermarker != null) {
                                                mdrivermarker.remove();
                                            }
                                        }
                                    });//if the user cancels order
                                    progressDialog.show();
                                    getDriver();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                    else{
                        AlertDialog.Builder Alert=new AlertDialog.Builder(CustomerMapsActivity.this);
                        Alert.setTitle("NO ORDER");
                        Alert.setMessage("In order to call a delivery person You have to place an order");
                        Alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        Alert.show();
                    }
                }
            });


            Submit.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Intent myintent= new Intent(CustomerMapsActivity.this,Custhomepage.class);
                    startActivity(myintent);
                }
            });

        }
        else {
            Intent intent = new Intent(CustomerMapsActivity.this, Login_Page.class);
            startActivity(intent);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            mMap = googleMap;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            } else {
            }
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);

        } else {
            Intent intent = new Intent(CustomerMapsActivity.this, Login_Page.class);
            startActivity(intent);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleapiclient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleapiclient.connect();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            lastlocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if(driverID.equals("")){
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            reference = FirebaseDatabase.getInstance().getReference("Users").child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            reference.child("DriverID").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull  DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        driverID=snapshot.getValue().toString();
                        getDriverlocation();
                    }
                }

                @Override
                public void onCancelled(@NonNull  DatabaseError error) {

                }
            });
            GeoFire geoFire = new GeoFire(reference);
            geoFire.setLocation("Location", new GeoLocation(location.getLatitude(), location.getLongitude()));

        }
        else {
            Intent intent=new Intent(CustomerMapsActivity.this,Login_Page.class);
            startActivity(intent);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            LocReq = new LocationRequest();
            if(driverID.equals("")){
                LocReq.setInterval(4000);
            }
            LocReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleapiclient, LocReq, this);
        }
        else {
            Intent intent=new Intent(CustomerMapsActivity.this,Login_Page.class);
            startActivity(intent);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull  ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleapiclient, this);
    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;



    private int radius=0;
    private boolean found=false;
    GeoQuery geoQuery;
    private void getDriver(){
        DatabaseReference driverslocation=FirebaseDatabase.getInstance().getReference().child("Delivery Available");
        GeoFire geoFire=new GeoFire(driverslocation);
        geoQuery=geoFire.queryAtLocation(new GeoLocation(pickuploc.latitude,pickuploc.longitude),radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(!found&&(Notcancelled)) {//to check if the delivery person is found
                    found = true;
                    driverID=key;
                    progressDialog.dismiss();
                    DatabaseReference Driverref= FirebaseDatabase.getInstance().getReference().child("Users").child("Delivery").child(driverID);
                    String CustomerID=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    HashMap map=new HashMap();
                    map.put("CustomerID",CustomerID);//adds deliveryID to the customer id node
                    Driverref.updateChildren(map);

                    getDriverlocation();
                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                //Checking if driver is found in that radius
                if(!found&&(Notcancelled)){
                    radius++;//increaces the radius   of search
                    Toast.makeText(CustomerMapsActivity.this,"Please WAIT",Toast.LENGTH_SHORT).show();
                    if(radius<=50) {
                        getDriver();
                    }
                    else {
                        radius=0;
                        progressDialog.dismiss();

                        AlertDialog.Builder Alert=new AlertDialog.Builder(CustomerMapsActivity.this);
                        if(!found) {
                            Alert.setTitle("Order wasn't placed");
                            Alert.setMessage("There were no delivery man in your area");
                        }
                        Alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {//cancels all the order
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference().child("Customer request").child(userid).setValue(null);
                                Pickupmarker.remove();
                                geoQuery.removeAllListeners();

                            }
                        });
                        Alert.show();
                        Notcancelled=false;
                    }
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
    DatabaseReference Driverlocref;
    private ValueEventListener DriverlocrefEventlistener;
    private Marker mdrivermarker;
    double locationLat=0;
    double locationLon=0;
    private void getDriverlocation(){
        Driverlocref= FirebaseDatabase.getInstance().getReference().child("DeliveryWorking").child(driverID);
        DriverlocrefEventlistener=Driverlocref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if(snapshot.exists()){
                    List<Object> map=(List<Object>)snapshot.child("l").getValue();
                    if(map.get(0)!=null){
                        locationLat=Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1)!=null){
                        locationLon=Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatLng=new LatLng(locationLat,locationLon);
                    if(mdrivermarker!=null){
                        mdrivermarker.remove();

                    }
                    mdrivermarker=mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Your Driver"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(driverLatLng));
                    Location loc1=new Location("");
                    if(pickuploc==null) {
                        pickuploc = new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude());
                    }
                    loc1.setLatitude(pickuploc.latitude);
                    loc1.setLongitude(pickuploc.longitude);


                }
            }


            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

                //added this
                Driverlocref.removeEventListener(DriverlocrefEventlistener);
            }
        });

    }//updates the location of the deliveryperson

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
                    Toast.makeText(this,"Access denied",Toast.LENGTH_SHORT);
                }
                return;
            }

        }
    }


}


