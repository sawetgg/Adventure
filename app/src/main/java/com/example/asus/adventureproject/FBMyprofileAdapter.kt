package com.example.asus.adventureproject

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.my_profile.view.*
import kotlinx.android.synthetic.main.row_adv.view.*
import kotlinx.android.synthetic.main.row_profile.view.*
import th.ac.nu.adventure.Adventure
import th.ac.nu.adventure.R


class FBMyprofileAdapter(options: FirebaseRecyclerOptions<Adventure>,val clickListener: (Adventure) -> Unit)
    :FirebaseRecyclerAdapter<Adventure,FBMyprofileAdapter.ViewHolder>(options){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_profile,parent,false)
        return FBMyprofileAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int, model: Adventure) {
        holder.bind(model,clickListener)
    }
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
        private var view: View = v
         fun bind(adventure: Adventure,  clickListener: (Adventure) -> Unit){

            view.captionProfile.text = adventure.caption
            view.setOnClickListener {
                clickListener(adventure)
            }

            GlideApp.with(view.context )
                    .load(adventure.image )
                    .into(view.myListImageProfile)
        }



    }
}