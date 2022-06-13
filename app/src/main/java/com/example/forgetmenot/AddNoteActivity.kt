package com.example.forgetmenot

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.*

class AddNoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

//        btn_choose_image = findViewById(R.id.btn_choose_image)
//        btn_upload_image = findViewById(R.id.btn_upload_image)
//        imagePreview = findViewById(R.id.image_preview)
//
//        firebaseStore = FirebaseStorage.getInstance()
//        storageReference = FirebaseStorage.getInstance().reference
    }
}