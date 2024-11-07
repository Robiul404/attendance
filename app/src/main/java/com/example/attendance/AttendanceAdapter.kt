package com.example.attendance

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class AttendanceAdapter(private val attendanceList: List<AttendanceRecord>) :
    RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.attendance_item, parent, false)
        return AttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val attendanceRecord = attendanceList[position]
        holder.rollTextView.text = attendanceRecord.roll
        holder.nameTextView.text = attendanceRecord.name
        holder.attendancePercentageTextView.text = "${attendanceRecord.presentDates.size} days present"
    }

    override fun getItemCount(): Int {
        Log.d("AttendanceAdapter", "RecyclerView item count: ${attendanceList.size}")
        return attendanceList.size
    }

    inner class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rollTextView: TextView = itemView.findViewById(R.id.rollTextView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val attendancePercentageTextView: TextView = itemView.findViewById(R.id.attendancePercentageTextView)
    }
}
