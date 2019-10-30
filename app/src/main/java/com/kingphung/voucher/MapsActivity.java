package com.kingphung.voucher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.kingphung.voucher.model.MyLatLng;
import com.kingphung.voucher.model.Resaurant;
import com.kingphung.voucher.model.RestaurantItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //firebase
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference userRef, voucherRef;
    FirebaseUser currentUser;
    ValueEventListener voucherValueListener;

    //location
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location mLastKnownLocation;
    private final LatLng mDefaultLocation = new LatLng(38.87104557, -77.05596332);
    private static final int DEFAULT_ZOOM = 15;
    private boolean mLocationPermissionGranted;
    private boolean gps_enabled;
    boolean gotTheGPS = false;


    private GoogleMap mMap;
    private ClusterManager<Resaurant> clusterManager;

    private final static int RC_SIGN_IN = 25;
    private final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 125;

    //var
    ArrayList<Resaurant> listRestaurant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        userRef = database.getReference("user");
        voucherRef = database.getReference("voucher");

        Resaurant.CreateDataScript(voucherRef);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (auth.getCurrentUser() != null) {
            // already signed in
           Log.d("KIG-error","Signed in successfully!");
            getLocationPermission();
            if(mLocationPermissionGranted){
                checkEnableGPS();
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
                Log.d("KIG","map created");
            }

        } else {
            // not signed in
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build()))
                            .build(),
                    RC_SIGN_IN);
        }

    }

    private void checkEnableGPS() {
        LocationManager lm = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE ) ;
         gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager. NETWORK_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(MapsActivity. this )
                    .setMessage( "GPS Enable" )
                    .setPositiveButton( "Settings" , new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick (DialogInterface paramDialogInterface , int paramInt) {
                                    startActivity( new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS )) ;
                                }
                            })
                    .setNegativeButton("Cancel", new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(MapsActivity.this, "Please enable GPS", Toast.LENGTH_LONG).show();
                                }
                    })
                    .show() ;
        }
        Log.d("KIG","dialog");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getDeviceLocation();//after get device location, add a listener to get list restaurant -> draw maker
        updateLocationUI();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("KIG","resume");
        if(!gotTheGPS){
            LocationManager lm = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE ) ;
            gps_enabled = false;
            try {
                gps_enabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
            } catch (Exception e) {
                e.printStackTrace() ;
            }

            if(gps_enabled)  getDeviceLocation(0);
            else  Toast.makeText(MapsActivity.this, "Please enable GPS", Toast.LENGTH_LONG).show();

            updateLocationUI();
        }

    }

    private void addVoucherValueListener() {
        if(voucherValueListener==null){
            voucherValueListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    getListRestaurant(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            voucherRef.addValueEventListener(voucherValueListener);
        }

    }

    private void drawRestaurantMaker() {
        setUpCluster();
        clusterManager.addItems(listRestaurant);

    }
    private void setUpCluster() {
        if(clusterManager==null)
        {
            clusterManager = new ClusterManager<Resaurant>(this, mMap);
            mMap.setOnCameraIdleListener(clusterManager);
            mMap.setOnMarkerClickListener(clusterManager);
            clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<Resaurant>() {
                @Override
                public boolean onClusterItemClick(Resaurant resaurant) {
                    openVoucherPagerActivity(resaurant);
                    return true;
                }
            });
        }
    }

    private void openVoucherPagerActivity(Resaurant resaurant) {
        Intent intent = new Intent(MapsActivity.this, SlideVoucherActivity.class);
        intent.putParcelableArrayListExtra("LIST_VOUCHER", resaurant.getListVoucher());
        startActivity(intent);
    }


    private void getListRestaurant(DataSnapshot dataSnapshot) {

        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses = null;
        Address address;

        listRestaurant = new ArrayList<>();
        listRestaurant.clear();
        for(DataSnapshot i : dataSnapshot.getChildren()){
            Resaurant resaurant = i.getValue(Resaurant.class);
            try {
                addresses = geocoder.getFromLocationName(resaurant.getAddress(),1);
                address = addresses.get(0);
                resaurant.setPosition(new MyLatLng(address.getLatitude(),address.getLongitude()));

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something wrong: "+e.toString(), Toast.LENGTH_SHORT).show();

            }

            listRestaurant.add(resaurant);
            Log.d("'KIG",resaurant.getId());
        }

        drawRestaurantMaker();
    }

    private void getLocationPermission() {
        Log.d("KIG-e","get location permission");
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }else{
                    getLocationPermission();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,"Signed in successfully!",Toast.LENGTH_SHORT).show();
                getLocationPermission();
            }else{
                Toast.makeText(this,"Something wrong, try again!",Toast.LENGTH_SHORT).show();
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.GoogleBuilder().build()))
                                .build(),
                        RC_SIGN_IN);
            }
        }
    }


    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                    Task locationResult = fusedLocationProviderClient.getLastLocation();


                    locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if (task.isSuccessful() && task.getResult()!=null) {
                                // Set the map's camera position to the current location of the device.
                                gotTheGPS = true;
                                mLastKnownLocation = (Location) task.getResult();
                                addVoucherValueListener();
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                Log.d("KIG","GET my location ...");
                            } else {
                                    Log.d("KIG", "Current location is null. Using defaults.");
                                    Log.e("Loi", "Exception: %s", task.getException());
                                    mLastKnownLocation = new Location("provider");
                                    mLastKnownLocation.setAltitude(mDefaultLocation.latitude);
                                    mLastKnownLocation.setLongitude(mDefaultLocation.longitude);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                                }

                            }
                    });
            }
        } catch(SecurityException e)  {
            Log.d("KIG-exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation(int a) {
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = fusedLocationProviderClient.getLastLocation();


                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful() && task.getResult()!=null) {
                            // Set the map's camera position to the current location of the device.
                            gotTheGPS = true;
                            mLastKnownLocation = (Location) task.getResult();
                            addVoucherValueListener();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            Log.d("KIG","GET my location 0000000000");
                        } else {
                            getDeviceLocation(0);
                        }

                    }
                });
            }
        } catch(SecurityException e)  {
            Log.d("KIG-exception: %s", e.getMessage());
        }
    }
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
               // mLastKnownLocation = null; //????????
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}
