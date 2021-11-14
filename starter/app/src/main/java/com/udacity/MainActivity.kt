package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var downloadUrl: String
    private lateinit var downloadName: String
    private var radioChecked: Boolean = false

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        createChannel(getString(R.string.download_finish_channel_id),getString(R.string.channel_name))

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            download()
            //custom_button.buttonState = ButtonState.Loading
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            //As explained in mentor question:
            //https://knowledge.udacity.com/questions/590022
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val query = DownloadManager.Query()
                .setFilterById(id!!)
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val cursor = downloadManager.query(query)
            if(cursor.moveToFirst()){
                val status = cursor.getInt(
                    cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                )
                if (context != null) {
                    notificationManager = ContextCompat.getSystemService(
                        context,
                        NotificationManager::class.java
                    ) as NotificationManager
                    notificationManager.sendNotification(context.getString(R.string.notification_description),context,status,downloadName)
                }
            }

            //Toast.makeText(context,"Download finished!",Toast.LENGTH_LONG).show()

        }
    }

    private fun download() {
        if(radioChecked) {
            val request =
                DownloadManager.Request(Uri.parse(downloadUrl))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        } else {
            val message = getString(R.string.selection_required)
            Toast.makeText(this,message,Toast.LENGTH_LONG).show()
        }

    }

    fun onRadioButtonClicked(view: View){
        if(view is RadioButton){
            radioChecked = view.isChecked
            if(radioChecked){
                when(view.getId()){
                    R.id.glid_radio -> { downloadUrl = GLIDE_URL; downloadName = getString(R.string.glide_radio_text) }
                    R.id.loadapp_radio -> { downloadUrl = UDACITY_URL; downloadName = getString(R.string.loadapp_radio_text)}
                    R.id.retrofit_radio -> { downloadUrl = RETROFIT_URL; downloadName = getString(R.string.retrofit_radio_text) }
                    else -> { downloadUrl = ""; downloadName = ""}
                }
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.enableVibration(true)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.description = getString(R.string.channel_description)

            notificationManager = ContextCompat.getSystemService(
                this,
                NotificationManager::class.java
            ) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    companion object {
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
