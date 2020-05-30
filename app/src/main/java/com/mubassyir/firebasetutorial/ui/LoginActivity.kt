package com.mubassyir.firebasetutorial.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.mubassyir.firebasetutorial.R
import com.mubassyir.firebasetutorial.utils.login
import com.mubassyir.firebasetutorial.utils.snackbar
import com.mubassyir.firebasetutorial.utils.toast
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.edit_text_password

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth= FirebaseAuth.getInstance()

        button_sign_in.setOnClickListener{
            val email = edit_text_email.text.toString().trim()
            val password = edit_text_password.text.toString().trim()

            if (email.isEmpty()){
                edit_text_email.also {
                    it.error = "Email is required"
                    it.requestFocus()
                }
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                edit_text_email.also{
                    it.error = "Please Enter an valid email"
                    it.requestFocus()
                }
                return@setOnClickListener
            }
            if (password.isEmpty() || password.length < 5){
                edit_text_password.also {
                    it.error = "Pass min 6 characters"
                    it.requestFocus()
                }
                return@setOnClickListener
            }
            loginUser(email,password)
        }

        text_view_register.setOnClickListener{
            Intent(this, RegisterActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        progressbar.visibility = View.VISIBLE
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){ task->
            progressbar.visibility = View.GONE
            if (task.isSuccessful){
                login()
                finish()
            }else{
                task.exception?.message?.let {
                    base_login_layout.snackbar(it)
                    toast(it)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (mAuth.currentUser!=null){
            login()
            finish()
        }
    }

}
