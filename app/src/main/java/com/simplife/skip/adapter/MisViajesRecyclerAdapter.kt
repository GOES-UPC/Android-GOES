package com.simplife.skip.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.app.ShareCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.simplife.skip.R
import com.simplife.skip.activities.MainActivity
import com.simplife.skip.activities.ViajeDetail
import com.simplife.skip.interfaces.ReportApiService
import com.simplife.skip.interfaces.SolicitudApiService
import com.simplife.skip.models.*
import com.simplife.skip.util.URL_API
import com.simplife.skip.util.URL_API_GOOGLE_MAPS
import kotlinx.android.synthetic.main.myviaje_list_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MisViajesRecyclerAdapter(val launchActivity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

    inner class MiViajeViewHolder constructor(
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
        val sharebutton = itemView.miviaje_shareBtn
        val cardViaje = itemView.cardViajePas

        val scale2 = AnimationUtils.loadAnimation(itemView.context,R.anim.fade_scale_animation)

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

            cardViaje.animation = scale2

            reportbutton.setOnClickListener{
                dialogCalificar(itemView, viaje)
            }

            sharebutton.setOnClickListener{
                compartirViaje(itemView, viaje.id, launchActivity)
            }

        }

        fun calificar(dialog: AlertDialog, itemView: View,et_report: EditText, calificacion: Float, viajeId: Long)
        {
            var reporteService: ReportApiService

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            reporteService = retrofit.create(ReportApiService::class.java)
            var prefs : SharedPreferences
            prefs = itemView.context.getSharedPreferences("user", Context.MODE_PRIVATE)

            var report = ReportRequest(mensaje = et_report.text.toString(),calificacion =  calificacion)

            reporteService.calificarViaje(report, prefs.getLong("idusuario",0L), viajeId).enqueue(object : Callback<Reporte> {
                override fun onResponse(call: Call<Reporte>?, response: Response<Reporte>?) {
                    val rep = response!!.body()
                    if(rep == null){
                        Toast.makeText(itemView.context, "No se puede calificar el viaje", Toast.LENGTH_SHORT).show()
                    }
                    Log.i("Reporte",rep.toString())
                    dialog.dismiss()
                }
                override fun onFailure(call: Call<Reporte>?, t: Throwable?) {
                    t?.printStackTrace()
                }
            })
        }

        fun dialogCalificar(itemView: View, viaje: ViajeInicio)
        {
            var dialog: AlertDialog
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)

            //https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=12&size=400x400&key=YOUR_API_KEY
            val builer = AlertDialog.Builder(itemView.context)
            //val inflater = layoutInflater
            val view = LayoutInflater.from(itemView.context).inflate(R.layout.dialog_report,null)
            //val view  = inflater.inflate(R.layout.dialog_report,null)
            builer.setView(view)

            dialog =  builer.create()
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.show()

            val rb = view.findViewById(R.id.rating_viaje) as RatingBar
            val reportarBtn = view.findViewById(R.id.btn_dialog_reportar) as Button
            val et_dialog_report = view.findViewById(R.id.et_reportar) as EditText
            var calificacion : Float
            calificacion = 0f

            rb.rating = 0f
            rb.stepSize = .5f
            rb.setOnRatingBarChangeListener {ratingBar, rating, fromUser ->
                calificacion = rating
                Toast.makeText(itemView.context, "Rating: $rating", Toast.LENGTH_SHORT).show()
            }


            reportarBtn.setOnClickListener {
                if (et_dialog_report.text.isNullOrEmpty())
                {
                    Toast.makeText(itemView.context,"Complete los campos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                calificar(dialog, itemView, et_dialog_report, calificacion, viaje.id)
            }
        }

        fun compartirViaje(itemView: View, viajeId: Long, launchActivity: Activity){
            val prefs = itemView.context.getSharedPreferences("user", Context.MODE_PRIVATE)
            val nombreUsuario = prefs.getString("nombres", "Bryan")
            val url = "https://www.goesapp.com/viajes?id=${viajeId}"
            val type = "text/html"
            ShareCompat.IntentBuilder
                .from(launchActivity)
                .setType(type)
                .setSubject("Goes - Viaje compartido")
                .setHtmlText("<b>$nombreUsuario cree que este viaje podría serte útil:</b> ${url}")
                .setChooserTitle(R.string.share_text)
                .startChooser()
        }
    }





}