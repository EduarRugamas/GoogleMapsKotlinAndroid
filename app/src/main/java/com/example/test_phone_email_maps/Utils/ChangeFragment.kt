package com.example.test_phone_email_maps.Utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.test_phone_email_maps.R

fun ChangeFragment(fragmentChange: Fragment, supporFragmentManager: FragmentManager){

    val frag: Fragment = fragmentChange
    val fragMa: FragmentManager = supporFragmentManager

    val fragTran: FragmentTransaction = fragMa.beginTransaction()

    fragTran.replace(R.id.FrameLayout, frag)
    fragTran.addToBackStack(null)
    fragTran.commit()

}