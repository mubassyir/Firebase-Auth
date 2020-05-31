package com.mubassyir.firebasetutorial.ui.fragments


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.mubassyir.firebasetutorial.R
import com.mubassyir.firebasetutorial.utils.toast
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
    private lateinit var uri : Uri


    companion object{
        private const val REQUEST_IMAGE_CAPTURE =100
        private const val DEFAULT_IMAGE_URI = "https://picsum.photos.200"
    }
    val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)

    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        currentUser?.let { user->
            if (user.photoUrl!=null){
                Glide.with(this)
                    .load(user.photoUrl)
                    .into(image_view_profile)
            }

            edit_text_name.setText(user.displayName)
            text_view_email.text = user.email
            text_phone.text = if (user.phoneNumber.isNullOrEmpty()) "Config Phone Number" else user.phoneNumber

            if (user.isEmailVerified){
                text_not_verified.visibility = View.GONE
            } else{
                text_not_verified.visibility = View.VISIBLE
            }
        }

        image_view_profile.setOnClickListener{
            takePictureIntent()
        }
        button_save.setOnClickListener { it ->
            val photo = currentUser?.photoUrl

            val name = edit_text_name.text.toString().trim()
            if (name.isEmpty()){
                edit_text_name.error = "Field is required"
                edit_text_name.requestFocus()
                return@setOnClickListener
            }
            val updates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(photo)
                .build()

            photo?.let {
                progressbar.visibility = View.VISIBLE
                currentUser?.updateProfile(updates)?.addOnCompleteListener{task->
                    if(task.isSuccessful){
                        progressbar.visibility = View.GONE
                        context?.toast("Profile Updated")
                    } else {
                        context?.toast(task.exception?.message!!)
                        progressbar.visibility = View.GONE
                    }
                }
            }
        }

        text_not_verified.setOnClickListener {
            currentUser?.sendEmailVerification()?.addOnCompleteListener {
                if(it.isComplete) activity?.toast("Email Verification sent!") else activity?.toast(it.exception?.message!!)
            }
        }

        text_phone.setOnClickListener {
            val action = ProfileFragmentDirections.actionVerifyPhone()
            Navigation.findNavController(it).navigate(action)
        }

        text_view_email.setOnClickListener {

            val action = ProfileFragmentDirections.actionVerifyEmail()
            Navigation.findNavController(it).navigate(action)

        }
        text_password.setOnClickListener {
            val navigation = ProfileFragmentDirections.actionUpdatePassword()
            Navigation.findNavController(it).navigate(navigation)
        }


    }

    private fun takePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {pictureIntent->
            pictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                startActivityForResult(pictureIntent,
                    REQUEST_IMAGE_CAPTURE
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode== REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap
            uploadImageAndSaveUri(imageBitmap)
        }
    }

    private fun uploadImageAndSaveUri(imageBitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        val storageRef = FirebaseStorage.getInstance().reference.child(
            "pics/${FirebaseAuth.getInstance().currentUser?.uid}")
        imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val image = baos.toByteArray()

        val upload = storageRef.putBytes(image)

        progressbar_pic.visibility = View.VISIBLE
        upload.addOnCompleteListener{uploadTask->
            progressbar_pic.visibility = View.GONE
            if (uploadTask.isSuccessful){
                storageRef.downloadUrl.addOnCompleteListener{urlTask->
                    urlTask.result?.let {
                         uri = it
                        activity?.toast(uri.toString())
                        image_view_profile.setImageBitmap(imageBitmap)
                    }
                }
            } else {
                uploadTask.exception?.let {
                    activity?.toast(it.message!!)
                }
            }
        }
    }
}
