package com.mubassyir.firebasetutorial.ui.fragments


import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.mubassyir.firebasetutorial.R
import com.mubassyir.firebasetutorial.utils.toast
import kotlinx.android.synthetic.main.fragment_update_email.*

class UpdateEmailFragment : Fragment() {

    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_email, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        layoutPassword.visibility = View.VISIBLE
        layoutUpdateEmail.visibility = View.INVISIBLE

        button_authenticate.setOnClickListener {
            val password = edit_text_password.text.toString().trim()

            if (password.isEmpty()) {
                edit_text_email.error = "Field is required"
                edit_text_email.requestFocus()
                return@setOnClickListener
            }
            progressbarPass.visibility = View.VISIBLE
            currentUser?.let { user ->
                val credential = EmailAuthProvider.getCredential(user.email!!, password)
                user.reauthenticate(credential).addOnCompleteListener {
                    progressbarPass.visibility = View.INVISIBLE
                    when {
                        it.isSuccessful -> {
                            layoutPassword.visibility = View.INVISIBLE
                            layoutUpdateEmail.visibility = View.VISIBLE
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

        button_update.setOnClickListener { view ->
            progressbarPass.visibility = View.VISIBLE

            val email = edit_text_email.text.toString().trim()
            if (email.isEmpty()) {
                edit_text_email.error = "Email Is required"
                edit_text_email.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edit_text_email.error = "Please Enter an valid email"
                edit_text_email.requestFocus()
                return@setOnClickListener
            }
            currentUser?.let { user ->
                user.updateEmail(email).addOnCompleteListener { task ->
                    progressbarPass.visibility = View.INVISIBLE
                    if (task.isSuccessful) {
                        val action = UpdateEmailFragmentDirections.actionEmailVerified()
                        Navigation.findNavController(view).navigate(action)
                    } else {
                        context?.toast(task.exception?.message!!)
                    }
                }
            }
        }
    }
}
