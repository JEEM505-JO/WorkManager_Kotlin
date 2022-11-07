package com.devnic.workmanagerkotlin

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

@RequiresApi(Build.VERSION_CODES.O)
class MyWork(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    companion object {
        const val CHANEL_ID = "channel_id"
        const val NOTIFICATION = 1
    }

    override suspend fun doWork(): Result {
        return try {
            try {
                Log.d("MyWorker", "Run Work Manager")
//                setForeground(getForegroundInfo())
                notification()
                return Result.success()
            } catch (e: Exception) {
                Log.d("Error-MyWorker", "Exception in doWork ${e.message}")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.d("Exepcion doWork", e.message.toString())
            Result.failure()
        }
    }

/*  override suspend fun getForegroundInfo(): ForegroundInfo {
        val title = "Notification Worker"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelNotification()
        }

        val notification = Notification.Builder(applicationContext, CHANEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText("Notification Description")
            .build()
        return ForegroundInfo(
            NOTIFICATION, notification
        )
    }*/


    fun createChannelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chanelname = "Channel Name"
            val chaneldescrip = "Channel Descripcion"
            val channelimport = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(CHANEL_ID, chanelname, channelimport).apply {
                description = chaneldescrip
            }
            val notificationmanager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationmanager.createNotificationChannel(channel)
        }
    }

    fun generateNotification(): Notification {
        val channelid = CHANEL_ID
        val notification = Notification.Builder(applicationContext, channelid)
            .setContentTitle("Notification Worker")
            .setContentText("1. Crear Notificaciones")
            .setSubText("Desarrollo")
            .setPriority(Notification.PRIORITY_DEFAULT)
            .build()

        return notification
    }


    fun notification() {
        val pendingIntent: PendingIntent?
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannelNotification()
        }


        val notification = Notification.Builder(applicationContext, CHANEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Notificacion WorkManager")
            .setContentText("Inicio de trabajo")
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)


        with(NotificationManagerCompat.from(applicationContext)) {
            notify(NOTIFICATION, notification.build())
        }
    }
}
