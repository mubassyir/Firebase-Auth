package com.mubassyir.firebasetutorial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance()

        text_view_login.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }

        button_register.setOnClickListener {
            val email = edit_text_email.text.toString().trim()
            val password = edit_text_password.text.toString().trim()

            if (email.isEmpty()) {
                edit_text_email.also {
                    it.error = "Email is required"
                    it.requestFocus()
                }
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edit_text_email.also {
                    it.error = "Please Enter an valid email"
                    it.requestFocus()
                }
                return@setOnClickListener
            }
            if (password.isEmpty() || password.length < 5) {
                edit_text_password.also {
                    it.error = "Pass min 6 characters"
                    it.requestFocus()
                }
                return@setOnClickListener
            }
            registerUser(email, password)
        }
    }

    private fun registerUser(email: String, password: String) {
        progressbar.visibility = View.VISIBLE
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            progressbar.visibility = View.GONE
            if (task.isSuccessful) {
                login()
                finish()
            } else {
                task.exception?.message?.let {
                    loginLayout.snackbar(it)
                }
            }
        }
    }
}
