package com.example.attendance

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class myDialog : DialogFragment() {
    companion object {
        const val CLASS_ADD_DIALOG = "addclass"
        const val STUDENT_ADD_DIALOG = "addstudent"
        fun setListener(myDialog: myDialog, listener: OnClickListener) {
            myDialog.listener= listener
        }
    }

    private var listener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(text1: String, text2: String)
    }

    fun setListener(listener: OnClickListener) {
        this.listener = listener
    }

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val view = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog, null)
        builder.setView(view)

        when (tag) {
            CLASS_ADD_DIALOG -> getAddClassDialog(view, builder)
            STUDENT_ADD_DIALOG -> getAddStudentDialog(view, builder)
        }

        return builder.create()
    }

    private fun getAddStudentDialog(view: View, builder: AlertDialog.Builder) {
        val title: TextView = view.findViewById(R.id.titleDialog)
        title.text = "Add New Student"

        val idEdt: EditText = view.findViewById(R.id.edt01)
        val nameEdt: EditText = view.findViewById(R.id.edt02)
        idEdt.setHint("582")
        nameEdt.setHint("Robiul Islam")

        val cancel: Button = view.findViewById(R.id.cancel_btn)
        val add: Button = view.findViewById(R.id.add_btn)

        cancel.setOnClickListener {
            dismiss()
        }

        add.setOnClickListener {
            val roll = idEdt.text.toString()
            val name = nameEdt.text.toString()
            listener?.onClick(roll, name)
            dismiss()
        }
    }

    private fun getAddClassDialog(view: View, builder: AlertDialog.Builder) {
        val title: TextView = view.findViewById(R.id.titleDialog)
        val crsEdt: EditText = view.findViewById(R.id.edt01)
        val secEdt: EditText = view.findViewById(R.id.edt02)

        val cancel: Button = view.findViewById(R.id.cancel_btn)
        val add: Button = view.findViewById(R.id.add_btn)

        cancel.setOnClickListener {
            dismiss()
        }

        add.setOnClickListener {
            val crsName = crsEdt.text.toString()
            val secName = secEdt.text.toString()
            listener?.onClick(crsName, secName)
            dismiss()
        }
    }


}
