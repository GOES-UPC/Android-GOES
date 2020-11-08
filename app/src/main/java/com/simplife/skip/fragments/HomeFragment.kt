package com.simplife.skip.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.InflateException
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

import com.simplife.skip.R
import com.simplife.skip.activities.MainActivity
import com.simplife.skip.activities.Post
import com.simplife.skip.activities.ViajeDetail
import com.simplife.skip.adapter.ViajeRecyclerAdapter
import com.simplife.skip.interfaces.GoogleMapDirections
import com.simplife.skip.interfaces.StaticMapApiService
import com.simplife.skip.interfaces.UsuarioApiService
import com.simplife.skip.interfaces.ViajeApiService
import com.simplife.skip.models.Parada
import com.simplife.skip.models.Usuario
import com.simplife.skip.models.Viaje
import com.simplife.skip.models.ViajeInicio
import com.simplife.skip.util.API_KEY
import com.simplife.skip.util.TopSpacingItemDecoration
import com.simplife.skip.util.URL_API
import com.simplife.skip.util.URL_API_GOOGLE_MAPS
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.ClassCastException
import javax.sql.DataSource

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private  lateinit var viajeAdapter: ViajeRecyclerAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addBtn: ImageButton
    private lateinit var viajeService: ViajeApiService
    private lateinit var usuarioService: UsuarioApiService

    var usuario: Usuario? = null

    var rol = ""

    private lateinit var staticmapService :StaticMapApiService



    private lateinit var prefs : SharedPreferences
    private lateinit var edit: SharedPreferences.Editor

    private var viewDialog : View? = null
    private lateinit var dialog :AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("FragmentHome","Creado")
    }


    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val vista = inflater.inflate(R.layout.fragment_home, container, false)
        addBtn = vista.findViewById(R.id.add_btn)

        prefs = activity!!.getSharedPreferences("user", Context.MODE_PRIVATE)
        edit= prefs.edit()

        rol = prefs.getString("rol","")!!


        val usuarioid = prefs.getLong("idusuario",0)

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofitStaticMap = Retrofit.Builder()
            .baseUrl(URL_API_GOOGLE_MAPS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        staticmapService = retrofitStaticMap.create(StaticMapApiService::class.java)

        viajeService = retrofit.create(ViajeApiService::class.java)
        usuarioService = retrofit.create(UsuarioApiService::class.java)


        swipeRefreshLayout = vista.findViewById(R.id.swipeRefreshLayoutHome)


        if(rol == "ROL_CONDUCTOR"){
            addBtn.visibility = View.VISIBLE
        }

        addBtn.setOnClickListener{
            val i = Intent(context, Post::class.java)
            startActivityForResult(i,111)
            //context?.startActivity(Intent(context, Post::class.java))
        }

        recyclerView = vista.findViewById(R.id.recycler_viaje_view)
        recyclerView.layoutManager = LinearLayoutManager(context)


        addDataSet()
        swipeRefreshLayout.setOnRefreshListener {
            addDataSet()
        }
        return vista
    }

    private fun addDataSet(){
        viajeService!!.getHomeViajes().enqueue(object: Callback<List<ViajeInicio>> {
            override fun onResponse(call: Call<List<ViajeInicio>>, response: Response<List<ViajeInicio>>) {
                val viajesaux = response.body()

                var sorted = viajesaux!!.sortedWith(compareByDescending ({it.id}))

                viajeAdapter = ViajeRecyclerAdapter()
                recyclerView.adapter = viajeAdapter
                viajeAdapter.submitContext(this@HomeFragment)
                viajeAdapter.submitList(viajesaux)
                swipeRefreshLayout.isRefreshing = false
            }
            override fun onFailure(call: Call<List<ViajeInicio>>?, t: Throwable?) {
                t?.printStackTrace()
                swipeRefreshLayout.isRefreshing = false
                Toast.makeText(context,"Ha ocurrido un error",Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 111)
        {
            addDataSet()
            val act = activity as MainActivity
            (act.fragmentList[2] as ViajesFragment).addDataSet()
        }
        if (requestCode == 333)
        {
            val act = activity as MainActivity
            (act.fragmentList[2] as ViajesFragment).addDataSet()
            (act.fragmentList[3] as NotificacionFragment).addDataset()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    interface Respuesta{
        fun callback()
    }

     lateinit var  call : Respuesta

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            call = context as Respuesta
        }catch ( e: ClassCastException)
        {

        }
    }

    fun mostrarDialogViaje()
    {
            //https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=12&size=400x400&key=YOUR_API_KEY
            val builer = AlertDialog.Builder(context)
            if (viewDialog != null)
            {
                val parent = viewDialog!!.parent as ViewGroup
                if (parent != null)
                {
                    parent.removeView(viewDialog)
                }
            }
            try {
                viewDialog  = layoutInflater.inflate(R.layout.dialog_solicitar2,null)
            }catch (e: InflateException) {

            }


            builer.setView(viewDialog)
            dialog =  builer.create()
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.show()

            val solicitarButton = viewDialog!!.findViewById(R.id.btn_dialog_solicitar) as Button
            val iv_staticMap = viewDialog!!.findViewById(R.id.iv_staticmap) as ImageView


            solicitarButton.setOnClickListener {

            }
    }

    fun cargarStaticMapRoute(origen: Parada, destino: Parada, iv_staticMap: ImageView)
    {
        var origin = "${origen.latitud},${origen.longitud}"
        var destination = "${destino.latitud},${destino.longitud}"
        var size= "1000x1000"
        var key = API_KEY

        Log.i("Direcctions", "https://maps.googleapis.com/maps/api/directions/json?origin=$origin&destination=$destination&mode=driving&key=$API_KEY")
        staticmapService.getRoutes(origin,destination,"driving", key).enqueue(object :Callback<GoogleMapDirections>{
            override fun onResponse(call: Call<GoogleMapDirections>, response: Response<GoogleMapDirections>) {
                val googleMapDirection = response.body()
                val points = googleMapDirection!!.routes[0].overview_polyline.points
                Log.i("Entro", "https://maps.googleapis.com/maps/api/staticmap?size=$size&path=enc%3A$points&key=$key")

                Glide.with(context!!)
                    .load("https://maps.googleapis.com/maps/api/staticmap?size=$size&path=enc%3A$points&key=$key")
                    .into(iv_staticMap)
            }
            override fun onFailure(call: Call<GoogleMapDirections>, t: Throwable) {
                Log.e("F", "LA ptmr")
                Log.e("F", t.message.toString())
            }
        })
    }

}


