package com.example.asus.adventureproject

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import kotlinx.android.synthetic.main.row_adv.view.*
import th.ac.nu.adventure.Adventure
import th.ac.nu.adventure.R


class FBAdventureAdapter(options: FirebaseRecyclerOptions<Adventure>,val clickListener: (Adventure) -> Unit)
    :FirebaseRecyclerAdapter<Adventure,FBAdventureAdapter.ViewHolder>(options){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_adv,parent,false)
        return FBAdventureAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int, model: Adventure) {
        holder.bind(model,position,clickListener)

    }
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
        private var view: View = v
        fun bind(adventure: Adventure, position: Int, clickListener: (Adventure) -> Unit){
            view.captionText.text=adventure.caption
            view.dateText.text = adventure.date
            view.timeText.text = adventure.time

            view.setOnClickListener {
                    clickListener(adventure)
            }
            GlideApp.with(view.context )
                    .load(adventure.image )
                    .into(view.imgAdv)
        }


    }
}