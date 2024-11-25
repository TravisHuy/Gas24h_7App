package com.nhathuy.gas24h_7app.fragment.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.databinding.FragmentProfileBinding
import com.nhathuy.gas24h_7app.databinding.FragmentSettingBinding


class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        // Set initial switch state based on current theme
        binding.switchTheme.isChecked =
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        // Set up switch listener for theme toggling
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            // Apply theme based on switch state
            val mode = if (isChecked) {
                AppCompatDelegate.MODE_NIGHT_YES // Dark mode
            } else {
                AppCompatDelegate.MODE_NIGHT_NO  // Light mode
            }

            // Apply the selected theme
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        return binding.root
    }
}