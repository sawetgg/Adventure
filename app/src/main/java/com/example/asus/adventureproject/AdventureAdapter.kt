package th.ac.nu.adventure

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.row_adv.view.*
import th.ac.nu.adventure.R


class AdventureAdapter(private  val adv : List<Adventure>) : RecyclerView.Adapter<AdventureAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_adv,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city = adv[position]
        holder.bind(city)
    }

    override fun getItemCount(): Int {
        return adv.size
    }
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
        private var view: View = v

        fun bind(adventure: Adventure){
            view.captionText.text = adventure.caption

            view.dateText.text = adventure.date
            view.timeText.text = adventure.time


        }
    }
}