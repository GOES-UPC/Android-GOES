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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.simplife.skip.R
import com.simplife.skip.activities.Login
import com.simplife.skip.interfaces.UsuarioApiService
import com.simplife.skip.models.Usuario
import com.simplife.skip.util.URL_API
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_viaje_detail.*
import kotlinx.android.synthetic.main.fragment_perfil.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.coroutineContext

/**
 * A simple [Fragment] subclass.
 */
class PerfilFragment : Fragment() {

    private lateinit var perfil_nombre: TextView
    private lateinit var perfil_sede: TextView
    private lateinit var perfil_fb: TextView
    private lateinit var perfil_ubicacion: TextView
    private lateinit var perfil_foto: ImageView
    private lateinit var cerrar_sesion: LinearLayout
    private lateinit var editar_perfil: LinearLayout
    private lateinit var opciones: CardView

    private lateinit var perfil_settings: ImageButton

    private lateinit var usuarioService: UsuarioApiService

    var usuario: Usuario? = null
    var usuarioid = 0L
    var rol = "pasajero"
    private lateinit var prefs : SharedPreferences
    private lateinit var edit: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("FragmentPerfil","Creado")

    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val vista = inflater.inflate(R.layout.fragment_perfil, container, false)


        prefs = activity!!.getSharedPreferences("user", Context.MODE_PRIVATE)
        edit= prefs.edit()

        perfil_nombre = vista.findViewById(R.id.perfil_username)
        perfil_fb = vista.findViewById(R.id.perfil_fb)
        perfil_sede = vista.findViewById(R.id.perfil_sede)
        //perfil_ubicacion = vista.findViewById(R.id.perfil_ubicacion)
        perfil_foto = vista.findViewById(R.id.perfil_photo)
        cerrar_sesion = vista.findViewById(R.id.cerrar_sesion)
        editar_perfil = vista.findViewById(R.id.editar_perfil)
        opciones = vista.findViewById(R.id.opciones)

        perfil_settings = vista.findViewById(R.id.perfil_settings)

        //Recibimos data de usuario
        usuarioid = prefs.getLong("idusuario",0)
        rol = prefs.getString("rol","pasajero")!!

        perfil_settings.setOnClickListener{
            if(opciones.visibility == View.GONE) {
                opciones.visibility = View.VISIBLE
            }
            else
                opciones.visibility = View.GONE
        }

        cerrar_sesion.setOnClickListener{
            edit.clear().commit()
            this.activity?.finish()
            context?.startActivity(Intent(context, Login::class.java))
        }





        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        usuarioService = retrofit.create(UsuarioApiService::class.java)

        if (rol == "ROL_CONDUCTOR")
        {
            getConductor()
        }
        else
        {
            getPasajero()
        }




        return vista
    }

    companion object {
        @JvmStatic
        fun newInstance() = PerfilFragment()
    }

    fun getPasajero()
    {

        val requestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)

        usuarioService.getUsuarioById(usuarioid).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>?, response: Response<Usuario>?) {
                val usuario = response?.body()

                edit.putString("nombres",usuario?.nombres)
                edit.putString("apellidos",usuario?.apellidos)
                edit.putString("imagen",usuario?.imagen)
                edit.commit()

                perfil_nombre.setText(usuario?.nombres+" "+usuario?.apellidos)
                perfil_sede.setText(usuario?.sede)
                layoutFacebook.isVisible = false

                context?.applicationContext?.let {
                    Glide.with(it)
                        .applyDefaultRequestOptions(requestOptions)
                        .load(usuario?.imagen)
                        .into(perfil_foto)
                }


            }
            override fun onFailure(call: Call<Usuario>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })
    }

    fun getConductor()
    {

        val requestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)

        usuarioService.getUsuarioById(usuarioid).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>?, response: Response<Usuario>?) {
                val usuario = response?.body()

                edit.putString("nombres",usuario?.nombres)
                edit.putString("apellidos",usuario?.apellidos)
                edit.putString("imagen",usuario?.imagen)
                edit.commit()

                perfil_nombre.setText(usuario?.nombres+" "+usuario?.apellidos)
                perfil_sede.setText(usuario?.sede)
                perfil_fb.setText(usuario?.facebook)

                context?.applicationContext?.let {
                    Glide.with(it)
                        .applyDefaultRequestOptions(requestOptions)
                        .load(usuario?.imagen)
                        .into(perfil_foto)
                }


            }
            override fun onFailure(call: Call<Usuario>?, t: Throwable?) {
                t?.printStackTrace()
            }
        })
    }

}
