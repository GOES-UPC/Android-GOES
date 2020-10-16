package com.simplife.skip.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simplife.skip.R
import com.simplife.skip.activities.StartViajeActivity
import com.simplife.skip.activities.ViajeDetail
import com.simplife.skip.models.Viaje
import kotlinx.android.synthetic.main.myviaje_conductor_list_item.view.*

class MisViajeCondRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items : List<Viaje> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MiViajeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.myviaje_conductor_list_item, parent, false)
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

    fun submitList(viajeList: List<Viaje>){
        items = viajeList
    }

    class MiViajeViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
       //TODO: HAY QUE ACTUALIZAR
        val miviajeSource = itemView.miviajecond_origen
        val miviajeDestiny = itemView.miviajecond_destino
        val miviajeHoraOrigen = itemView.miviajecond_horaorigen
        val miviajeHoraDestino = itemView.miviajecond_horadestino

        fun bind(viaje: Viaje){
            //miviajeSource.setText(viaje.conductor.ubicacion)
            miviajeDestiny.setText(viaje.conductor.sede)
            miviajeHoraDestino.setText(viaje.horaLlegada)
            miviajeHoraOrigen.setText(viaje.horaInicio)

            itemView.setOnClickListener{
                itemView.context.startActivity(Intent(itemView.context, StartViajeActivity::class.java).putExtra("via", viaje))
            }

        }

    }


}