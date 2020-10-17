package com.simplife.skip.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.simplife.skip.*
import com.simplife.skip.models.Reporte
import com.simplife.skip.models.Resena
import kotlinx.android.synthetic.main.resena_item_list.view.*

class ResenasRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items : List<Reporte> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ResenaViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.resena_item_list, parent, false)
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
    fun submitList(resenaList: List<Reporte>){
        items = resenaList
    }

    class ResenaViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){

        val resenaUserImage = itemView.resena_image
        val resenaBody = itemView.resena_text
        val resenaValoracion = itemView.resena_valoracion
        val resenaPublish = itemView.resena_title
        val resenaAuthor = itemView.resena_author

        fun bind(resena: Reporte){

            resenaBody.setText(resena.contenido)
            resenaValoracion.setText(resena.valoracion.toString())
            resenaPublish.setText(resena.viaje.fechaPublicacion)
            val nombreCompleto: String
            nombreCompleto = resena.usuario.nombres + resena.usuario.apellidos
            resenaAuthor.setText(nombreCompleto)

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(resena.usuario.imagen)
                .into(resenaUserImage)
        }
    }

}