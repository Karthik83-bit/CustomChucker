//package com.isu.apitracker
//
//import android.app.Application
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.os.Build
//import androidx.annotation.RequiresApi
//
//
//
//class ApiTrackerApplication:Application() {
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onCreate() {
//
//        super.onCreate()
//        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        val notificationChannel = NotificationChannel(
//            "api_tracker",
//            "ApiTracker",
//            NotificationManager.IMPORTANCE_HIGH
//        )
//        notificationManager.createNotificationChannel(notificationChannel)
//    }
//}