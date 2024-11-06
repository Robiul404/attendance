package com.example.attendance

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.attendance.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {
    private lateinit var actBinding: ActivityRegisterBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(VarSave.EMAILADD, FirebaseAuth.getInstance().currentUser?.email)
            startActivity(intent)
            finish()
        }
        actBinding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        actBinding.regButton.setOnClickListener {
            createTeacher()
        }
        actBinding.goLoginButton.setOnClickListener {
            Intent(this@Register, Login::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }

    private fun createTeacher() {
        val tName = actBinding.teacherName.editText?.text.toString()
        val instT = actBinding.institute.editText?.text.toString()
        val email = actBinding.regEmail.editText?.text.toString()
        val pass = actBinding.regPassword.editText?.text.toString()
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Enter Your Email/Password", Toast.LENGTH_SHORT).show()
            return
        }
        fun saveData(tName: String, email: String, instT: String) {
            val faculty = hashMapOf(
                "Teacher Name" to tName,
                "Teacher Email" to email,
                "Institute Name" to instT
            )

            db.collection(email).document(email)
                .set(faculty)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data Saved Successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saving data: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveData(tName, email, instT)
                    Toast.makeText(this, "Account was Created", Toast.LENGTH_SHORT).show()

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(this) { signInTask ->
                            if (signInTask.isSuccessful) {
                                Toast.makeText(this, "User Logged In", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@Register, MainActivity::class.java).apply {
                                    putExtra(VarSave.EMAILADD, email)
                                }
                                startActivity(intent)
                                finish()
                            }
                        }
                } else {
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }

            }

    }
}