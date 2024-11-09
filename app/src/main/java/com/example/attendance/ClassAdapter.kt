package com.example.attendance

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ClassAdapter(
    private val context: Context,
    private val classItems: List<ClassItem>
) : RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {


    private lateinit var onItemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onClick(position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    class ClassViewHolder(itemView: View, onItemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val crsName: TextView = itemView.findViewById(R.id.crs_name)
        val secName: TextView = itemView.findViewById(R.id.sec_name)

        init {
            itemView.setOnClickListener {
                onItemClickListener.onClick(position) // Use the instance variable
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.claas_item, parent, false)
        return ClassViewHolder(itemView, onItemClickListener)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val classItem = classItems[position]
        holder.crsName.text = classItem.crsName
        holder.secName.text = classItem.secName
    }

    override fun getItemCount(): Int {
        return classItems.size
    }
}