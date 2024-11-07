package com.example.attendance

import StudentItem
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class AttendanceActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AttendanceAdapter
    private var studentList: List<StudentItem> = mutableListOf()
    private var attendanceList: MutableList<AttendanceRecord> = mutableListOf()
    private lateinit var studentRef: CollectionReference
    private lateinit var attendanceRef: CollectionReference
    private var totalAttendanceDays: Int = 0 // Total number of days for the class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        val teacherEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        val courseName = intent.getStringExtra("CourseName").orEmpty()
        val sectionName = intent.getStringExtra("SectionName").orEmpty()

        // Firebase references
        studentRef = FirebaseFirestore.getInstance().collection("Teachers")
            .document(teacherEmail)
            .collection("Classrooms")
            .document("$courseName-$sectionName")
            .collection("Students")

        attendanceRef = FirebaseFirestore.getInstance().collection("Teachers")
            .document(teacherEmail)
            .collection("Classrooms")
            .document("$courseName-$sectionName")
            .collection("Attendance")

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerViewAttendance)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadStudents() // Load student list from Firebase

    }

    private fun loadStudents() {
        studentRef.get().addOnSuccessListener { querySnapshot ->
            if (querySnapshot.isEmpty) {
                Log.d("AttendanceActivity", "No students found")
            } else {
                Log.d("AttendanceActivity", "Loaded students: ${querySnapshot.size()} students.")
                studentList = querySnapshot.documents.map { document ->
                    StudentItem(
                        document.getString("roll") ?: "Unknown Roll",
                        document.getString("name") ?: "Unknown Name",
                        "A" // Default status
                    )
                }
            }
            // Once students are loaded, load attendance data
            loadAttendanceData()
        }.addOnFailureListener { e ->
            Log.e("AttendanceActivity", "Error loading students", e)
            Toast.makeText(this, "Failed to load students", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadAttendanceData() {
        attendanceRef.get().addOnSuccessListener { querySnapshot ->
            if (querySnapshot.isEmpty) {
                Log.d("AttendanceActivity", "No attendance records found")
            } else {
                Log.d("AttendanceActivity", "Found ${querySnapshot.size()} attendance records")
            }

            totalAttendanceDays = querySnapshot.size()
            Log.d("AttendanceActivity", "Total attendance days: $totalAttendanceDays")

            attendanceList.clear() // Clear previous data

            // Iterate over each student and calculate their attendance percentage
            for (student in studentList) {
                val presentDates = mutableListOf<String>()

                // Check each attendance document
                for (attendanceDoc in querySnapshot.documents) {
                    val attendanceData = attendanceDoc.get("attendance") as? List<Map<String, String>>
                    if (attendanceData != null) {
                        for (record in attendanceData) {
                            if (record["roll"] == student.roll && record["status"] == "P") {
                                presentDates.add(attendanceDoc.id)  // Assuming the document ID is the date
                                break
                            }
                        }
                    }
                }

                val attendancePercentage = if (totalAttendanceDays > 0) {
                    (presentDates.size.toDouble() / totalAttendanceDays.toDouble()) * 100
                } else {
                    0.0
                }

                Log.d("AttendanceActivity", "Student: ${student.name}, Present: ${presentDates.size} days, Attendance: $attendancePercentage%")
                attendanceList.add(AttendanceRecord(student.roll, student.name, presentDates))
            }

            // Notify the adapter that the data has changed
            Log.d("AttendanceActivity", "Attendance List Size after update: ${attendanceList.size}")

            // After attendanceList is populated, set the adapter if it's not already set
            if (this::adapter.isInitialized) {
                adapter.notifyDataSetChanged()
            } else {
                // If the adapter is not yet initialized, initialize it and set it to RecyclerView
                adapter = AttendanceAdapter(attendanceList)
                recyclerView.adapter = adapter
            }

        }.addOnFailureListener { e ->
            Log.e("AttendanceActivity", "Error loading attendance data", e)
            Toast.makeText(this, "Failed to load attendance data", Toast.LENGTH_SHORT).show()
        }
    }

}

