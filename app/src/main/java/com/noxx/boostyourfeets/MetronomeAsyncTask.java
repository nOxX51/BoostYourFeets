package com.noxx.boostyourfeets;

import android.os.AsyncTask;

/**
 * Created by HB on 07/12/2016.
 */

public class MetronomeAsyncTask extends AsyncTask {

    private Metronome metronome;

    public void setBpm(double bpm) {
        metronome.setBpm(bpm);
    }

    public MetronomeAsyncTask() {
        this.metronome = new Metronome();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        metronome.play();
        return null;
    }

    @Override
    protected void onCancelled() {
        metronome.stop();
        super.onCancelled();
    }

    @Override
    protected void onCancelled(Object o) {
        metronome.stop();
        super.onCancelled(o);
    }
}
