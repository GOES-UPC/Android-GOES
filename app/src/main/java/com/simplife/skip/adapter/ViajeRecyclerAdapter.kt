package com.simplife.skip.adapter

import android.content.Context
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
import kotlinx.android.synthetic.main.viaje_list_item.view.*

class ViajeRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var items : List<ViajeInicio> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViajeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.viaje_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ViajeViewHolder->{
                holder.bind(items.get(position))
            }
        }
    }

    fun submitList(viajeList: List<ViajeInicio>){
        items = viajeList
    }

    class ViajeViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){

        val userImage = itemView.viaje_image
        val viajeTitle = itemView.viaje_title
        val author = itemView.viaje_author
        val viajeText = itemView.viaje_text
        val viajeSource = itemView.viaje_origen
        val viajeDestiny = itemView.viaje_destino
        val viajeHoraOrigen = itemView.viaje_hora_origen
        val viajeHoraDestino = itemView.viaje_hora_destino

        fun bind(viaje: ViajeInicio){
            viajeTitle.setText(viaje.fechaPublicacion)
            author.setText(viaje.nombres)
            viajeText.setText(viaje.descripcion)
            viajeSource.setText(viaje.paradas[1].ubicacion)
            viajeDestiny.setText(viaje.paradas[1].ubicacion)
            viajeHoraDestino.setText(viaje.horaFin)
            viajeHoraOrigen.setText(viaje.horaInicio)

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(viaje.imagen)
                .into(userImage)

            itemView.setOnClickListener{
                itemView.context.startActivity(Intent(itemView.context, ViajeDetail::class.java).putExtra("via", viaje.id))
            }
        }
    }
}
