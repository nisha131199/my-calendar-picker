package com.nisha.mycalendar

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ongraph.mycalendar.R
import java.text.SimpleDateFormat
import java.util.Calendar

class MyDialogFragment : DialogFragment() {

    companion object {
        lateinit var listener: CalendarSelectCallback

        fun newInstance(listener: CalendarSelectCallback): MyDialogFragment {
            this.listener = listener
            return MyDialogFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this dialog fragment
        return inflater.inflate(R.layout.calendar_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val YEAR = 1
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, -23)
        var _startDate = cal
        var _endDate = Calendar.getInstance()

        // Prepare the list of months (past 1 year, current, and next 1 year)
        val calendarList = mutableListOf<Calendar>()
        val currentMonth = Calendar.getInstance()

        for (i in -(12*YEAR)..0) {
            val month = Calendar.getInstance()
            month.time = currentMonth.time
            month.add(Calendar.MONTH, i)
            calendarList.add(month)
        }

        val monthAdapter = MonthAdapter(calendarList)
        monthAdapter.updateDates(_startDate, _endDate)

        val formatter = SimpleDateFormat("dd MMM YYYY")
        view.findViewById<TextView>(R.id.tvStartDate).text = formatter.format(_startDate.time)
        view.findViewById<TextView>(R.id.tvEndDate).text = formatter.format(_endDate.time)

        view.findViewById<TextView>(R.id.tvStartDate).setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(view.rootView.context, R.style.SpinnerDatePickerDialog, { _, year, month, dayOfMonth ->
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                _startDate = calendar
                view.findViewById<TextView>(R.id.tvStartDate).text = "$dayOfMonth/${month + 1}/$year"
                if (_startDate.after(_endDate)) {
                    _endDate = null
                    view.findViewById<TextView>(R.id.tvEndDate).text = "end date"
                } else {
                    monthAdapter.updateDates(_startDate, _endDate)
                }
            },
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            ).apply {
                val calMin = Calendar.getInstance().apply {
                    add(Calendar.YEAR, -(1*YEAR))
                }
                val calMax = Calendar.getInstance()
                datePicker.minDate = calMin.time.time
                datePicker.maxDate = calMax.time.time
            }.show()
        }
        view.findViewById<TextView>(R.id.tvEndDate).setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(view.rootView.context, R.style.SpinnerDatePickerDialog, { _, year, month, dayOfMonth ->
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                _endDate = calendar
                view.findViewById<TextView>(R.id.tvEndDate).text = "$dayOfMonth/${month + 1}/$year"
                if (_endDate.before(_startDate)) {
                    _startDate = null
                    view.findViewById<TextView>(R.id.tvStartDate).text = "start date"
                } else {
                    monthAdapter.updateDates(_startDate, _endDate)
                }
            },
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            ).apply {
                val calMin = Calendar.getInstance().apply {
                    add(Calendar.YEAR, -(1*YEAR))
                }
                val calMax = Calendar.getInstance()
                datePicker.minDate = calMin.time.time
                datePicker.maxDate = calMax.time.time
            }.show()
        }

        val calendarRecyclerView = view.findViewById<RecyclerView>(R.id.calendarRecyclerView)

        calendarRecyclerView.adapter = monthAdapter
        calendarRecyclerView.layoutManager = LinearLayoutManager(view.rootView.context)

        view.findViewById<LinearLayoutCompat>(R.id.llConfirm).setOnClickListener {
            if (_startDate != null && _endDate != null && (_startDate.before(_endDate) || _startDate == _endDate) && (_endDate.after(_startDate) || _startDate == _endDate)) {
                listener.onSelectTime(_startDate, _endDate)
                dismiss()
            } else {
                Toast.makeText(view.rootView.context, "select the time", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }
}
