package com.nhathuy.gas24h_7app.admin.notification.all_notification

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.RemoveDone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nhathuy.gas24h_7app.Gas24h_7Application
import com.nhathuy.gas24h_7app.admin.notification.edit_notification.EditNotificationActivity
import com.nhathuy.gas24h_7app.admin.notification.theme.Gas24h_7AppTheme
import com.nhathuy.gas24h_7app.data.model.Notification
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

class AllNotificationActivity : ComponentActivity(), AllNotificationContract.View {

    private var currentNotifications = mutableStateListOf<Notification>()
    private var showLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)

    @Inject
    lateinit var presenter: AllNotificationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as Gas24h_7Application).getGasComponent().inject(this)

        setContent {
            Gas24h_7AppTheme {
                AllNotificationScreen()
            }
        }


        presenter.attachView(this)
        presenter.loadNotifications()
    }

    override fun showLoading() {
        showLoading = true
    }

    override fun hideLoading() {
        showLoading = false
    }

    override fun showMessage(message: String) {
        errorMessage = message
    }

    override fun showAllNotifications(notifications: List<Notification>) {
        currentNotifications.clear()
        currentNotifications.addAll(notifications)
    }

    override fun navigateEditNotification(id:String) {
        val intent = Intent(this, EditNotificationActivity::class.java)
        intent.putExtra("NOTIFICATION_ID",id)
        startActivity(intent)
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AllNotificationScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            TopAppBar(
                title = { Text("All Notifications") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.shadow(elevation = 0.dp)
            )

            //Main content
            if (showLoading) {
                Log.d("Loading", "LoadingIndicator is active")
                LoadingIndicator()
            } else {
                NotificationContent(notifications = currentNotifications)
            }

            // Error Handling
            errorMessage?.let { message ->
                ErrorDialog(
                    message = message,
                    onDismiss = { errorMessage = null }
                )
            }

        }

    }

    @Composable
    private fun NotificationContent(notifications: List<Notification>) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {

            items(notifications) { notification ->
                NotificationCard(
                    notification = notification,
                    onDelete =  { presenter.deleteNotification(notification.id) }
                )
            }
        }
    }

    @Composable
    private fun NotificationCard(notification: Notification,onDelete: (String) -> Unit) {
        var showDeleteDialog by remember { mutableStateOf(false) }

        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = notification.title,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = formatDate(notification.date),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.End
                )
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                , horizontalArrangement = Arrangement.Center) {

                    IconButton(onClick = { navigateEditNotification(notification.id) }) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(36.dp)
                                .padding(end = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.padding(start = 50.dp, end = 50.dp))

                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .size(36.dp)
                                .padding(end = 8.dp)
                        )
                    }
                }
            }
            if (showDeleteDialog) {
                DeleteNotificationDialog(
                    notification = notification,
                    onConfirm = {
                        onDelete(notification.id)
                        Log.d("AllNotification","${notification.id}")
                        showDeleteDialog = false
                    },
                    onDismiss = { showDeleteDialog = false }
                )
            }
        }
    }

    @Composable
    private fun DeleteNotificationDialog(notification: Notification, onConfirm: () -> Unit, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Delete Notification") },
            text = {
                Text("Are you sure you want to delete the notification: ${notification.title}?")
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm")
        return format.format(date)
    }

    @Composable
    private fun LoadingIndicator() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = Color.Red
            )
        }
    }

    @Composable
    private fun ErrorDialog(message: String, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            }
        )
    }


    //test card notification
    @Composable
    fun TestCard() {
        ElevatedCard(
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Thông báo giá gas tháng 11111111111111 năm 2024",
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Start,
                )
                Text(
                    text = "20-11-2024 10:45",
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        Gas24h_7AppTheme {
            TestCard()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun AllNotificationScreenPreview() {
        Gas24h_7AppTheme {
            AllNotificationScreen()
        }
    }
}


@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Gas24h_7AppTheme {
        Greeting2("Android")
    }
}