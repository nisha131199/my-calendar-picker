package com.nisha.mycalendarpicker

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.nisha.mycalendar.CalendarSelectCallback
import com.nisha.mycalendar.MyDialogFragment
import java.text.SimpleDateFormat
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private val button = lazy {
        findViewById<Button>(R.id.btnSelectDate)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.value?.setOnClickListener {
            showCalendarDialog()
        }
    }

    private fun showCalendarDialog() {
        val dialog = MyDialogFragment.newInstance(
            object : CalendarSelectCallback {
                override fun onSelectTime(startDate: Calendar, endDate: Calendar) {
                    val formatter = SimpleDateFormat("dd/MM/yyyy")
                    button.value.text = "start date: ${formatter.format(startDate.time)} \nend date: ${formatter.format(endDate.time)}"
                }
            }
        )
        dialog.show(supportFragmentManager, "MyDialogFragment")
    }
}