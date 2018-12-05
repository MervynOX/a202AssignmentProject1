package com.example.user.a202assignmentproject1;

import android.app.Application;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;


public class Channel extends Application {//creating the notification channel
    public static final String CHANNEL_ID = "channel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notification = new NotificationChannel(
                    CHANNEL_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notification.setDescription("This is a Channel");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notification);
        }
    }
}