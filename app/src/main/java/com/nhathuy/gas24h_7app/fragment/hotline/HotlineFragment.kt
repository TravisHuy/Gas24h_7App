package com.nhathuy.gas24h_7app.fragment.hotline

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.databinding.FragmentHotlineBinding
import com.nhathuy.gas24h_7app.fragment.home.HomeFragment
import com.nhathuy.gas24h_7app.ui.main.MainActivity


class HotlineFragment : Fragment(R.layout.fragment_hotline),HotlineContract.View {
    private var _binding: FragmentHotlineBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter: HotlinePresenter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentHotlineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = HotlinePresenter()
        presenter.attachView(this)

        binding.btnCancel.setOnClickListener {
            presenter.onCancelButtonClicked()
        }
        binding.btnCall.setOnClickListener {
            presenter.onCallButtonClicked()
        }
    }
    override fun navigateHome() {
        startActivity(Intent(requireContext(),MainActivity::class.java))
    }

    override fun makePhoneCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data= Uri.parse("tel:$phoneNumber")
        }

        val zaloIntent= Intent(Intent.ACTION_VIEW).apply {
            data= Uri.parse("zalo://chat?phone=$phoneNumber")
        }

        val chooser = Intent.createChooser(intent, "Chọn ứng dụng để gọi")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(zaloIntent))

        startActivity(chooser)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        presenter.detachView()
    }

}