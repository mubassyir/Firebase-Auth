package com.mubassyir.firebasetutorial.utils

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.mubassyir.firebasetutorial.ui.HomeActivity
import com.mubassyir.firebasetutorial.ui.LoginActivity

fun Context.toast(message:String){
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
}

fun View.snackbar(message:String){
    Snackbar.make(this,message,Snackbar.LENGTH_LONG).also {snackbar->
        snackbar.setAction("Ok"){
            snackbar.dismiss()
        }
    }
}

fun Context.login(){
    Intent(this, HomeActivity::class.java).also {
        startActivity(it)
    }
}

fun Context.signOut(){
    Intent(this, LoginActivity::class.java).also {
        startActivity(it)
        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    }
}
