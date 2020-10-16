package com.simplife.skip.adapter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.simplife.skip.fragments.*

internal class PagerViewAdapter(fm:FragmentManager , fl: List<Fragment>) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    var  fragmentList = fl

    override fun getItem(position: Int): Fragment {
        Log.i("Posicion en el FL",position.toString())
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    /*
    private fun init() {


        mViewPager.setOnNavigationItemSelectedListener { menuItem ->

            if (currentMenuItemId != menuItem.itemId) {
                currentMenuItemId = menuItem.itemId

                when (currentMenuItemId) {
                    R.id.navigationHome -> {changeFragment(TAG_ONE, MapsFragment.newInstance()); btnImgRefresh.visibility = View.VISIBLE}
                    R.id.navigationContribuir -> {changeFragment(TAG_SECOND, ContribuirFragment.newInstance()); btnImgRefresh.visibility = View.INVISIBLE}
                    R.id.navigationGuardado -> {changeFragment(TAG_THIRD, GuardadosFragment.newInstance()); btnImgRefresh.visibility = View.INVISIBLE}
                    R.id.navigationPerfil -> {changeFragment(TAG_FOURTH, PerfilFragment.newInstance()); btnImgRefresh.visibility = View.INVISIBLE}
                }

                return@setOnNavigationItemSelectedListener true
            }

            false
        }

    }
*/
}