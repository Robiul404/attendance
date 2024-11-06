package com.example.attendance

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.attendance.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {
    private lateinit var actBinding: ActivityLoginBinding
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseAuth.getInstance().currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(VarSave.EMAILADD, FirebaseAuth.getInstance().currentUser?.email)
            startActivity(intent)
            finish()
        }
        actBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        actBinding.loginButton.setOnClickListener {
            loginTeacher()
        }
        actBinding.goRegButton.setOnClickListener {
            Intent(this, Register::class.java).also {
                startActivity(it)
                finish()
            }

        }
    }

    private fun loginTeacher() {
        val email = actBinding.logEmail.editText?.text.toString()
        val pass = actBinding.logPassword.editText?.text.toString()
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { signInTask ->
                if (signInTask.isSuccessful) {
                    Toast.makeText(this, "User Logged In", Toast.LENGTH_SHORT).show()
                    Intent(this@Login, MainActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }
                else{
                    Toast.makeText(this, "Your Information is Incorrect", Toast.LENGTH_SHORT).show()
                }
            }

    }
}