package com.example.attendance

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendance.myDialog.Companion.CLASS_ADD_DIALOG
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var fab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var classAdapter: ClassAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var classItems: ArrayList<ClassItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab = findViewById(R.id.fab_main)
        fab.setOnClickListener { showDialog() }

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        classAdapter = ClassAdapter(this, classItems)
        recyclerView.adapter = classAdapter
        classAdapter.setOnItemClickListener(object : ClassAdapter.OnItemClickListener {
            override fun onClick(position: Int) {
                gotoItemActivity(position)
            }
        })
    }

    private fun gotoItemActivity(position: Int) {
        val intent = Intent(this, StudentActivity::class.java)
        intent.putExtra("CourseName", classItems[position].crsName)
        intent.putExtra("SectionName", classItems[position].secName)
        intent.putExtra("Position", position)
        startActivity(intent)
    }

    private fun showDialog() {
        val dialog = myDialog()
        dialog.setListener(object : myDialog.OnClickListener {
            override fun onClick(text1: String, text2: String) {
                addClass(text1, text2)
            }
        })
        dialog.show(supportFragmentManager, CLASS_ADD_DIALOG)
    }

    private fun addClass(crsName: String, secName: String) {
        classItems.add(ClassItem(crsName, secName))
        classAdapter.notifyDataSetChanged()
    }
}
