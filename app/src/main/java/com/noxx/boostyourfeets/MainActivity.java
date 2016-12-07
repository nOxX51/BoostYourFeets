package com.noxx.boostyourfeets;

import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lukedeighton.wheelview.WheelView;



public class MainActivity extends AppCompatActivity {

    private boolean isPlaying = false;
    private MetronomeAsyncTask metronomeAsyncTask;
    public static final double DEFAUL_RPM = 90.0;
    private FloatingActionButton fab;
    private double rpm = DEFAUL_RPM;

    private SharedPreferences saveLastRpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saveLastRpm = getPreferences(MODE_PRIVATE);

        WheelView wheelView = (WheelView) findViewById(R.id.wheelview);
        //final TextView textView = (TextView) findViewById(R.id.text);
        final TextView rpmScore = (TextView) findViewById(R.id.rpm_score);

        wheelView.setOnWheelAngleChangeListener(new WheelView.OnWheelAngleChangeListener() {
            @Override
            public void onWheelAngleChange(float angle) {


                //textView.setText(angle+"");

                //metronomeAsyncTask.execute();
                if(metronomeAsyncTask != null) {
                    metronomeAsyncTask.setBpm(changeRhythm(angle));
                    rpmScore.setText(String.valueOf(Math.round(changeRhythm(angle))));
                }
            }
        });


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               if (!isPlaying){

                   metronomeAsyncTask = new MetronomeAsyncTask();
                   metronomeAsyncTask.setBpm(rpm);
                   metronomeAsyncTask.execute();
                   isPlaying = true;
               }else {
                   metronomeAsyncTask.onCancelled();
                   isPlaying = false;

               }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        rpm = Double.valueOf(saveLastRpm.getString("last rpm", String.valueOf(DEFAUL_RPM)));
    }

    @Override
    protected void onStop() {
        SharedPreferences.Editor editor = saveLastRpm.edit();
        editor.putString("last rpm",String.valueOf(rpm));
        editor.commit();
        super.onStop();
    }

    public double changeRhythm(float angle) {
        rpm = Math.round(DEFAUL_RPM +((60.0/360.0)*angle));
        return rpm;
    }
}
