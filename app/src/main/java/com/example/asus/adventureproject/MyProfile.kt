package th.ac.nu.adventure


import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.example.asus.adventureproject.FBMyprofileAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.my_profile.*
import kotlinx.android.synthetic.main.profile_bar.*
import kotlinx.android.synthetic.main.row_profile.*


class MyProfile : AppCompatActivity(){
    private val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("adventure")
    private val adapter: FBMyprofileAdapter by lazy {
        val query = database.getReference("adventure")
        val options = FirebaseRecyclerOptions.Builder<Adventure>()
                .setQuery(query, Adventure::class.java)
                .setLifecycleOwner(this)
                .build()
        FBMyprofileAdapter(options,{ partItem : Adventure -> partItemClicked(partItem) })
    }
    private fun partItemClicked(partItem : Adventure) {


        // Launch second activity, pass part ID as string parameter
        val showDetailActivityIntent = Intent(this, MapsActivity::class.java)
        val lat = partItem.latitude.toString()
        val long = partItem.longitude.toString()
        val caption = partItem.caption
        val date = partItem.date
        val time = partItem.time
        val pic = partItem.image
        showDetailActivityIntent.putExtra("lat",lat)
        showDetailActivityIntent.putExtra("long",long)
        showDetailActivityIntent.putExtra("caption",caption)
        showDetailActivityIntent.putExtra("date",date)
        showDetailActivityIntent.putExtra("time",time)
        showDetailActivityIntent.putExtra("image",pic)
        startActivity(showDetailActivityIntent)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_profile)
        profile_recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        profile_recyclerView.adapter = adapter

        backToHome.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }


    }
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }
    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}