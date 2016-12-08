package com.noxx.boostyourfeets;

import android.Manifest;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lukedeighton.wheelview.WheelView;
import com.mindandgo.locationdroid.LocationDroid;
import com.xavierbauquet.theo.Theo;
import com.xavierbauquet.theo.annotations.location.AccessFineLocation;


public class MainActivity extends AppCompatActivity {

    private boolean isPlaying = false;
    private MetronomeAsyncTask metronomeAsyncTask;
    public static final double DEFAUL_RPM = 90.0;
    private FloatingActionButton fab;
    private double rpm = DEFAUL_RPM;

    private SharedPreferences saveLastRpm;
    private LocationDroid locationDroid;
    private TextView kmhText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saveLastRpm = getPreferences(MODE_PRIVATE);

        WheelView wheelView = (WheelView) findViewById(R.id.wheelview);
        kmhText = (TextView) findViewById(R.id.speed);
        final TextView rpmScore = (TextView) findViewById(R.id.rpm_score);

        wheelView.setOnWheelAngleChangeListener(new WheelView.OnWheelAngleChangeListener() {
            @Override
            public void onWheelAngleChange(float angle) {
                if (metronomeAsyncTask != null) {
                    metronomeAsyncTask.setBpm(angleToRpm(angle));
                    rpmScore.setText(String.valueOf(Math.round(angleToRpm(angle))));
                }
            }
        });


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isPlaying) {
                    metronomeAsyncTask = new MetronomeAsyncTask();
                    metronomeAsyncTask.setBpm(rpm);
                    metronomeAsyncTask.execute();
                    isPlaying = true;
                } else {
                    metronomeAsyncTask.onCancelled();
                    isPlaying = false;
                }
            }
        });
    }

    @Override
    @AccessFineLocation
    protected void onStart() {
        super.onStart();
        initLocationDroid();
        rpm = Double.valueOf(saveLastRpm.getString("last rpm", String.valueOf(DEFAUL_RPM)));
    }

    private void initLocationDroid() {
        if(Theo.isPermissionGranted(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)){
            location();
        }else{
            kmhText.setText("Error");
        }
    }

    @Override
    protected void onStop() {
        SharedPreferences.Editor editor = saveLastRpm.edit();
        editor.putString("last rpm", String.valueOf(rpm));
        editor.commit();
        if(locationDroid!=null){
            locationDroid.stop();
        }
        super.onStop();
    }

    public double angleToRpm(float angle) {
        rpm = Math.round(DEFAUL_RPM + ((60.0 / 360.0) * angle));
        return rpm;
    }

    @SuppressWarnings("MissingPermission")
    public void location() {
        locationDroid = new LocationDroid(this) {
            @Override
            public void onNewLocation(Location location) {
                kmhText.setText(String.valueOf(Math.round(location.getSpeed()))+" Kmh");
            }

            @Override
            public void onProviderEnabled(String s) {
                // TODO : do something when a provider is enabled
            }

            @Override
            public void onProviderDisabled(String s) {
                // TODO : do something when a provider is disabled
            }

            @Override
            public void serviceProviderStatusListener(String s, int i, Bundle bundle) {

            }
        };

        locationDroid.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==Theo.REQUEST_CODE) {
           initLocationDroid();
        }

    }
}
