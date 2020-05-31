package com.mubassyir.firebasetutorial.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.mubassyir.firebasetutorial.R
import com.mubassyir.firebasetutorial.utils.toast
import kotlinx.android.synthetic.main.activity_forget_password.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.edit_text_email
import java.util.*

class ForgetPasswordActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        button_reset_password.setOnClickListener {
            val email = edit_text_email.text.toString().trim()

            if (email.isEmpty()){
                edit_text_email.error = "Field is required"
                edit_text_email.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                edit_text_email.error = "Please Enter an valid email"
                edit_text_email.requestFocus()
                return@setOnClickListener
            }

            progressbarForget.visibility = View.VISIBLE

            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {task->
                if (task.isSuccessful){
                    this.toast("Password reset has been send to your email")
                } else{
                    this.toast(task.exception?.message!!)
                }
            }
        }
    }
}
