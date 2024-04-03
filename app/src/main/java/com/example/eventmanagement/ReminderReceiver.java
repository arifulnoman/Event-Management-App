package com.example.eventmanagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        MediaPlayer mediaPlayer = MediaPlayer.create(context,R.raw.my_alarm);

        mediaPlayer.start();

        Toast.makeText(context, "It's time for your reminder!", Toast.LENGTH_SHORT).show();
    }
}
