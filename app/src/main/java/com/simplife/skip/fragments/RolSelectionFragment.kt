package com.simplife.skip.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import com.simplife.skip.R


class RolSelectionFragment : Fragment() {

    lateinit var btPasajero: LinearLayout
    lateinit var btConductor: LinearLayout
    val fragmentId = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_rol_selection, container, false)



        btPasajero = view.findViewById(R.id.pasajero_container)
        btConductor = view.findViewById(R.id.conductor_container)

        val generalInformationFragment = RegisterFormGeneralFragment()


        btPasajero.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("rol", "pasajero")

            generalInformationFragment.arguments = bundle
            loadFragment(generalInformationFragment)
        }

        btConductor.setOnClickListener{
            val bundle = Bundle()

            bundle.putString("rol", "conductor")
            generalInformationFragment.arguments = bundle
            loadFragment(generalInformationFragment)
        }

        return view
    }

    fun loadFragment(fragment: Fragment){
        requireActivity().supportFragmentManager.beginTransaction().also{
                fragmentTransaction ->
                        fragmentTransaction.replace(R.id.fragment_container_register, fragment)
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
        }
    }


}