package com.simplife.skip.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.simplife.skip.R
import com.simplife.skip.activities.MainActivity
import com.simplife.skip.adapter.SolicitudesPasajeroRecyclerAdapter
import com.simplife.skip.adapter.SolicitudesRecyclerAdapter
import com.simplife.skip.interfaces.SolicitudApiService
import com.simplife.skip.interfaces.UsuarioApiService
import com.simplife.skip.models.LoginEntity
import com.simplife.skip.models.Solicitud
import com.simplife.skip.models.Usuario
import com.simplife.skip.util.SolisData
import com.simplife.skip.util.URL_API
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple [Fragment] subclass.
 */
class NotificacionFragment : Fragment() {

    private  lateinit var solicitudesAdapter: SolicitudesRecyclerAdapter
    private  lateinit var solicitudesPasajeroAdapter: SolicitudesPasajeroRecyclerAdapter
    private lateinit var recyclerView: RecyclerView


    private lateinit var swipeRefreshLayout: SwipeRefreshLayout


    private lateinit var prefs : SharedPreferences
    private lateinit var edit: SharedPreferences.Editor

    private lateinit var solicitudService: SolicitudApiService


    var usuarioid = 0L
    var rol = "pasajero"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("FragmentNotifcaciones","Creado")
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val vista = inflater.inflate(R.layout.fragment_notificacion, container, false)


        prefs = activity!!.getSharedPreferences("user", Context.MODE_PRIVATE)
        edit= prefs.edit()
        usuarioid = prefs.getLong("idusuario", 0)
        rol = prefs.getString("rol","pasajero")!!
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        solicitudService = retrofit.create(SolicitudApiService::class.java)


        recyclerView = vista.findViewById(R.id.recycler_notificaciones_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        if(rol == "ROL_CONDUCTOR")
        {
            getSolicitudesConductor()
        }
        else
        {
            getSolicitudesPasajero()
        }

        swipeRefreshLayout = vista.findViewById(R.id.swipeRefreshLayoutNotifications)

        swipeRefreshLayout.setOnRefreshListener {
            if(rol == "ROL_CONDUCTOR")
            {
                getSolicitudesConductor()
            }
            else
            {

                getSolicitudesPasajero()
            }
        }

        //val data1 = SolisData.createResenas()
        //solicitudesAdapter.submitList(data1)

        return vista
    }

    fun getSolicitudesPasajero()
    {
        solicitudService.getSolicitudesPorUsuario(usuarioid).enqueue(object : Callback<List<Solicitud>> {
            override fun onResponse(call: Call<List<Solicitud>>?, response: Response<List<Solicitud>>?) {
                val solicitudes = response!!.body()
                solicitudesPasajeroAdapter = SolicitudesPasajeroRecyclerAdapter()
                recyclerView.adapter = solicitudesPasajeroAdapter
                solicitudesPasajeroAdapter.submitList(solicitudes!!)
                swipeRefreshLayout.isRefreshing = false

                Log.i("Solicitudes Pasajero", solicitudes.toString())
            }
            override fun onFailure(call: Call<List<Solicitud>>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })
    }


    fun getSolicitudesConductor()
    {
        solicitudService.getSolicitudesPorConductor(usuarioid).enqueue(object : Callback<List<Solicitud>> {
            override fun onResponse(call: Call<List<Solicitud>>?, response: Response<List<Solicitud>>?) {
                val solicitudes = response!!.body()
                solicitudesAdapter = SolicitudesRecyclerAdapter()
                recyclerView.adapter = solicitudesAdapter
                solicitudesAdapter.submitList(solicitudes!!)
                swipeRefreshLayout.isRefreshing = false

                Log.i("Solicitudes Conductor", solicitudes.toString())
                Log.i("Solicitudes Conductor", response.body().toString())
            }

            override fun onFailure(call: Call<List<Solicitud>>?, t: Throwable?) {
                t?.printStackTrace()
                swipeRefreshLayout.isRefreshing = false
                Toast.makeText(context,"Ha ocurrido un error",Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = NotificacionFragment()
    }
}
