package com.simplife.skip.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView

import com.simplife.skip.R
import kotlinx.android.synthetic.main.fragment_search.*

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {

    private lateinit var buscador: TextView
    private lateinit var filtros: CardView
    private lateinit var buscarbtn: ImageButton


    private lateinit var prefs : SharedPreferences
    private lateinit var edit: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("FragmentSearch","Creado")
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val vista = inflater.inflate(R.layout.fragment_search, container, false)

        prefs = activity!!.getSharedPreferences("user", Context.MODE_PRIVATE)
        edit= prefs.edit()


        buscador = vista.findViewById(R.id.etBuscador)
        filtros = vista.findViewById(R.id.filtrar)
        buscarbtn = vista.findViewById(R.id.bussearchButton)

        buscador.setOnClickListener {
            filtros.visibility = View.VISIBLE
        }

        buscarbtn.setOnClickListener {
            filtros.visibility = View.GONE
        }
        return vista
    }

    companion object {
        @JvmStatic
        fun newInstance() = SearchFragment()
    }




}
