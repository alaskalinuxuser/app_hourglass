package com.alaskalinuxuser.hourglass;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import static com.alaskalinuxuser.hourglass.MainActivity.allCancel;


/**
 * Created by alaskalinuxuser on 5/2/18.
 */




public class CancelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the screen orientation to portrait to keep the screen rotation bug from stoping
        // the timer on some phones.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Just log.
        Log.i("WJH", "CancellAll.");
        allCancel = true;
        finish();

    }

}
