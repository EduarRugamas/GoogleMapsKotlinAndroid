package com.example.test_phone_email_maps.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.test_phone_email_maps.R
import com.example.test_phone_email_maps.Utils.ChangeFragment

class FragmentContacts : Fragment() {

    private lateinit var navegar: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_contacts, container, false)

        navegar = view.findViewById(R.id.Btn_navegar)

        fragmentMaps()


        return view
    }

    private fun fragmentMaps() {
        navegar.setOnClickListener{
            ChangeFragment(MapsFragment(), activity!!.supportFragmentManager)
        }
    }



}