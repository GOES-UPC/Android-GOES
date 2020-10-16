package com.simplife.skip.activities

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.emmanuelkehinde.shutdown.Shutdown
import com.simplife.skip.R
import com.simplife.skip.adapter.PagerViewAdapter
import com.simplife.skip.fragments.*
import com.simplife.skip.interfaces.UsuarioApiService
import com.simplife.skip.models.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {


    private lateinit var homebtn: ImageButton
    private lateinit var viajesbtn: ImageButton
    private lateinit var notifbtn: ImageButton
    private lateinit var perfilbtn: ImageButton
    private lateinit var buscarbtn: ImageButton

    private lateinit var mViewPager: ViewPager
    private lateinit var mPagerViewAdapter: PagerViewAdapter


    private lateinit var fragmentList : MutableList<Fragment>


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("user", Context.MODE_PRIVATE)
        val edit= prefs.edit()

        val usuarioid = prefs.getLong("idusuario",0)

        fragmentList = arrayListOf()
        fragmentList.add(HomeFragment.newInstance())
        fragmentList.add(SearchFragment.newInstance())
        fragmentList.add(ViajesFragment.newInstance())
        fragmentList.add(NotificacionFragment.newInstance())
        fragmentList.add(PerfilFragment.newInstance())

        mViewPager = findViewById(R.id.viewPager)

        homebtn = findViewById(R.id.homebtn)
        viajesbtn = findViewById(R.id.carbtn)
        notifbtn = findViewById(R.id.notifbtn)
        perfilbtn = findViewById(R.id.profilebtn)
        buscarbtn = findViewById(R.id.searchbtn)

        requestPermissionAndContinue()

        homebtn.setOnClickListener {
            mViewPager.currentItem = 0
        }

        buscarbtn.setOnClickListener {
            mViewPager.currentItem = 1
        }

        viajesbtn.setOnClickListener {

            mViewPager.currentItem = 2

        }

        notifbtn.setOnClickListener {
            mViewPager.currentItem = 3

        }

        perfilbtn.setOnClickListener {
            mViewPager.currentItem = 4

        }




        mPagerViewAdapter = PagerViewAdapter(supportFragmentManager,fragmentList)
        mViewPager.adapter = mPagerViewAdapter
        mViewPager.offscreenPageLimit = 4

        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }
            override fun onPageSelected(position: Int) {
                changeTabs(position)
                Log.i("Posicion",position.toString())
            }
            override fun onPageScrollStateChanged(state: Int) {}
        })

        //mViewPager.currentItem = 0
        //homebtn.setImageResource(R.drawable.ic_home_selected)
    }

    private fun changeTabs(position: Int) {
        if (position == 0) {
            homebtn.setImageResource(R.drawable.ic_home_selected)
            buscarbtn.setImageResource(R.drawable.ic_search)
            viajesbtn.setImageResource(R.drawable.ic_car)
            notifbtn.setImageResource(R.drawable.ic_notifications)
            perfilbtn.setImageResource(R.drawable.ic_account)
        }
        if (position == 1) {
            homebtn.setImageResource(R.drawable.ic_home_gray)
            buscarbtn.setImageResource(R.drawable.ic_search_selected)
            viajesbtn.setImageResource(R.drawable.ic_car)
            notifbtn.setImageResource(R.drawable.ic_notifications)
            perfilbtn.setImageResource(R.drawable.ic_account)
        }
        if (position == 2) {
            homebtn.setImageResource(R.drawable.ic_home_gray)
            buscarbtn.setImageResource(R.drawable.ic_search)
            viajesbtn.setImageResource(R.drawable.ic_car_selected)
            notifbtn.setImageResource(R.drawable.ic_notifications)
            perfilbtn.setImageResource(R.drawable.ic_account)
        }
        if (position == 3) {
            homebtn.setImageResource(R.drawable.ic_home_gray)
            buscarbtn.setImageResource(R.drawable.ic_search)
            viajesbtn.setImageResource(R.drawable.ic_car)
            notifbtn.setImageResource(R.drawable.ic_notifications_selected)
            perfilbtn.setImageResource(R.drawable.ic_account)
        }
        if (position == 4) {
            homebtn.setImageResource(R.drawable.ic_home_gray)
            buscarbtn.setImageResource(R.drawable.ic_search)
            viajesbtn.setImageResource(R.drawable.ic_car)
            notifbtn.setImageResource(R.drawable.ic_notifications)
            perfilbtn.setImageResource(R.drawable.ic_account_selected)
        }
    }

    override fun onBackPressed() {
        Shutdown.now(this, "Presione de nuevo para salir")
    }


    private fun requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.e("Error", "permission denied, show dialog")
                //mostrarDialogoPermisos()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        } else {
            mViewPager.currentItem = 0
            homebtn.setImageResource(R.drawable.ic_home_selected)
        }
        this
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when(requestCode)
        {
            -1 -> {}
            1 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mViewPager.currentItem = 0
                    homebtn.setImageResource(R.drawable.ic_home_selected)
                    this
                }  else {
                    requestPermissionAndContinue()
                    this
                }
            }
        }

    }



}






