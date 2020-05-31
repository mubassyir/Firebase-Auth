package com.mubassyir.firebasetutorial.ui.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

import com.mubassyir.firebasetutorial.R
import com.mubassyir.firebasetutorial.utils.toast
import kotlinx.android.synthetic.main.fragment_update_password.*

/**
 * A simple [Fragment] subclass.
 */
class UpdatePasswordFragment : Fragment() {

    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_update_password, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        layoutPassword.visibility = View.VISIBLE
        layoutUpdatePassword.visibility = View.INVISIBLE

        button_authenticate.setOnClickListener {
            val password = edit_text_password.text.toString().trim()

            if (password.isEmpty()) {
                edit_text_password.error = "Field is required"
                edit_text_password.requestFocus()
                return@setOnClickListener
            }
            progressbar.visibility = View.VISIBLE
            currentUser?.let { user ->
                val credential = EmailAuthProvider.getCredential(user.email!!, password)
                user.reauthenticate(credential).addOnCompleteListener {
                    progressbar.visibility = View.INVISIBLE
                    when {
                        it.isSuccessful -> {
                            layoutPassword.visibility = View.INVISIBLE
                            layoutUpdatePassword.visibility = View.VISIBLE
                        }
                        it.exception is FirebaseAuthInvalidCredentialsException -> {
                            context?.toast("Invalid Password")
                        }
                        else -> {
                            context?.toast(it.exception?.message!!)
                        }
                    }
                }
            }
        }

        button_update.setOnClickListener{view->
            val password = edit_text_new_password.text.toString().trim()
            if (password.isEmpty() || password.length < 5){
                edit_text_new_password.error = "min 6 chareacters"
                edit_text_new_password.requestFocus()
                return@setOnClickListener
            }
            if (password!=edit_text_new_password_confirm.text.toString()){
                edit_text_new_password_confirm.error = "Password not match"
                edit_text_new_password_confirm.requestFocus()
                return@setOnClickListener
            }

            progressbar.visibility = View.VISIBLE
            currentUser?.let{ user->
                user.updatePassword(password).addOnCompleteListener {task->
                    progressbar.visibility = View.INVISIBLE
                    if (task.isSuccessful){
                        context?.toast("Password updated")
                        val action = UpdatePasswordFragmentDirections.actionPasswordUpdated()
                        Navigation.findNavController(view).navigate(action)

                    } else {
                        context?.toast(task.exception?.message!!)
                    }
                }
            }
        }
    }
}
