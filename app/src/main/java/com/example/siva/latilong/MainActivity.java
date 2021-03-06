package com.example.siva.latilong;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button btn;
    private TextView txt;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionsGranted = false;
    private Context mContext;
    boolean isGPS=false;
    public boolean isLoc=false;
    Location mLocation;
    double mLatitude;
    double mLongitude;
    private static final long time=30000;
    private static final long dist=20;
    protected LocationManager mLocationManager;
    AppLocationService appLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appLocationService=new AppLocationService(MainActivity.this);
        btn=(Button) findViewById(R.id.button);
        txt=(TextView) findViewById(R.id.textview);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                permissioncheck();

                //gps(mContext);
            }
        });

    }

    public void gpslocation(){
        if(mLocationPermissionsGranted){
            Location gpsLocation=appLocationService.getLocation(LocationManager.GPS_PROVIDER);
            if(gpsLocation!=null){
                double latitude=gpsLocation.getLatitude();
                double longitude=gpsLocation.getLongitude();
                txt.setText("Latitude:"+latitude+"  Longitude:"+longitude);
            }
            else{
                showSettingsAlert("GPS");
            }
        }
    }
    public void showSettingsAlert(String provider){
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(provider+" Settings");
        alertDialog.setMessage(provider+" is not enabled! Want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                MainActivity.this.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialog.show();
    }

    public void permissioncheck(){

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(MainActivity.this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(MainActivity.this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                Log.d("msgma","mLocationPermissionGranted=true");
                gpslocation();

            }else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /*public void gps(Context context){
        this.mContext=context;
        mLocationManager=(LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        getlocation();
    }

    public void getlocation(){

        try{
            isGPS=mLocationManager.isProviderEnabled(GPS_PROVIDER);
            if(isGPS){
                mLocationManager.requestLocationUpdates(mLocationManager.requestLocationUpdates();,time,dist,this);
            }
        }
        catch(Exception ex){

        }

    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;

                            return;
                        }
                    }

                    mLocationPermissionsGranted = true;
                    gpslocation();

                }
            }
        }
    }

}
