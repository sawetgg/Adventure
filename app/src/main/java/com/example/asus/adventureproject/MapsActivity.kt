package th.ac.nu.adventure


import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_adventure_detail.*
import kotlinx.android.synthetic.main.custom_info_maps.*
import java.util.zip.Inflater


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    val database = FirebaseDatabase.getInstance()
    val testRef = database.getReference("adventure")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (intent.hasExtra("lat")) {
            val lat = intent.getStringExtra("lat")
            val long = intent.getStringExtra("long")
            val caption = intent.getStringExtra("caption")
            val date = intent.getStringExtra("date")
            val time = intent.getStringExtra("time")
            val image = intent.getStringExtra("image")
            val uri = Uri.parse(image)
            // Add a marker in Sydney and move the camera
            val NU = LatLng(lat.toDouble(), long.toDouble())
            mMap.addMarker(MarkerOptions().position(NU).title(caption).snippet("$date $time" ))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NU, 12f))
        }
        else{
            testRef.addValueEventListener(object: ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot?) {
                    val childen = snapshot!!.children
                    childen.forEach{
                        val lat = it.child("latitude").value.toString().toDouble()
                        val long = it.child("longitude").value.toString().toDouble()
                        val caption = it.child("caption").value.toString()
                        val NU = LatLng(lat,long )
                        mMap.addMarker(MarkerOptions().position(NU).title(caption))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NU, 10f))
                    }

                }

                override fun onCancelled(error: DatabaseError?) {}
            })
        }



    }
}




