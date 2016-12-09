package com.dafasoft.miuiclock;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends Activity {

    private MIUIClock mMIUIClock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMIUIClock = (MIUIClock) findViewById(R.id.miui_clock);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mMIUIClock.startAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMIUIClock.cancelAnimation();
    }
}
