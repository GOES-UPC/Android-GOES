package com.simplife.skip.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.simplife.skip.R
import com.simplife.skip.activities.Login
import com.simplife.skip.interfaces.UsuarioApiService
import com.simplife.skip.models.RegisterEntity
import com.simplife.skip.models.SignUpRequest
import com.simplife.skip.util.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterFormGeneralFragment : Fragment() {

    val fragmentId = 2
    lateinit var botonAtras: Button
    lateinit var botonSiguiente: Button
    lateinit var botonConfirmar: Button
    lateinit var usuarioService: UsuarioApiService

    lateinit var etName: EditText
    lateinit var  etLastName: EditText
    lateinit var etDni: EditText
    lateinit var etCodigo: EditText
    lateinit var etContrasena: EditText
    lateinit var spSede: Spinner
    lateinit var errorSede: TextView
    lateinit var sede: String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register_form_general, container, false)

        var rol = ""
        arguments?.let{
            rol = it.getString("rol").toString()
        }

        botonAtras = view.findViewById(R.id.back_button_general_register)
        botonSiguiente = view.findViewById(R.id.next_button_general_register)
        botonConfirmar = view.findViewById(R.id.confirm_button_general_register)

        botonAtras.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack(null,0)
        }
        usuarioService = ApiClient.retrofit.create(UsuarioApiService::class.java)

        val fragmentDriver = RegisterDriverInformationFragment()

        etName = view.findViewById<EditText>(R.id.etName)
        etLastName = view.findViewById<EditText>(R.id.etLastName)
        etDni = view.findViewById<EditText>(R.id.etDni)
        etCodigo = view.findViewById<EditText>(R.id.etCode)
        etContrasena = view.findViewById<EditText>(R.id.etPassword)

        spSede = view.findViewById<Spinner>(R.id.spCampus)
        errorSede = view.findViewById(R.id.error_spinner_sede)

        validateInputs()

        val listaSedes = arrayListOf<String>("--Sede--", "Monterrico", "San Isidro", "San Miguel", "Villa")

        val adapter = ArrayAdapter<String>(requireContext(), R.layout.custom_spinner_layout, listaSedes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        sede = ""

        spSede.adapter = adapter
        spSede.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                sede = listaSedes.get(0)
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                errorSede.setError(null)
                errorSede.visibility = View.GONE
                sede = listaSedes.get(position)
            }
        }


        if(rol.equals("pasajero")){
            botonConfirmar.visibility = View.VISIBLE
            botonSiguiente.visibility = View.GONE



            botonConfirmar.setOnClickListener {
                if(camposCompletos()){
                    val request = SignUpRequest(
                        etCodigo.text.toString(),
                        etContrasena.text.toString(),
                        etDni.text.toString(),
                        etName.text.toString(),
                        etLastName.text.toString(),
                        sede,
                        "https://wp-content.bluebus.com.br/wp-content/uploads/2017/03/31142426/twitter-novo-avatar-padrao-2017-bluebus.png",
                        listOf("pasajero").toSet(), null, null)

                    registrarUsuarioPasajero(request)
                } else {
                    Toast.makeText(requireContext(), "Por favor, completar todos los campos", Toast.LENGTH_SHORT).show()
                }



            }



        } else if(rol.equals("conductor")){

            botonSiguiente.visibility = View.VISIBLE
            botonConfirmar.visibility = View.GONE
            botonSiguiente.setOnClickListener{
                if(camposCompletos()){
                    val bundle = Bundle()
                    bundle.putString("codigo", etCodigo.text.toString())
                    bundle.putString("contrasena", etContrasena.text.toString())
                    bundle.putString("dni", etDni.text.toString())
                    bundle.putString("nombres", etName.text.toString())
                    bundle.putString("apellidos", etLastName.text.toString())
                    bundle.putString("sede", sede)
                    bundle.putString("imagen", "https://wp-content.bluebus.com.br/wp-content/uploads/2017/03/31142426/twitter-novo-avatar-padrao-2017-bluebus.png")
                    fragmentDriver.arguments = bundle
                    loadFragment(fragmentDriver)
                } else {
                    Toast.makeText(requireContext(), "Por favor, completar todos los campos", Toast.LENGTH_SHORT).show()
                }

            }
        }

        return view
    }


    fun registrarUsuarioPasajero(form: SignUpRequest){
        usuarioService.registroUsuario(form).enqueue(object: Callback<RegisterEntity>{
            override fun onFailure(call: Call<RegisterEntity>, t: Throwable) {
                Log.i("Fallo en registro", "No se pudo registrar")
            }

            override fun onResponse(call: Call<RegisterEntity>, response: Response<RegisterEntity>) {
                if(response.isSuccessful){
                    Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    /*val fragmentManager = requireActivity().supportFragmentManager
                    fragmentManager.popBackStack(null,0)*/
                    requireActivity().finish()
                } else {
                    Toast.makeText(context, "No se pudo registrar", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun loadFragment(fragment: Fragment){
        requireActivity().supportFragmentManager.beginTransaction().also{
                fragmentTransaction ->
            fragmentTransaction.replace(R.id.fragment_container_register, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    fun camposCompletos(): Boolean{
        if(!etName.text.isEmpty() and
            !etLastName.text.isEmpty() and
            !etDni.text.isEmpty() and
            !etCodigo.text.isEmpty() and
            !etContrasena.text.isEmpty() and
            (sede != "--Sede--")){

            return true

        } else{

            if(etName.text.isEmpty()){
                etName.setError("Completar el campo nombres")
            }
            if(etLastName.text.isEmpty()){
                etLastName.setError("Completar el campo apellidos")
            }
            if(etDni.text.isEmpty()){
                etDni.setError("Completar el campo DNI")
            }
            if(etCodigo.text.isEmpty()){
                etCodigo.setError("Completar el campo código")
            }
            if(etContrasena.text.isEmpty()){
                etContrasena.setError("Completar el campo contraseña")
            }
            if(sede == "--Sede--"){
                errorSede.visibility = View.VISIBLE
                errorSede.setError("Elegir una sede")
            }
            return false
        }

    }

    fun validateInputs(){

        etDni.addTextChangedListener(( object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().length < 8) etDni.setError("El dni debe tener mínimo 8 caracteres")
                else etDni.setError(null)
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                /*if (p0.toString().length  == 8) etDni.setError("El dni solo puede tener hasta 8 caracteres")
                else etDni.setError(null)*/
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


            }
        }))


        etCodigo.addTextChangedListener(( object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().length  < 10) etCodigo.setError("El código debe tener 10 caracteres")
                else etCodigo.setError(null)
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                /*if (p0.toString().length  == 10) etCodigo.setError("El código solo puede tener hasta 10 caracteres")
                else etCodigo.setError(null)*/
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        }))

        etContrasena.addTextChangedListener(( object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().length  < 6) etContrasena.setError("La contraseña debe tener mínimo 6 caracteres y máximo 25 caracteres")
                else etContrasena.setError(null)
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
               /* if(p0.toString().length == 25) etContrasena.setError("La contraseña solo puede tener como máximo 25 caracteres")*/
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        }))

    }

    fun validarEmail( email : String): Boolean{
        val pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }


}