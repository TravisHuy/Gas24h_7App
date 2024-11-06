package com.nhathuy.gas24h_7app.websocket

import android.util.Log
import com.google.gson.Gson
import com.nhathuy.gas24h_7app.data.helper.NotificationHelper
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

class WebSocketService @Inject constructor(private val notificationHelper: NotificationHelper) {

    private var webSocketClient: WebSocketClient? = null
    private var isConnected = false
    private val SOCKET_URL = "wss://mongodb-csvv.onrender.com/ws/websocket"

    fun connectForNotification(onConnected: () -> Unit, onError: (String) -> Unit) {
        if (isConnected) {
            onConnected()
            return
        }

        try {
            val uri = URI(SOCKET_URL)
            webSocketClient = object : WebSocketClient(uri) {
                override fun onOpen(handshakedata: ServerHandshake?) {
                    Log.d("WebSocket", "Connected")
                    isConnected = true
                    // Subscribe to STOMP destination after connection
                    val stompSubscribe = """
                        SUBSCRIBE
                        id:sub-0
                        destination:/travishuy/notifications
                        
                        
                    """.trimIndent()
                    send(stompSubscribe)

                    // Send STOMP connect frame
                    val stompConnect = """
                        CONNECT
                        accept-version:1.1,1.0
                        heart-beat:10000,10000
                        
                        
                    """.trimIndent()
                    send(stompConnect)

                    onConnected()
                }

                override fun onMessage(message: String?) {
                    message?.let {
                        try {
                            // Extract payload from STOMP message
                            val payloadStartIndex = it.indexOf("\n\n") + 2
                            if (payloadStartIndex > 1 && payloadStartIndex < it.length) {
                                val payload = it.substring(payloadStartIndex).trim()
                                if (payload.isNotEmpty()) {
                                    val notification = parseNotification(payload)
                                    notificationHelper.showNotification(
                                        notification.title,
                                        notification.content,
                                        notification.hotline
                                    )
                                } else {

                                }
                            } else {

                            }
                        } catch (e: Exception) {
                            Log.e("WebSocket", "Error parsing message", e)
                        }
                    }
                }

                override fun onClose(code: Int, reason: String?, remote: Boolean) {
                    Log.d("WebSocket", "Closed: $reason")
                    isConnected = false
                    // Try to reconnect after a delay
                    Thread {
                        Thread.sleep(5000)
                        connectForNotification(onConnected, onError)
                    }.start()
                }

                override fun onError(ex: Exception?) {
                    Log.e("WebSocket", "Error", ex)
                    isConnected = false
                    onError(ex?.message ?: "Unknown error occurred")
                }
            }

            webSocketClient?.connect()

        } catch (e: Exception) {
            Log.e("WebSocket", "Connection error", e)
            onError("Failed to initialize connection: ${e.message}")
        }
    }

    fun disconnect() {
        try {
            // Send STOMP disconnect frame before closing
            val stompDisconnect = """
                DISCONNECT
                receipt:77
                
                
            """.trimIndent()
            webSocketClient?.send(stompDisconnect)

            webSocketClient?.close()
            isConnected = false
        } catch (e: Exception) {
            Log.e("WebSocket", "Error during disconnect", e)
        }
    }

    private fun parseNotification(message: String): NotificationData {
        return try {
            Gson().fromJson(message, NotificationData::class.java)
        } catch (e: Exception) {
            Log.e("WebSocket", "Error parsing JSON", e)
            NotificationData("Error", "Failed to parse notification", "")
        }
    }

    data class NotificationData(
        val title: String,
        val content: String,
        val hotline: String,
        val imageData: String? = null,
        val date: String? = null
    )
}