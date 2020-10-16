package com.simplife.skip.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.simplife.skip.R
import com.simplife.skip.interfaces.UsuarioApiService
import com.simplife.skip.models.Auto
import com.simplife.skip.models.RegisterEntity
import com.simplife.skip.models.SignUpRequest
import com.simplife.skip.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.sign


class VehicleInformationFragment : Fragment() {

    lateinit var usuarioService: UsuarioApiService
    lateinit var placa: EditText
    lateinit var nAsientos: EditText
    lateinit var nAnhosUso: EditText
    lateinit var poliza: EditText
    lateinit var marca: EditText
    lateinit var modelo: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_vehicle_information, container, false)

        val confirmButton: Button = view.findViewById(R.id.confirm_button_driver_register)
        val backButton: Button = view.findViewById(R.id.back_button_driver_vehicle)

        placa = view.findViewById(R.id.etPlaca)
        nAsientos = view.findViewById(R.id.etAsientos)
        nAnhosUso = view.findViewById(R.id.etAnhosUso)
        poliza = view.findViewById(R.id.etPoliza)
        marca = view.findViewById(R.id.etMarca)
        modelo = view.findViewById(R.id.etModelo)

        var request: SignUpRequest? = null
        arguments?.let{
            request = it.getSerializable("signUpRequest") as SignUpRequest
        }

        usuarioService = ApiClient.retrofit.create(UsuarioApiService::class.java)

        backButton.setOnClickListener {
            val fmt = requireActivity().supportFragmentManager
            fmt.popBackStack(null, 0)
        }

        confirmButton.setOnClickListener {
            if(camposValidos()){
                request?.let{
                    val auto = Auto(
                        placa.text.toString(),
                        poliza.text.toString(),
                        marca.text.toString(),
                        modelo.text.toString(),
                        nAsientos.text.toString().toInt(),
                        nAnhosUso.text.toString().toInt())
                    it.auto = auto
                    registrarConductor(it)
                }

            } else {
                Toast.makeText(requireContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }

        }


        return view
    }

    fun registrarConductor(signUpRequest: SignUpRequest){
        usuarioService.registroUsuario(signUpRequest).enqueue(object: Callback<RegisterEntity>{
            override fun onFailure(call: Call<RegisterEntity>, t: Throwable) {
                Log.i("Fallo en registro",  "F")
            }

            override fun onResponse(
                call: Call<RegisterEntity>,
                response: Response<RegisterEntity>
            ) {
                Log.i("Code", response.code().toString())
                Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        })

    }


    fun camposValidos(): Boolean{
        if(!placa.text.isEmpty() &&
                !nAsientos.text.isEmpty() &&
                !nAnhosUso.text.isEmpty() &&
                !poliza.text.isEmpty() &&
                !marca.text.isEmpty() &&
                !modelo.text.isEmpty()){
            return true
        } else{
            if(placa.text.isEmpty()){
                placa.setError("Completar el campo placa")
            }
            if(nAsientos.text.isEmpty()){
                nAsientos.setError("Completar el campo Nº de asientos")
            }
            if(nAnhosUso.text.isEmpty()){
                nAnhosUso.setError("Completar el campo años de uso")
            }
            if(poliza.text.isEmpty()){
                poliza.setError("Completar el campo poliza de seguro")
            }
            if(marca.text.isEmpty()){
                marca.setError("Completar el campo marca")
            }
            if(modelo.text.isEmpty()){
                modelo.setError("Completar el campo modelo")
            }
            return false
        }

    }

}