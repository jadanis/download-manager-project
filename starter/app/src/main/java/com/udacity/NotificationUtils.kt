package com.udacity

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context,status: Int,title: String){

    val contentIntent = Intent(applicationContext,DetailActivity::class.java)
        .putExtra("status",status)
        .putExtra("title",title)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )


    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.download_finish_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext
            .getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setStyle(NotificationCompat.BigTextStyle())
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.check_status),
            contentPendingIntent
        )

    notify(NOTIFICATION_ID,builder.build())

}