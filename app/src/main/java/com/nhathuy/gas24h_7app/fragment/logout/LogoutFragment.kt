package com.nhathuy.gas24h_7app.fragment.logout

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.databinding.FragmentHotlineBinding
import com.nhathuy.gas24h_7app.databinding.FragmentLogoutBinding
import com.nhathuy.gas24h_7app.ui.main.MainActivity
import javax.inject.Inject


class LogoutFragment : Fragment(R.layout.fragment_logout) {

    private var _binding: FragmentLogoutBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLogoutBinding.inflate(inflater,container,false)
        return binding.root
    }


}