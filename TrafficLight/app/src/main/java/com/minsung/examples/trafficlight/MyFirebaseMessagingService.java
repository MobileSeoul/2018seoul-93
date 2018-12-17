package com.minsung.examples.trafficlight;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by minsung on 2018-09-30.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.i("messagingService", "onMessageReceived");
//        int sec = 0;
//        Map<String, String> data = remoteMessage.getData();
//        //String degree = data.get("degree");
////        if(!TextUtils.isEmpty(data.get("time"))&&TextUtils.isDigitsOnly(data.get("time"))){
//         sec = Integer.parseInt(data.get("time").trim());
////
//        System.out.println("message : "+sec);
//        controlTracfficLight(sec);
    }

    private void controlTracfficLight(int sec) {
        NotificationManager notifManager
                = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(
                    "channel", "channelName", importance);

            notifManager.createNotificationChannel(mChannel);

        }
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), "channel");

        Intent notificationIntent = new Intent(getApplicationContext()
                , MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int requestID = (int) System.currentTimeMillis();

        PendingIntent pendingIntent
                = PendingIntent.getActivity(getApplicationContext()
                , requestID
                , notificationIntent
                , PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentTitle(sec+"초로 연장되었습니다") // required
                .setContentText(sec+"초로 연장되었습니다")  // required
                .setDefaults(Notification.DEFAULT_ALL) // 알림, 사운드 진동 설정
                .setAutoCancel(true) // 알림 터치시 반응 후 삭제
                .setSound(RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(android.R.drawable.btn_star);

        OnTimeChangeListener onTimeChangeListener = (OnTimeChangeListener) this;
        onTimeChangeListener.onChange(sec);
    }

    public interface OnTimeChangeListener {
        void onChange(int time);
    }

}
