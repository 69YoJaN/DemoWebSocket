package com.example.kekaclockinclockout.webSocketManager

import com.example.kekaclockinclockout.model.TextMessage
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketManager {

    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket
    private val gson = Gson()

    fun connect(url: String, listener: WebSocketListener) {
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, listener)
    }

    fun sendMessage(textData: String) {
        if (::webSocket.isInitialized) {
            val message = TextMessage(textData,true)
            val jsonMessage = gson.toJson(message)
            webSocket.send(jsonMessage)
        }
    }

    fun disconnect() {
        if (::webSocket.isInitialized) {
            webSocket.close(1000, "Disconnect requested")
        }
    }
}
