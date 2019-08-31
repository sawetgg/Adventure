package th.ac.nu.adventure


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_adventure.*
import kotlinx.android.synthetic.main.add_top_bar.*
import th.ac.nu.adventure.R.layout.add_top_bar
import java.text.SimpleDateFormat
import java.util.*

class AddAdventure : AppCompatActivity() {
    lateinit var imageUri: Uri
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    protected var mLastLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_adventure)

        val uriString: String = intent.getStringExtra("photoFileURI")
        imageUri = Uri.parse(uriString)
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
        takePhoto.setImageBitmap(bitmap)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        add_top_bar.apply {
            saveAdventure.setOnClickListener {
                submitPost()
            }
        }

    }
    public override fun onStart() {
        super.onStart()

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            getLastLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient!!.lastLocation
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful && task.result != null) {
                        mLastLocation = task.result
                        latitude = (mLastLocation)!!.latitude
                        longitude = (mLastLocation)!!.longitude
                    } else {
                        Log.w(TAG, "getLastLocation:exception", task.exception)

                    }
                }
    }


    private fun showSnackbar(mainTextStringId: Int, actionStringId: Int,
                             listener: View.OnClickListener) {

        Toast.makeText(this@AddAdventure, getString(mainTextStringId), Toast.LENGTH_LONG).show()
    }


    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this@AddAdventure,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE)
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    View.OnClickListener {
                        // Request permission
                        startLocationPermissionRequest()
                    })

        } else {
            Log.i(TAG, "Requesting permission")
            startLocationPermissionRequest()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size <= 0) {

                Log.i(TAG, "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation()
            } else {
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        View.OnClickListener {
                            // Build intent that displays the App settings screen.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package",
                                    BuildConfig.APPLICATION_ID, null)
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        })
            }
        }
    }

    companion object {

        private val TAG = "LocationProvider"

        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }

    fun submitPost() {
        progressbarLoading.visibility = View.VISIBLE
        val caption = captionEditText.text.toString()
        val date = Date()
        val today = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).format(date)
        val time = SimpleDateFormat("H:mm:ss a", Locale.ENGLISH).format(date)

        val post = Adventure(caption = caption, image = "", date = "$today", time = "$time", latitude = latitude,longitude = longitude)

        val database = FirebaseDatabase.getInstance().reference
        val postsRef = database.child("adventure")
        val newPostRef = postsRef.push()
        newPostRef.setValue(post)

        val storage = FirebaseStorage.getInstance().reference
        val uploadRef = storage.child("adventure").child(newPostRef.key).child("image")
        uploadRef.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    newPostRef.child("image").setValue(taskSnapshot.downloadUrl.toString())
                    progressbarLoading.visibility = View.GONE
                    finish()
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener { exception ->
                    progressbarLoading.visibility = View.GONE
                    Toast.makeText(this@AddAdventure, "Failed to upload image", Toast.LENGTH_LONG).show()
                }
    }
}
