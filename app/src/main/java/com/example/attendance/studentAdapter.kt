package com.example.attendance

import StudentItem
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
class studentAdapter(
    private val context: Context,
    private val studentItems: List<StudentItem>
) : RecyclerView.Adapter<studentAdapter.StudentViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null // Make this nullable

    interface OnItemClickListener {
        fun onClick(position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var cardview: CardView
        val roll: TextView = itemView.findViewById(R.id.cid)
        val name: TextView = itemView.findViewById(R.id.cname)
        val status: TextView = itemView.findViewById(R.id.present)

        fun bind(studentItem: StudentItem, listener: OnItemClickListener?) {
            roll.text = studentItem.roll
            name.text = studentItem.name
            status.text = studentItem.status
            cardview= itemView.findViewById(R.id.card)
            // Set click listener
            itemView.setOnClickListener {
                listener?.onClick(adapterPosition) // Use adapterPosition for the current item
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.student_item, parent, false)
        return StudentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val studentItem = studentItems[position]
        holder.bind(studentItem, onItemClickListener)
        holder.cardview.setCardBackgroundColor(getColor(position)) // Should correctly change color
    }


    private fun getColor(position: Int): Int {
        val status: String = studentItems[position].status
        return when (status) {
            "P" -> ContextCompat.getColor(context, R.color.present)
            "A" -> ContextCompat.getColor(context, R.color.absent)
            else -> ContextCompat.getColor(context, R.color.white)
        }
    }



    override fun getItemCount(): Int {
        return studentItems.size
    }
}
