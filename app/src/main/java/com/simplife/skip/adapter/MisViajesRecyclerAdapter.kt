package com.simplife.skip.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.simplife.skip.R
import com.simplife.skip.activities.ViajeDetail
import com.simplife.skip.models.Viaje
import com.simplife.skip.models.ViajeInicio
import kotlinx.android.synthetic.main.myviaje_list_item.view.*

class MisViajesRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items : List<ViajeInicio> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MiViajeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.myviaje_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is MiViajeViewHolder->{
                holder.bind(items.get(position))
            }
        }
    }

    fun submitList(viajeList: List<ViajeInicio>){
        items = viajeList
    }

    class MiViajeViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val miuserImage = itemView.miviaje_image
        val miviajeTitle = itemView.miviaje_title
        val miauthor = itemView.miviaje_author
        val miviajeSource = itemView.miviaje_origen
        val miviajeDestiny = itemView.miviaje_destino
        val miviajeHoraOrigen = itemView.miviaje_horaorigen
        val miviajeHoraDestino = itemView.miviaje_horadestino
        val reportbutton = itemView.miviaje_commentBtn

        fun bind(viaje: ViajeInicio){
            miviajeTitle.setText(viaje.fechaPublicacion)
            miauthor.setText(viaje.nombres)
            miviajeSource.setText(viaje.paradas.get(0).ubicacion)
            miviajeDestiny.setText(viaje.paradas.get(1).ubicacion)
            miviajeHoraDestino.setText(viaje.horaFin)
            miviajeHoraOrigen.setText(viaje.horaInicio)

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(viaje.imagen)
                .into(miuserImage)

            reportbutton.setOnClickListener{
                itemView.context.startActivity(Intent(itemView.context, ViajeDetail::class.java).putExtra("via", viaje.id))
            }
        }
    }


}