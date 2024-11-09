package com.example.attendance

import StudentItem
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.io.InputStream
import android.net.Uri
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.text.SimpleDateFormat
import java.util.*


class StudentActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var cal: FloatingActionButton
    private var courseName: String = ""
    private var sectionName: String = ""
    private var position: Int = 0
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: studentAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var studentitems: ArrayList<StudentItem> = ArrayList()
    private val REQUEST_CODE_PICK_FILE = 1
    private var selectedDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private val db = FirebaseFirestore.getInstance()
    private lateinit var studentRef: CollectionReference
    private lateinit var attendanceRef: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)

        toolbar = findViewById(R.id.toolbar12)
        setSupportActionBar(toolbar)
        toolbar.setPopupTheme(R.style.CustomMenuStyle)

        courseName = intent.getStringExtra("CourseName").orEmpty()
        sectionName = intent.getStringExtra("SectionName").orEmpty()
        position = intent.getIntExtra("Position", -1)

        val teacherEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
        studentRef = db.collection("Teachers").document(teacherEmail)
            .collection("Classrooms").document("$courseName-$sectionName")
            .collection("Students")

        attendanceRef = db.collection("Teachers").document(teacherEmail)
            .collection("Classrooms").document("$courseName-$sectionName")
            .collection("Attendance")

        setToolbar()
        setupRecyclerView()
        loadStudents()

        cal = findViewById(R.id.cal_btn)
        cal.setOnClickListener { showDatePicker() }
    }


    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerview2)
        recyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = studentAdapter(this, studentitems)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : studentAdapter.OnItemClickListener {
            override fun onClick(position: Int) {
                changeStatus(position)
            }
        })
    }

    private fun loadStudents() {
        studentRef.get().addOnSuccessListener { querySnapshot ->
            studentitems.clear()
            for (document in querySnapshot) {
                val roll = document.getString("roll") ?: ""
                val name = document.getString("name") ?: ""
                val status = document.getString("status") ?: ""
                studentitems.add(StudentItem(roll, name, status))
            }
            adapter.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            Log.w("StudentActivity", "Error loading students", e)
            Toast.makeText(this, "Failed to load students", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveStudents(roll: String, name: String) {
        val student = hashMapOf("roll" to roll, "name" to name, "status" to "P")
        studentRef.document(roll).set(student).addOnSuccessListener {
            Log.d("StudentActivity", "Student saved successfully")
        }.addOnFailureListener { e ->
            Log.w("StudentActivity", "Error saving student", e)
            Toast.makeText(this, "Failed to save student", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            Toast.makeText(this, "Selected date: $selectedDate", Toast.LENGTH_SHORT).show()
        }, year, month, day).show()
    }

    private fun takeAttendance() {
        val attendanceData = studentitems.map {
            mapOf("roll" to it.roll, "name" to it.name, "status" to it.status)
        }
        attendanceRef.document(selectedDate).set(mapOf("attendance" to attendanceData))
            .addOnSuccessListener {
                Toast.makeText(this, "Attendance saved for $selectedDate", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { e ->
                Log.w("StudentActivity", "Error saving attendance", e)
                Toast.makeText(this, "Failed to save attendance", Toast.LENGTH_SHORT).show()
            }
    }

    private fun changeStatus(position: Int) {
        studentitems[position].status = if (studentitems[position].status == "P") "A" else "P"
        adapter.notifyItemChanged(position)
    }

    private fun setToolbar() {
        toolbar = findViewById(R.id.toolbar12)
        val title: TextView = toolbar.findViewById(R.id.title2)
        val subtitle: TextView = toolbar.findViewById(R.id.stitle2)
        val back: ImageButton = toolbar.findViewById(R.id.back)
        val save: ImageButton = toolbar.findViewById(R.id.save)

        title.text = courseName
        subtitle.text = sectionName

        back.setOnClickListener {
            onBackPressed()
        }
        save.setOnClickListener { takeAttendance() } // Save attendance using the selected date

        toolbar.setOnMenuItemClickListener { menuItem -> onMenuItemClick(menuItem) }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.student_menu, menu)
        return true
    }

    private fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.add_student -> {
                showAddStudentDialog()
                return true
            }
            R.id.see_attendance -> {
                val intent = Intent(this, AttendanceActivity::class.java)
                intent.putExtra("SelectedMonth", selectedDate.substring(0, 7))
                startActivity(intent)
                return true
            }
            R.id.upload_excel_btn -> {
                openFilePicker()
                return true
            }
            else -> return false
        }
    }
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        startActivityForResult(intent, REQUEST_CODE_PICK_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                importExcelData(uri)
            }
        }
    }

    private fun importExcelData(fileUri: Uri) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(fileUri)
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0)

            for (row in sheet) {
                if (row.rowNum == 0) continue

                val rollCell = row.getCell(0)
                val nameCell = row.getCell(1)


                val roll = if (rollCell != null && rollCell.cellType == CellType.NUMERIC) {
                    rollCell.numericCellValue.toInt().toString()
                } else {
                    rollCell?.toString()?.trim() ?: continue
                }

                val name = nameCell?.toString()?.trim() ?: continue

                studentitems.add(StudentItem(roll, name, ""))
                saveStudents(roll, name) // Save each student to Firebase
            }

            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Students imported successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to import data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddStudentDialog() {
        val dialog = myDialog()
        dialog.setListener(object : myDialog.OnClickListener {
            override fun onClick(roll: String, name: String) {
                studentitems.add(StudentItem(roll, name, ""))
                adapter.notifyDataSetChanged()
                saveStudents(roll, name)
            }
        })
        dialog.show(supportFragmentManager, myDialog.STUDENT_ADD_DIALOG)
    }
}