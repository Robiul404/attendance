package com.example.attendance

import StudentItem
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class StudentActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: studentAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var studentItems: ArrayList<StudentItem> = ArrayList()
    private lateinit var classroomRef: CollectionReference
    private lateinit var teacherEmail: String
    private var courseName: String = ""
    private var sectionName: String = ""
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)

        // Toolbar setup
        toolbar = findViewById(R.id.toolbar12) ?: throw IllegalStateException("Toolbar not found")
        setSupportActionBar(toolbar)
        toolbar.setPopupTheme(R.style.CustomMenuStyle)

        // Get data from Intent
        courseName = intent.getStringExtra("CourseName").orEmpty()
        sectionName = intent.getStringExtra("SectionName").orEmpty()
        position = intent.getIntExtra("Position", -1)

        // Firebase reference setup
        teacherEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
        classroomRef = FirestoreHelper.getClassroomRef(teacherEmail)
            .document("$courseName-$sectionName")
            .collection("students")

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerview2)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = studentAdapter(this, studentItems)
        recyclerView.adapter = adapter

        // Load students from Firebase
        loadStudents()

        // Set toolbar and item click listeners
        setToolbar()
        adapter.setOnItemClickListener(object : studentAdapter.OnItemClickListener {
            override fun onClick(position: Int) {
                changeStatus(position)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.student_menu, menu)
        return true
    }

    private fun changeStatus(position: Int) {
        // Toggle attendance status between "P" and "A"
        studentItems[position].status = if (studentItems[position].status == "P") "A" else "P"
        adapter.notifyItemChanged(position)

        // Update attendance status in Firebase
        classroomRef.document(studentItems[position].roll)
            .update("status", studentItems[position].status)
    }

    private fun setToolbar() {
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
        if (menuItem?.itemId == R.id.add_student) {
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
        val newStudent = StudentItem(roll, name, "P")  // Default status as Present ("P")
        studentItems.add(newStudent)
        adapter.notifyDataSetChanged()

        // Save the new student to Firebase
        classroomRef.document(roll).set(newStudent.toMap())
    }

    private fun loadStudents() {
        // Retrieve student data from Firebase
        classroomRef.get().addOnSuccessListener { querySnapshot ->
            studentItems.clear()
            for (document in querySnapshot) {
                val roll = document.getString("roll") ?: ""
                val name = document.getString("name") ?: ""
                val status = document.getString("status") ?: "P"
                studentItems.add(StudentItem(roll, name, status))
            }
            adapter.notifyDataSetChanged()
        }
    }
}
