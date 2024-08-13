package com.example.kekaclockinclockout.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kekaclockinclockout.webSocketManager.WebSocketManager
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.Response
import org.json.JSONObject

class MainViewModel : ViewModel() {

    private val webSocketManager = WebSocketManager()
    private val _messageDisplay = MutableLiveData<String>()
    val messageDisplay: LiveData<String> get() = _messageDisplay

    fun connectWebSocket(url: String) {
        webSocketManager.connect(url, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                _messageDisplay.postValue("WebSocket connected")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                println("Raw received message: $text")

                try {
                    val jsonObject = JSONObject(text)
                    val type = jsonObject.optString("type", null.toString())
                    val dataArray = jsonObject.optJSONArray("data")

                    if (type == "send_message" && dataArray != null && dataArray.length() > 0) {
                        val rawMessage = dataArray.getString(0)
                        val cleanedMessage = rawMessage.trim().removeSurrounding("\"").trimEnd('}')
                        _messageDisplay.postValue("Received: $cleanedMessage")
                    } else {
                        _messageDisplay.postValue("Error: Unexpected message format or type")
                    }
                } catch (e: Exception) {
                    _messageDisplay.postValue("Error parsing message: ${e.message}")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                _messageDisplay.postValue("WebSocket error: ${t.message}")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                _messageDisplay.postValue("WebSocket closed: $reason (Code: $code)")
            }
        })
    }

    fun sendMessage(textData: String) {
        webSocketManager.sendMessage(textData)
    }

    fun disconnectWebSocket() {
        webSocketManager.disconnect()
    }

    override fun onCleared() {
        super.onCleared()
        webSocketManager.disconnect()
    }
}
