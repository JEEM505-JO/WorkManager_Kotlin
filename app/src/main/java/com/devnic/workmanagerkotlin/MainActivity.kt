package com.devnic.workmanagerkotlin

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.*
import com.devnic.workmanagerkotlin.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val result =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it) {
                periodicWork()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* if (!isServiceRunning(this, MyWork::class.java)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this, MyWork::class.java))
            } else {
                startService(Intent(this, MyWork::class.java))
            }*/

        binding.btnOneWork.setOnClickListener {
            permisos()
        }
        binding.btnPeriodicWork.setOnClickListener {
            periodicWork()
        }

    }


    fun permisos() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INSTANT_APP_FOREGROUND_SERVICE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun periodicWork() {
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .build()

        val myWorkReques = PeriodicWorkRequestBuilder<MyWork>(
            30,
            TimeUnit.SECONDS
        ).setConstraints(constraint)
            .addTag("My_ID")
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("My_ID", ExistingPeriodicWorkPolicy.REPLACE, myWorkReques)
    }

    private fun onWork() {
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(true)
            .build()

        val myWorkerRequest: WorkRequest = OneTimeWorkRequest.Builder(MyWork::class.java)
            .setConstraints(constraint)
            .build()
        WorkManager.getInstance(this).enqueue(myWorkerRequest)
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


}