package com.example.androidproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TimerService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static CountDownTimer timer = null;
    public static boolean running = true;
    public static boolean notified = false;
    String CHANNEL_ID = "Schedule App Channel";

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "ScheduleApp", importance);
            channel.setDescription("Used for timer that checks login session");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(getApplicationContext(), AppActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.unknown)
                .setContentTitle("ScheduleApp")
                .setContentText("Your session will expire in 20s!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notifManager = NotificationManagerCompat.from(getApplicationContext());

        timer = new CountDownTimer(1 * 30 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                if(millisUntilFinished< 20*1000 && !notified)
                {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                        notifManager.notify(1, builder.build());
                    }
                    notified = true;
                }
            }

            public void onFinish() {
                running = false;
                //stopSelf();
            }
        };

    }
}
