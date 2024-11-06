package com.example.attendance

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendance.myDialog.Companion.CLASS_ADD_DIALOG
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var fab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var classAdapter: ClassAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var logoutBtn: ImageButton
    private val db = FirebaseFirestore.getInstance()
    private lateinit var teacherEmail: String
    private var classItems: ArrayList<ClassItem> = ArrayList()
    private lateinit var classroomRef: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab = findViewById(R.id.fab_main)
        fab.setOnClickListener { showDialog() }

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        // Get teacher email for Firestore reference
        teacherEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
        classroomRef = FirestoreHelper.getClassroomRef(teacherEmail)

        // Initialize RecyclerView and adapter
        classAdapter = ClassAdapter(this, classItems)
        recyclerView.adapter = classAdapter
        classAdapter.setOnItemClickListener(object : ClassAdapter.OnItemClickListener {
            override fun onClick(position: Int) {
                gotoItemActivity(position)
            }
        })

        logoutBtn = findViewById(R.id.logout)
        logoutBtn.setOnClickListener {
            showLogout()
        }

        // Load classrooms from Firebase
        loadClassrooms()
    }

    private fun loadClassrooms() {
        // Fetch classrooms for the current teacher from Firestore
        classroomRef.get().addOnSuccessListener { querySnapshot ->
            classItems.clear()  // Clear the list before adding items to avoid duplicates
            for (document in querySnapshot) {
                val crsName = document.getString("Course Name") ?: "Unknown Course"
                val secName = document.getString("Section Name") ?: "Unknown Section"
                classItems.add(ClassItem(crsName, secName))
            }
            classAdapter.notifyDataSetChanged() // Update the RecyclerView with new data
        }.addOnFailureListener { exception ->
            Log.w("MainActivity", "Error getting documents: ", exception)
            Toast.makeText(this, "Failed to load classrooms", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogout() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Logout")
        builder.setMessage("Do you want to logout?")
        builder.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
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
        // Add new class to local list and notify adapter
        val newClassItem = ClassItem(crsName, secName)
        classItems.add(newClassItem)
        classAdapter.notifyDataSetChanged()

        // Save the new class to Firestore
        val newClassroom = hashMapOf("Course Name" to crsName, "Section Name" to secName)
        val classroomId = "$crsName-$secName"
        classroomRef.document(classroomId).set(newClassroom).addOnSuccessListener {
            Log.d("Saved", "Classroom created with ID: $classroomId")
        }.addOnFailureListener { e ->
            Log.w("MainActivity", "Error adding classroom", e)
            Toast.makeText(this, "Failed to add classroom", Toast.LENGTH_SHORT).show()
        }
    }
}
