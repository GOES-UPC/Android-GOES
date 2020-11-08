package com.simplife.skip.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.simplife.skip.R
import com.simplife.skip.activities.StartViajeActivity
import com.simplife.skip.activities.ViajeDetail
import com.simplife.skip.models.Viaje
import com.simplife.skip.models.ViajeInicio
import kotlinx.android.synthetic.main.myviaje_conductor_list_item.view.*

class MisViajeCondRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items : List<ViajeInicio> = ArrayList()
    private lateinit var context : Fragment

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

    fun submitContext(context: Fragment){
        this.context = context
    }

    fun getContext():Fragment{
        return this.context
    }

    fun submitList(viajeList: List<ViajeInicio>){
        items = viajeList
    }

    inner class MiViajeViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView){
       //TODO: HAY QUE ACTUALIZAR

        val miviajeSource = itemView.miviajecond_origen
        val miviajeDestiny = itemView.miviajecond_destino
        val miviajeHoraOrigen = itemView.miviajecond_horaorigen
        val miviajeHoraDestino = itemView.miviajecond_horadestino
        val cardViaje = itemView.cardViajeCond

        val scale2 = AnimationUtils.loadAnimation(itemView.context,R.anim.fade_scale_animation)

        fun bind(viaje: ViajeInicio){
            miviajeSource.setText(viaje.paradas.get(0).ubicacion)
            miviajeDestiny.setText(viaje.paradas.get(1).ubicacion)
            miviajeHoraDestino.setText(viaje.horaFin)
            miviajeHoraOrigen.setText(viaje.horaInicio)
            Log.i("Viaje: ", viaje.estadoViaje)
            itemView.setOnClickListener{
                if(viaje.estadoViaje != "FINALIZADO"){
                     getContext().startActivityForResult(Intent(itemView.context, StartViajeActivity::class.java).putExtra("via", viaje),222)
                }
                else{
                    Toast.makeText(itemView.context, "El viaje ha finalizado", Toast.LENGTH_SHORT).show()
                }
            }
            cardViaje.animation = scale2
        }
    }

    interface RowClickListener{
        fun OnItemClickListener(viaje: ViajeInicio)
    }


}