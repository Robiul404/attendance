package com.example.attendance

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StudentActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar

    private var courseName: String = ""
    private var sectionName: String = ""
    private var position: Int = 0
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: studentAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var studentitems: ArrayList<StudentItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)


        // Initialize the toolbar
        toolbar = findViewById(R.id.toolbar12) ?: throw IllegalStateException("Toolbar not found")

        setSupportActionBar(toolbar)
        toolbar.setPopupTheme(R.style.CustomMenuStyle)

        // Get data from Intent
        courseName = intent.getStringExtra("CourseName").orEmpty()
        sectionName = intent.getStringExtra("SectionName").orEmpty()
        position = intent.getIntExtra("Position", -1)

        setToolbar()
        recyclerView = findViewById(R.id.recyclerview2)
        recyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = studentAdapter(this, studentitems)
        adapter.setOnItemClickListener(object : studentAdapter.OnItemClickListener {
            override fun onClick(position: Int) {
                changeStatus(position) // Handle click
            }
        })

        recyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : studentAdapter.OnItemClickListener {
            override fun onClick(position: Int) {
                changeStatus(position)
            }
        })
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.student_menu, menu)
        return true // Ensures the menu is displayed
    }


    private fun changeStatus(position: Int) {
        studentitems[position].status=if (studentitems[position].status == "P") "A"
        else "P"

        adapter.notifyItemChanged(position) // Notify the adapter to refresh the specific item
    }


    private fun setToolbar() {
        toolbar = findViewById(R.id.toolbar12)
        val title: TextView = toolbar.findViewById(R.id.title2)
        val subtitle: TextView = toolbar.findViewById(R.id.stitle2)
        val back: ImageButton = toolbar.findViewById(R.id.back)

        title.text = courseName
        subtitle.text = sectionName

        back.setOnClickListener {
            onBackPressed()
        }

        toolbar.setOnMenuItemClickListener { menuItem -> onMenuItemClick(menuItem) }


    }

    private fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        if (menuItem?.itemId== R.id.add_student) {
            showAddStudentDialog()
        }
        return false
    }
    private fun showAddStudentDialog() {
        val dialog = myDialog()
        dialog.setListener(object : myDialog.OnClickListener {
            override fun onClick(text1: String, text2: String) {
                addStudent(text1, text2)
            }
        })
        dialog.show(supportFragmentManager, myDialog.STUDENT_ADD_DIALOG)
    }


    private fun addStudent(roll: String, name: String) {
        studentitems.add(StudentItem(roll, name,""))
        adapter.notifyDataSetChanged() // Notify that a new item was inserted
    }






}
