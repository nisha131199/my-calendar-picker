package com.nisha.mycalendar

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.ongraph.mycalendar.R
import java.util.*

class MonthAdapter(
    private val months: List<Calendar>
) : RecyclerView.Adapter<MonthAdapter.MonthViewHolder>() {

    private var startDate: Calendar? = null
    private var endDate: Calendar? = null

    fun updateDates(startDate: Calendar, endDate: Calendar) {
        this.startDate = startDate
        this.endDate = endDate
        this.notifyDataSetChanged()
    }

    inner class MonthViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val monthTitle: TextView = itemView.findViewById(R.id.month_title)
        private val calendarGrid: GridLayout = itemView.findViewById(R.id.calendar_grid)

        fun bind(month: Calendar) {
            monthTitle.text = "${month.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())} ${month.get(Calendar.YEAR)}"
            setupCalendarGrid(month)
        }

        private fun setupCalendarGrid(month: Calendar) {
            calendarGrid.removeAllViews() // Clear previous views
            month.set(Calendar.DAY_OF_MONTH, 1)

            val daysInMonth = month.getActualMaximum(Calendar.DAY_OF_MONTH)
            val firstDayOfWeek = month.get(Calendar.DAY_OF_WEEK)

            // Calculate how many empty spaces before the first day of the month
            val shift = (firstDayOfWeek + 5) % 7 // Adjust for Monday start

            // Add empty views for days before the first day of the month
            for (i in 0 until shift) {
                val emptyView = LayoutInflater.from(itemView.context).inflate(R.layout.calendar_day, calendarGrid, false)

                // Set the row and column position of the item
                val layoutParams = GridLayout.LayoutParams(
                    GridLayout.spec(0, GridLayout.FILL, 1f),
                    GridLayout.spec(i, GridLayout.FILL, 1f)
                ).apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                }

                emptyView.layoutParams = layoutParams

                emptyView.findViewById<TextView>(R.id.day_number).text = ""
                calendarGrid.addView(emptyView)
            }

            var col = shift
            var row = 0
            // Add the current month's days
            for (day in 1..daysInMonth) {
                val dayView = LayoutInflater.from(itemView.context).inflate(R.layout.calendar_day, calendarGrid, false)

                if (col > 6) {
                    col = 0
                    ++row
                }

                // Set the row and column position of the item
                val layoutParams = GridLayout.LayoutParams(
                    GridLayout.spec(row, GridLayout.FILL, 1f),
                    GridLayout.spec(col, GridLayout.FILL, 1f)
                ).apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                }
                col++

                dayView.layoutParams = layoutParams

                // Create a Calendar instance for the current day
                val currentDay = Calendar.getInstance().apply {
                    set(month.get(Calendar.YEAR), month.get(Calendar.MONTH), day)
                }

                // Highlight days within the date range
                if (isDateAtStart(currentDay, startDate)) {
                    // adjacent dates
                    dayView.findViewById<TextView>(R.id.day_number).background =
                        ResourcesCompat.getDrawable(dayView.rootView.context.resources, R.drawable.circle_app_theme_button, null)
                    dayView.findViewById<TextView>(R.id.day_number).setTextColor(Color.WHITE)
                    dayView.findViewById<View>(R.id.vEnd).setBackgroundColor(Color.parseColor("#979797"))
                } else if (isDateAtEnd(currentDay, endDate)) {
                    // adjacent dates
                    dayView.findViewById<TextView>(R.id.day_number).background =
                        ResourcesCompat.getDrawable(dayView.rootView.context.resources, R.drawable.circle_app_theme_button, null)
                    dayView.findViewById<TextView>(R.id.day_number).setTextColor(Color.WHITE)
                    dayView.findViewById<View>(R.id.vStart).setBackgroundColor(Color.parseColor("#979797"))
                } else if (isDateToday(currentDay)) {
                    // today
                    dayView.findViewById<TextView>(R.id.day_number).background =
                        ResourcesCompat.getDrawable(dayView.rootView.context.resources, R.drawable.circle_app_theme_border, null)
                    dayView.findViewById<TextView>(R.id.day_number).setTextColor(Color.parseColor("#DEC477"))
                } else if (isDateInRange(currentDay, startDate, endDate)) {
                    // date range
                    dayView.setBackgroundColor(Color.parseColor("#979797"))
                    dayView.findViewById<TextView>(R.id.day_number).setTextColor(Color.WHITE)
                } else {
                    dayView.setBackgroundColor(Color.WHITE)
                    dayView.findViewById<TextView>(R.id.day_number).setTextColor(Color.parseColor("#979797"))
                }

                dayView.findViewById<TextView>(R.id.day_number).text = String.format("%02d", day)
                calendarGrid.addView(dayView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.month_view, parent, false)
        return MonthViewHolder(view)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        holder.bind(months[position])
    }

    override fun getItemCount(): Int {
        return months.size
    }

    private fun isDateInRange(currentDay: Calendar, startDate: Calendar?, endDate: Calendar?): Boolean {
        return (startDate != null && endDate != null) &&
                (currentDay.after(startDate) && currentDay.before(endDate))
    }

    private fun isDateAtStart(currentDay: Calendar, startDate: Calendar?): Boolean {
        return (startDate != null) &&
                (currentDay.get(Calendar.YEAR) == startDate.get(Calendar.YEAR) && currentDay.get(Calendar.DAY_OF_MONTH) == startDate.get(Calendar.DAY_OF_MONTH) && currentDay.get(Calendar.MONTH) == startDate.get(Calendar.MONTH))
    }

    private fun isDateAtEnd(currentDay: Calendar, endDate: Calendar?): Boolean {
        return (endDate != null) &&
                (currentDay.get(Calendar.YEAR) == endDate.get(Calendar.YEAR) && currentDay.get(Calendar.DAY_OF_MONTH) == endDate.get(Calendar.DAY_OF_MONTH) && currentDay.get(Calendar.MONTH) == endDate.get(Calendar.MONTH))
    }

    private fun isDateToday(currentDay: Calendar): Boolean {
        val today = Calendar.getInstance()
        return (
                currentDay.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                        currentDay.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH) &&
                        currentDay.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                )
    }
}
