package com.simplife.skip.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.simplife.skip.R
import com.simplife.skip.models.Solicitud
import kotlinx.android.synthetic.main.solicitud_list_item.view.*

class SolicitudesPasajeroRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items : List<Solicitud> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ResenaViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.solicitud_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ResenaViewHolder->{
                holder.bind(items.get(position))
            }
        }
    }
    fun submitList(solicitudList: List<Solicitud>){
        items = solicitudList
    }

    class ResenaViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){

        val soliUserImage = itemView.solis_image
        val solisMessage = itemView.solis_message
        val solisAuthor = itemView.solis_author
        val solisTime = itemView.solis_tiempo
        val solisCond = itemView.solisPas

        val scale2 = AnimationUtils.loadAnimation(itemView.context,R.anim.fade_scale_animation)

        fun bind(solicitud: Solicitud){

            solisAuthor.setText(solicitud.viaje.conductor.nombres +" "+ solicitud.viaje.conductor.apellidos)
            solisMessage.setText(solicitud.mensaje)
            solisTime.setText(solicitud.horaSolicitud)

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            solisCond.animation = scale2

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(solicitud.viaje.conductor.imagen)
                .into(soliUserImage)
        }
    }

}