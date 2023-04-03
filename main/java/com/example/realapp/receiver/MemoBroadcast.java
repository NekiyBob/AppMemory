package com.example.realapp.receiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.realapp.R;
import com.example.realapp.activities.CreateNoteActivity;
import com.example.realapp.activities.MainActivity;

public class MemoBroadcast extends BroadcastReceiver {
    private String NotificationText = CreateNoteActivity.inputNoteTextFornotif;
    private String NotificationTitle = CreateNoteActivity.inputNoteTitleForNotif;

    int changingChannelIdBuilder = 1;
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    // Запуск приложения при клике на уведомление
    public void onReceive(Context context, Intent intent) {
        Intent repeating_Intent  = new Intent(context, MainActivity.class);
        repeating_Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (NotificationText.equals("")){
            NotificationText = "Нет текста";
        }
        if (NotificationTitle.equals("")){
            NotificationTitle = "";
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, MainActivity.codeForNotificationChannel, repeating_Intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Integer.toString(changingChannelIdBuilder))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentText(NotificationText)
                .setContentTitle("Пора повторить: " + NotificationTitle)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(MainActivity.codeForNotificationChannel, builder.build());
    }

}