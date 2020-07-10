package com.mysasse.wheretonext.ui.rides

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mysasse.wheretonext.R
import com.mysasse.wheretonext.data.models.Ride
import com.mysasse.wheretonext.ui.RideClickListener

class RidesAdapter(private val rides: List<Ride>, private val listener: RideClickListener) :
    RecyclerView.Adapter<RidesAdapter.RideViewHolder>() {

    inner class RideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(ride: Ride) {

            itemView.findViewById<TextView>(R.id.date_time_tv).text = "${ride.time} ${ride.date}"
            itemView.findViewById<TextView>(R.id.cost_tv).text = ride.cost.toString()
            itemView.findViewById<TextView>(R.id.pickup_location_tv).text =
                ride.pickup?.get("name").toString()
            itemView.findViewById<TextView>(R.id.drop_off_location_tv).text =
                ride.dropOff?.get("name").toString()

            itemView.setOnClickListener { listener.onRideClicked(ride, itemView) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideViewHolder {
        return RideViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.single_ride_layout, parent, false)
        )
    }

    override fun getItemCount() = rides.size

    override fun onBindViewHolder(holder: RideViewHolder, position: Int) {
        holder.bind(rides[position])
    }
}