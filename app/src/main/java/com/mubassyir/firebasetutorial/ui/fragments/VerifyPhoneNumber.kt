package com.mubassyir.firebasetutorial.ui.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

import com.mubassyir.firebasetutorial.R
import com.mubassyir.firebasetutorial.utils.toast
import kotlinx.android.synthetic.main.fragment_verify_phone_number.*
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 */
class VerifyPhoneNumber : Fragment() {

    private var verificationId:String?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_verify_phone_number, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        layoutPhone.visibility = View.VISIBLE
        layoutVerification.visibility = View.INVISIBLE

        button_send_verification.setOnClickListener{
            val number = edit_text_phone.text.toString().trim()

            if (number.isEmpty() || number.length < 10){
                edit_text_phone.error = "Phone enter an valid phone number"
                edit_text_phone.requestFocus()
                return@setOnClickListener
            }

            val phoneNumber = "+${ccp.selectedCountryCode}$number"
            context?.toast(phoneNumber)

            PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(phoneNumber,
                120,
                TimeUnit.SECONDS
                ,requireActivity()
                ,callback)

            layoutPhone.visibility = View.INVISIBLE
            layoutVerification.visibility = View.VISIBLE
        }

        button_verify.setOnClickListener {
            val code = edit_text_phone.text.toString().trim()
            if (code.isEmpty()){
                edit_text_phone.error = "Field Required"
                edit_text_phone.requestFocus()
                return@setOnClickListener
            }

            verificationId?.let {
                val credential = PhoneAuthProvider.getCredential(it,code)
                addPhoneNumber(credential)
            }
        }
    }

    private val callback = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential?) {
            phoneAuthCredential?.let {
                addPhoneNumber(it)
            }
        }

        override fun onVerificationFailed(exeptions: FirebaseException?) {
            context?.toast(exeptions?.message!!)
        }

        override fun onCodeSent(verificationId: String?, token: PhoneAuthProvider.ForceResendingToken?) {
            super.onCodeSent(verificationId, token)

            this@VerifyPhoneNumber.verificationId = verificationId
        }

    }

    private fun addPhoneNumber(it: PhoneAuthCredential) {
        FirebaseAuth.getInstance().currentUser?.updatePhoneNumber(it)?.addOnCompleteListener {task->
            if (task.isSuccessful){
                context?.toast("Phone Number Registered")
                val action = VerifyPhoneNumberDirections.actionPhoneVerified()
                Navigation.findNavController(button_verify).navigate(action)

            } else {
                context?.toast(task.exception?.message!!)
            }
        }
    }
}
