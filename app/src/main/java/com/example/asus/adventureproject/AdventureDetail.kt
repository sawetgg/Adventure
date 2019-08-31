package th.ac.nu.adventure

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.asus.adventureproject.GlideApp
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_adventure_detail.*
import kotlinx.android.synthetic.main.profile_bar.*
import kotlinx.android.synthetic.main.row_adv.view.*

class AdventureDetail : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adventure_detail)
        backToHome.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }



            val caption = intent.getStringExtra("caption")
            val date = intent.getStringExtra("date")
            val time = intent.getStringExtra("time")
            val image = intent.getStringExtra("image")
            val uri = Uri.parse(image)
            captionDetail.text = "Caption : $caption"
            dateDetail.text = "Date : $date"
            timeDetail.text = "Time : $time"
            Picasso.with(this).load(uri).into(imageDetail)




    }
}
