package com.example.countbloodbottomnav.ui.alarm;

import androidx.core.app.NotificationCompat;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.countbloodbottomnav.SplashActivity;

import static com.example.countbloodbottomnav.MainActivity.CHANNEL_1_ID;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent actionintent = new Intent(context, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, actionintent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(intent.getIntExtra("Icon", 0))
                .setContentTitle(intent.getStringExtra("Title"))
                .setContentText(intent.getStringExtra("Message"))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(intent.getIntExtra("RequestCode", 0), builder.build());
    }
}

        //createNotification(context, title, message, icon, requestCode);


        //Log.d("","" +noti.getRequestCode());

//        Intent repeating_intent = new Intent(context, Repeating_activity.class);
//        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 10, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Intent intent2 = getIntent(intent);



//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL1")
//                //.setContentIntent()
//                .setSmallIcon(noti.getIcon())
//                .setContentTitle(noti.getTitle())
//                .setContentText(noti.getMessage())
//                .setAutoCancel(true);
//        notificationManager.notify(10, builder.build());



    //String str = noti.getTitle();
//        String title = intent.getStringExtra("Title");
//        String message =intent.getStringExtra("Message");
//        int icon = intent.getIntExtra("Icon", 0);
//        int requestCode = intent.getIntExtra("RequestCode", 0);

//    public void createNotification(Context context, String title, String msgText, int icon, int requestCode)
//    {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_1_ID)
//                .setSmallIcon(icon)
//                .setContentTitle(title)
//                .setContentText(msgText)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(requestCode, builder.build());


        //                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText(msgText))

        //NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Intent i = new Intent(context, MainActivity.class);
        //i.putExtra(DataBaseManager.CN_ID_APLICACION, idAplicacion);

        //PendingIntent notificIntent = PendingIntent.getActivity(context,10, i ,PendingIntent.FLAG_CANCEL_CURRENT);

//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "CHANNEL1")
//                .setContentTitle(title)
//                .setContentText(msgText)
//                .setSmallIcon(R.drawable.ic_blood_drop)
//                .setAutoCancel(true)
//                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
//                .setContentIntent(notificIntent);




