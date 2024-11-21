package com.nhathuy.gas24h_7app.fragment.delete

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
import com.nhathuy.gas24h_7app.databinding.FragmentChatBinding
import com.nhathuy.gas24h_7app.databinding.FragmentDeleteAccountBinding
import com.nhathuy.gas24h_7app.ui.main.MainActivity
import javax.inject.Inject


class DeleteAccountFragment : Fragment(), DeleteAccountContract.View {

    private var _binding: FragmentDeleteAccountBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenter:DeleteAccountPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentDeleteAccountBinding.inflate(inflater,container,false)
        presenter.attachView(this)

        setupListeners()

        return binding.root
    }

    private fun setupListeners() {
        binding.btnCancel.setOnClickListener {
            navigateMain()
        }
        binding.btnConfirm.setOnClickListener {
            presenter.deleteAccount()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as Gas24h_7Application).getGasComponent().inject(this)
    }

    override fun showMessage(message: String) {
        Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
    }

    override fun navigateMain() {
        startActivity(Intent(requireContext(),MainActivity::class.java))
    }

}