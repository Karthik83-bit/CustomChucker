package com.isu.apitracker

import android.app.Application.NOTIFICATION_SERVICE
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService

class NotificationHelper(val context: Context) {
    companion object {
        private const val TRANSACTIONS_CHANNEL_ID = "api_tracker"
        private const val TRANSACTION_NOTIFICATION_ID = 1138
        private const val INTENT_REQUEST_CODE = 11
        private  var mNotificationManager:NotificationManager?= null
    }
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            mNotificationManager?.createNotificationChannelGroup(
                NotificationChannelGroup(TRANSACTIONS_CHANNEL_ID, "music")
            )
            val notificationChannel = NotificationChannel(
                "service_channel", "Service Notification",
                NotificationManager.IMPORTANCE_HIGH
            )

            mNotificationManager?.createNotificationChannel(notificationChannel)
        }
        mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    }

    fun showNotification(content:String){
        val pendingIntentToTrackingActivity= PendingIntent.getActivity(
            context,
            /* requestCode = */ 0,
            Intent(context,ApiTrackingActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT)



        val builder = NotificationCompat.Builder(context, "service_channel")
        builder.setSmallIcon(R.drawable.baseline_circle_notifications_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntentToTrackingActivity)
            .setContentTitle("Recorded API")
            .setContentText(content)
        val notification = builder.build()
        mNotificationManager?.notify(TRANSACTION_NOTIFICATION_ID, notification)




//        try {
//            val pendingIntentToTrackingActivity= PendingIntent.getActivity(
//                context,
//                /* requestCode = */ 0,
//                Intent(context,ApiTrackingActivity::class.java),
//                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT)
//
//            val notification=Notification
//                .Builder(context, TRANSACTIONS_CHANNEL_ID)
//                .setContentIntent(pendingIntentToTrackingActivity)
//                .setContentTitle("Recorded API")
//                .setContentText(content)
//                .setSmallIcon(R.drawable.baseline_circle_notifications_24)
//                .build()
//            notificationManager?.notify(TRANSACTION_NOTIFICATION_ID,notification)
//        }catch (e:Exception){
//            Log.d("notificationerror", "showNotification: ${e.message}")
//        }

    }
}