/*  Copyright 2017 by AlaskaLinuxUser (https://thealaskalinuxuser.wordpress.com)
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/
package com.alaskalinuxuser.hourglass;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;

public class MainActivity extends AppCompatActivity {

    // Define the imageView.
    ImageView myHourGlass;
    ImageView vibe, recure;
    // Define the sound choice button.
    Button soundChoices;
    // Define the media player.
    MediaPlayer mp;
    // Define the text we wish to set.
    TextView timeGoing;
    // Define the seekbar.
    SeekBar timeBar;
    // Define the countdown timer.
    CountDownTimer timeCount;
    // Define the integers.
    int maxTime, curTime, soundNum, recureTime;
    // Define the strings.
    String minutes, seconds, mi, se, theButton;
    // Declare our boolean.
    boolean vibrateYes, recureYes;
    // Declare the manual time string and edittext box.
    String manualTime;
    EditText manualText;
    // Declare the manual layout entry view.
    LinearLayout manualEntryLayout;
    // Get the application context for the notification.
    public Context context;
    // Define the notificationmanager.
    NotificationManager mNotificationManager;
    // Define stopped boolean.
    boolean stoppedTimer;

    // Call this method for the time.
    public void theTime (int timeStillLef) {

        // To get the rounded down number of "whole" minutes.
        int m = timeStillLef/60000;
        // To get the remainder in seconds.
        int s = (timeStillLef - (m*60000))/1000;

        mi = String.valueOf(m);
        se = String.valueOf(s);

        if (m <=9) {

            minutes = "0" + mi;

        } else {

            minutes = mi;

        }

        if (s <= 9) {

            seconds = "0" + se;

        } else {

            seconds = se;

        }

        // Set the time.
        timeGoing.setText(minutes + ":" + seconds);

    }

    // What to do when the hourglass is clicked.
    public void hourClick (View view) {

        if (stoppedTimer) {

            recureTime = timeBar.getProgress();
            startCount(recureTime, 1000);

        } else {

            // Stop the timer with cancel, and set the boolean for it being stopped.
            timeCount.cancel();
            stoppedTimer = true;

            // Spin the hourglass for a cool special affect.
            myHourGlass.animate().rotation(0f).setDuration(1000).start();

            // Set the time bar back to usable.
            timeBar.setEnabled(true);

            // Cancel the notification, based on it's number, in this case, 0.
            mNotificationManager.cancel(0);

        }

    }

    public void startCount (int allotedTime, final int countBy) {

        // First, let's stop the time bar from working until the timer is stopped
        // or cancelled.
        timeBar.setEnabled(false);

            /* Let's make a notification, so the user doesn't forget that they have a timer running.
             * I only want it to display if the timer is actually running.
             * I am giving them the option to clear the notification, it is not persistent.
             *
             */
        String intentPhrase = "";

        if (recureYes) {
            intentPhrase = "Your recurring hourglass timer is running.";
        } else {
            intentPhrase = "Your hourglass timer is running.";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.hourglass).setWhen(System.currentTimeMillis()+recureTime).setUsesChronometer(true)
                .setContentTitle("Hourglass").setContentText(intentPhrase);

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pIntent);
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notif = builder.build();
        mNotificationManager.notify(0, notif);

        timeCount = new CountDownTimer(allotedTime, countBy) {

            @Override
            public void onTick(long timeLeft) {

                // Update the time every tick with the method "theTime" based on the long "timeLeft".
                theTime((int) timeLeft);

            }

            // What to do when the timer is done.
            @Override
            public void onFinish() {

                // Spin the hourglass for a cool special affect.
                myHourGlass.animate().rotation(0f).setDuration(1000).start();

                //Play the sound file.
                playThatSound();

                // And vibrate, if the permission was granted, for 2 seconds.
                if (vibrateYes) {

                    Vibrator vibRate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibRate.vibrate(2000);

                }

                if (recureYes) {
                    //
                    // And make a toast popup.
                    Toast timeUp = Toast.makeText(getApplicationContext(),
                            "Time is up! Restarting timer.", Toast.LENGTH_LONG);
                    timeUp.show();

                    startCount(recureTime, countBy);

                } else {
                    // Cancel the notification.
                    mNotificationManager.cancel(0);

                    // Set the time bar back to usable.
                    timeBar.setEnabled(true);

                    // Set the time.
                    timeGoing.setText("00:00");

                    // And make a toast popup.
                    Toast timeUp = Toast.makeText(getApplicationContext(), "Time is up!",
                            Toast.LENGTH_LONG);
                    timeUp.show();
                }
            }
        };

        // Spin the hourglass for a cool special affect.
        myHourGlass.animate().rotation(360f).setDuration(1000).start();

        // Start the timer and set the boolean for running.
        timeCount.start();
        stoppedTimer = false;

    }// end startCount

    // Our method that is called to play whichever sound was chosen....
    public void playThatSound() {

        // Fancy footwork to turn thebutton into the integer for the sound we want.
        int mpSound = getResources().getIdentifier(theButton, "raw", "com.alaskalinuxuser.hourglass");

        // Define the sound file.
        mp = MediaPlayer.create(this, mpSound);

        //Play the sound file.
        mp.start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the screen orientation to portrait to keep the screen rotation bug from stoping
        // the timer on some phones.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Define our manual text entry field.
        manualText = (EditText)findViewById(R.id.manualTimeText);
        // And give it a default number.
        manualText.setText("5:00");

        // Define our context for our notification.
        context = getApplicationContext();

        // Yes, we set vibrate to on by default.
        vibrateYes = true;
        vibe = (ImageView) findViewById(R.id.vibView);
        vibe.setImageResource(R.drawable.vibs);
        recureYes = false;
        recure = (ImageView) findViewById(R.id.recurView);
        recure.setImageResource(R.drawable.notrecur);

        // Define the manual entry layout view.
        manualEntryLayout = (LinearLayout) findViewById(R.id.manEntryLayout);
        manualEntryLayout.setVisibility(View.INVISIBLE);

        // Define our button for sound choice.
        soundChoices = (Button)findViewById(R.id.choiceButton);

        // So we set the default sound.
        theButton = "shipbell";
        soundNum = 1;
        // And the default sound button text.
        soundChoices.setText("Bell");

        // Set the timer boolean to true for stopped, since we just opened the app.
        stoppedTimer = true;

        // Define the resources.
        timeGoing = (TextView)findViewById(R.id.timerView);
        myHourGlass = (ImageView)findViewById(R.id.imageGlass);

        // Define the maximum time in milliseconds.
        maxTime = 3600000;

        // Defining the seekbar wich will act as a volume control
        timeBar = (SeekBar) findViewById(R.id.seekBarTime);

        // Define the maximum value and current value for seekbar.
        timeBar.setMax(maxTime);
        // The curent time could be set to what ever you want. But I have it set to nothing, or 0.
        timeBar.setProgress(curTime);

        // Set up our seekbar to listen to changes.
        timeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                // Do method "theTime" in reference to int "i".
                theTime(i);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                // What to do when they touch the seekbar. In this case, nothing.

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                // What to do when they release the seekbar. In this case, nothing.

            }
        });


    }

    // By request, a choice to change the sound used for the alarm.
    public void choiceSound (View soundChoice) {

        if (soundNum == 1) {

            // So we set the new sound.
            theButton = "horn";
            soundNum = 2;
            // And the default sound button text.
            soundChoices.setText("Horn");

        } else if (soundNum == 2){

            // So we set the new sound.
            theButton = "chirp";
            soundNum = 3;
            // And the default sound button text.
            soundChoices.setText("chirp");

        } else {

            // So we set it back to the default sound.
            theButton = "shipbell";
            soundNum = 1;
            // And the default sound button text.
            soundChoices.setText("Bell");

        }

    }

    public void vibChoice (View vibView) {

        if (vibrateYes) {

            // Set the boolean.
            vibrateYes = false;
            // Set the image.
            vibe.setImageResource(R.drawable.novibs);

        } else {

            // Set the boolean.
            vibrateYes = true;
            // Set the image.
            vibe.setImageResource(R.drawable.vibs);

        }

    }

    public void recurChoice (View recurView) {

        if (recureYes) {

            // Set the boolean.
            recureYes = false;
            // Set the image.
            recure.setImageResource(R.drawable.notrecur);

        } else {

            // Set the boolean.
            recureYes = true;
            // Set the image.
            recure.setImageResource(R.drawable.recur);

        }

    }

    // When we click on the text for manual entry.
    public void manualEntry (View manView) {

        // Make sure we stop the timer if they want to manually set it to something else.
        if (stoppedTimer == false) {
            // Stop the timer with cancel, and set the boolean for it being stopped.
            timeCount.cancel();
            stoppedTimer = true;

            // Spin the hourglass for a cool special affect.
            myHourGlass.animate().rotation(0f).setDuration(1000).start();

            // Set the time bar back to usable.
            timeBar.setEnabled(true);

            // Cancel the notification, based on it's number, in this case, 0.
            mNotificationManager.cancel(0);

        }

        // Show our manual entry view.
        manualEntryLayout.setVisibility(View.VISIBLE);

    }

    // Ok, we chose a manual time entry, let's use it!
    public void okGo (View okView) {

        // First, let's hide the manual view screen.
        manualEntryLayout.setVisibility(View.INVISIBLE);

        // Now, let's set out text view and timer.
        manualTime = manualText.getText().toString();

        // A check to make sure the string is not nothing.
        if (manualTime == "") {

            // No time was entered, so nothing should be done.
            Log.i("WJH", "Nothing");

            // And if time was entered, then do this.
        } else {

            // A log entry for testing purposes.
            // Log.i("WJH", manualTime);

            // We wrap this in try, so the app can't crash if they put in some weird number....
            try {
                    int sec,min,math;
                // Alright, let's split that time based on colon.
                if (manualTime.contains(":")) {
                    String[] foundSplit = manualTime.split(":");
                    // Testing only // Log.i("WJH", foundSplit[0]);
                    // Testing only // Log.i("WJH", foundSplit[1]);

                    // And let's get the seconds from the split.
                    String seconds = foundSplit[1];

                    // And the minutes from the split.
                    String minutes = foundSplit[0];

                    // Then we convert them to integers.
                    sec = Integer.parseInt(seconds);
                    min = Integer.parseInt(minutes);
                } else {
                    sec = 0;
                    min = Integer.parseInt(manualTime);
                    // Testing only // Log.i("WJH", String.valueOf(min));
                }

                // And do some basic math to turn them into milliseconds.
                math = (min * 60 * 1000) + (sec * 1000);

                // A test point for me.
                // Log.i("WJH", String.valueOf(math));

                // Now we set the progress bar on the time bar to the new number, so they can use it.
                timeBar.setProgress(math);

                // We need an escape clause, of coarse.
            } catch (Exception e) {

                // To which we don't do anything, except a little log note.
                Log.e("WJH", "exception");

            }

        }

        // The problem was, after enting time and clicking okay. Now we have a huge keyboard in the
        // way. So let's get rid of it. We do this with try/catch in the event that the keyboard
        // never came up....
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(okView.getWindowToken(), 0);
        } catch (Exception e) {
            // Log it if we can't hide it.
            Log.e("WJH", "We can't hide the keyboard.");
        }

    }

    // And our aboutclick to tell us all about the app.
    public void aboutClick (View v)
    {
        // Call an intent to go to the second screen when you click the about button.
        // First you define it.
            Intent myintent = new Intent(MainActivity.this, SetScaleActivity.class);
        // Now you call it.
            startActivity(myintent);
    }

}
