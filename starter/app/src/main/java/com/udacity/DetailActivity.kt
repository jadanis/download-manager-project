package com.udacity

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        val status = intent?.getIntExtra("status",0)
        val title = intent?.getStringExtra("title")
        val statusString = when(status){
            8 -> "Success"
            else -> "Fail"
        }
        findViewById<TextView>(R.id.status_value).text = statusString
        findViewById<TextView>(R.id.file_name_value_text).text = title
    }

    private fun onClickNav(view: View){
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }



}
