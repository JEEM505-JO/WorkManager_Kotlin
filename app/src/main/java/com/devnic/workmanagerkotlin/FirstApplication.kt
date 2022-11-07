package com.devnic.workmanagerkotlin

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.*
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)

class FirstApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        periodicWork()
    }


    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services = activityManager.getRunningServices(Integer.MAX_VALUE)

        if (services != null) {
            for (i in services.indices) {
                if (serviceClass.name == services[i].service.className && services[i].pid != 0) {
                    return true
                }
            }
        }
        return false
    }

    private fun periodicWork() {
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .build()

        val myWorkReques = PeriodicWorkRequestBuilder<MyWork>(
            15,
            TimeUnit.MINUTES
        ).setConstraints(constraint)
            .addTag("My_ID")
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork("My_ID", ExistingPeriodicWorkPolicy.REPLACE, myWorkReques)
    }
}