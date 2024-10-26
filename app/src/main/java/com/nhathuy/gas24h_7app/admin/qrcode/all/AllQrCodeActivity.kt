package com.nhathuy.gas24h_7app.admin.qrcode.all

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.adapter.AllQrCodeAdapter
import com.nhathuy.gas24h_7app.databinding.ActivityAllQrCodeBinding
import javax.inject.Inject

class AllQrCodeActivity : AppCompatActivity(),AllQrCodeContract.View {
    private lateinit var binding: ActivityAllQrCodeBinding
    private lateinit var adapter: AllQrCodeAdapter
    private var actionMode: ActionMode? = null

    @Inject
    lateinit var presenter: AllQrCodePresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllQrCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        setupRecyclerView()
        setupSwipeRefresh()
        presenter.loadAllQrCodes()
    }


    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.menuInflater?.inflate(R.menu.menu_qr_selection, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.action_print -> {
                    presenter.printSelectedQrCodes()
                    mode?.finish()
                    true
                }
                R.id.action_select_all -> {
                    // Add select all functionality
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            actionMode = null
            presenter.clearSelections()
        }
    }

    private fun setupRecyclerView() {
        adapter = AllQrCodeAdapter(
            onSave = { position -> presenter.saveQrCodeToGallery(position) },
            onShare = { position -> presenter.shareQrCode(position) },
            onItemSelect = { position ->
                presenter.toggleSelection(position)
            }
        )

        binding.recyclerViewAllQr.apply {
            layoutManager = GridLayoutManager(this@AllQrCodeActivity, 2)
            adapter = this@AllQrCodeActivity.adapter
        }
    }
    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnClickListener {
            presenter.loadAllQrCodes()
        }
    }

    override fun showLoading() {
        binding.swipeRefreshLayout.isRefreshing = true
    }

    override fun hideLoading() {
        binding.swipeRefreshLayout.isRefreshing = false
    }

    override fun showMessage(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun showQrCodes(qrData: List<Triple<String, String, Bitmap>>) {
        adapter.updateData(qrData)
    }

    override fun updateSelectedCount(count: Int) {
        actionMode?.title = "Đã chọn $count"
    }

    override fun showSelectionMode(show: Boolean) {
        if (show && actionMode == null) {
            actionMode = startActionMode(actionModeCallback)
        } else if (!show && actionMode != null) {
            actionMode?.finish()
            actionMode = null
        }
    }
}