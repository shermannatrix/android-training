package com.training.TrainingApp

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.*
import android.util.Log
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    val REQUEST_TAKE_PHOTO = 1
    val IMAGE_PICK_CODE = 2
    var currentPhotoPath: String = ""
    var photoURI: Uri? = null

    private lateinit var llSecondRow: LinearLayout
    private lateinit var btnTakePhoto: Button
    private lateinit var btnShowPhoto: Button
    private lateinit var btnSharePhoto: Button
    private lateinit var btnPickPhoto: Button
    private lateinit var textMessage: TextView
    private lateinit var imgPhoto: ImageView

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                textMessage.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                textMessage.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                textMessage.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    /**
     *  This is one way we can declare a function to handle our Button click events.
     */
//    fun btnTakePhoto_Click(view: View)  {
//        Toast.makeText(this,"You have clicked me!",Toast.LENGTH_LONG).show()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        llSecondRow = findViewById(R.id.llSecondRow)

        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnShowPhoto = findViewById(R.id.btnShowPhoto)
        btnSharePhoto = findViewById(R.id.btnSharePhoto)
        btnPickPhoto = findViewById(R.id.btnPickPhoto)
        textMessage = findViewById(R.id.message)
        imgPhoto = findViewById(R.id.imgPhoto)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        btnTakePhoto.setOnClickListener {
            //Toast.makeText(this,"You have clicked me!",Toast.LENGTH_LONG).show()
            dispatchTakePictureIntent()
        }

        btnPickPhoto.setOnClickListener {
            val pickPhotoIntent = Intent(Intent.ACTION_PICK)
            pickPhotoIntent.type = "image/*"
            startActivityForResult(pickPhotoIntent, IMAGE_PICK_CODE)
        }

        btnShowPhoto.setOnClickListener {
            val photo = File(currentPhotoPath)
            if (photo.length() > 0) {
                imgPhoto.setImageBitmap(BitmapFactory.decodeFile(currentPhotoPath))
            }
        }

        btnSharePhoto.setOnClickListener {
            val intent = Intent().apply {
                this.action = Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_STREAM, photoURI)
                this.type = "image/jpeg"
            }
            startActivity(Intent.createChooser(intent, "Share Image"))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            //btnShowPhoto.isVisible = true
            //btnSharePhoto.isVisible = true
            llSecondRow.isVisible = true
        } else if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            imgPhoto.setImageURI(data?.data)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.i("DIRECTORY_PICTURES", getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath)
        return File.createTempFile(
            "TRAINING_${timeStamp}_",   /* prefix */
            ".jpg",                 /* suffix */
            storageDir                    /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {takePictureIntent ->
            takePictureIntent.resolveActivity (packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                // Continue only if the File was successfully created.
                photoFile?.also {
                    photoURI = FileProvider.getUriForFile(
                        this,
                        "com.training.TrainingApp",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }
}
