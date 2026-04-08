package com.isu.apitracker.util

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.isu.apitracker.R
import com.isu.apitracker.presentation.ApiTrackingActivity

class NotificationHelper(val context: Context) {
    companion object {
        private const val TRANSACTIONS_CHANNEL_ID = "api_tracker"
        private const val TRANSACTION_NOTIFICATION_ID = 1138
        private const val INTENT_REQUEST_CODE = 11
        const val EXTRA_RESPONSE_FILE_PATH = "com.isu.apitracker.extra.RESPONSE_FILE_PATH"
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

    fun showNotification(content:String, responseFilePath: String? = null){
        val pendingIntentToTrackingActivity= PendingIntent.getActivity(
            context,
            INTENT_REQUEST_CODE,
            Intent(context, ApiTrackingActivity::class.java).apply {
                responseFilePath?.let {
                    putExtra(EXTRA_RESPONSE_FILE_PATH, it)
                }
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)



        val builder = NotificationCompat.Builder(context, "service_channel")
        builder.setSmallIcon(R.drawable.baseline_circle_notifications_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntentToTrackingActivity)
            .setContentTitle("Recorded API")
            .setContentText(content)
            .setAutoCancel(true)
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
