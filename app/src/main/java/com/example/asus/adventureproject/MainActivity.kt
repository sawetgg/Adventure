package th.ac.nu.adventure

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri

import android.os.Bundle
import android.provider.MediaStore

import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast

import com.example.asus.adventureproject.FBAdventureAdapter


import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_top_bar.*
import java.io.File
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    val database = FirebaseDatabase.getInstance()
    val adapter: FBAdventureAdapter by lazy {
        val query = database.getReference("adventure").orderByChild("date")
        val options = FirebaseRecyclerOptions.Builder<Adventure>()
                .setQuery(query,Adventure::class.java)
                .setLifecycleOwner(this)
                .build()
        FBAdventureAdapter(options,{ partItem : Adventure -> partItemClicked(partItem) })
    }
    private fun partItemClicked(partItem : Adventure) {


        // Launch second activity, pass part ID as string parameter
        val showDetailActivityIntent = Intent(this, AdventureDetail::class.java)
        val caption = partItem.caption
        val date = partItem.date
        val time = partItem.time
        val pic = partItem.image
        showDetailActivityIntent.putExtra("caption",caption)
        showDetailActivityIntent.putExtra("date",date)
        showDetailActivityIntent.putExtra("time",time)
        showDetailActivityIntent.putExtra("image",pic)
        startActivity(showDetailActivityIntent);
    }




    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = adapter

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                //takePhoto
                validatePermissions()

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {

                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myProfile.setOnClickListener{
            val intent = Intent(this,MyProfile::class.java)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }
    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    private val TAKE_PHOTO_REQUEST = 101
    private var currentPhotoPath: String = ""
    private fun validatePermissions() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object: PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        launchCamera()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?,
                                                                    token: PermissionToken?) {
                        AlertDialog.Builder(this@MainActivity)
                                .setTitle(R.string.storage_permission_rationale_title)
                                .setMessage(R.string.storage_permition_rationale_message)
                                .setNegativeButton(android.R.string.cancel,
                                        { dialog, _ ->
                                            dialog.dismiss()
                                            token?.cancelPermissionRequest()
                                        })
                                .setPositiveButton(android.R.string.ok,
                                        { dialog, _ ->
                                            dialog.dismiss()
                                            token?.continuePermissionRequest()
                                        })
                                .setOnDismissListener({ token?.cancelPermissionRequest() })
                                .show()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        Toast.makeText(this@MainActivity, R.string.storage_permission_denied_message,
                                Toast.LENGTH_LONG)
                                .show()
                    }
                })
                .check()
    }

    private fun launchCamera() {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        val fileUri = contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(packageManager) != null) {
            currentPhotoPath = fileUri.toString()
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(intent, TAKE_PHOTO_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == TAKE_PHOTO_REQUEST) {
            processCapturedPhoto()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun processCapturedPhoto() {
        val cursor = contentResolver.query(Uri.parse(currentPhotoPath),
                Array(1) {android.provider.MediaStore.Images.ImageColumns.DATA},
                null, null, null)
        cursor.moveToFirst()
        val photoPath = cursor.getString(0)
        cursor.close()
        val file = File(photoPath)
        val uri = Uri.fromFile(file)

        val intent = Intent(this, AddAdventure::class.java)
        intent.putExtra("photoFileURI", uri.toString())
        startActivity(intent)
    }
}
