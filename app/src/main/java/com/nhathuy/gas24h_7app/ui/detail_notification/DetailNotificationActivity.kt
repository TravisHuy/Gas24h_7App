package com.nhathuy.gas24h_7app.ui.detail_notification

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.R
import com.nhathuy.gas24h_7app.data.model.Notification
import com.nhathuy.gas24h_7app.databinding.ActivityDetailNotificationBinding
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import javax.inject.Inject

class DetailNotificationActivity : AppCompatActivity(),DetailNotificationContract.View {

    private lateinit var binding:ActivityDetailNotificationBinding

    @Inject
    lateinit var presenter: DetailNotificationPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDetailNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as Gas24h_7Application).getGasComponent().inject(this)
        presenter.attachView(this)

        val notificationId = intent.getStringExtra("NOTIFICATION_ID")
        presenter.loadNotificationDetail(notificationId!!)
    }

    override fun showNotification(notification: Notification) {
        binding.detailNotificationContent.text = notification.content
        binding.detailNotificationHotline.text = notification.hotline


        val format = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val formattedDate = format.format(notification.date)
        binding.detailNotificationDate.text = formattedDate

        val imageData = notification.imageData
        if(imageData.isNotEmpty()){
            try {
                val decodeString = Base64.decode(imageData,Base64.DEFAULT)
                val decodeByte = BitmapFactory.decodeByteArray(decodeString,0,decodeString.size)
                binding.detailNotificationImage.setImageBitmap(decodeByte)
            }
            catch (e:IllegalArgumentException){
                e.printStackTrace()
                Toast.makeText(this,"Failed to decode image",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun showError(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}