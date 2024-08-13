package com.example.kekaclockinclockout

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kekaclockinclockout.adapters.MessageAdapter
import com.example.kekaclockinclockout.model.TextMessage
import com.example.kekaclockinclockout.viewModel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var messageInput: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private val messages: MutableList<TextMessage> = mutableListOf()
    private var id : Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val connectButton: Button = findViewById(R.id.connectButton)
        val disconnectButton: Button = findViewById(R.id.disconnectButton)
        val sendButton: Button = findViewById(R.id.sendButton)
        val selectImageButton: Button = findViewById(R.id.selectImageButton)
        messageInput = findViewById(R.id.messageInput)
        recyclerView = findViewById(R.id.messageRecyclerView)
        val uniqueId : EditText = findViewById(R.id.uniqueId)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        messageAdapter = MessageAdapter(messages) { position -> position % 2 == 0 }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = messageAdapter

        viewModel.messageDisplay.observe(this) { message ->
            val receivedMessage = TextMessage(message, isSent = false)
            messages.add(receivedMessage)
            messageAdapter.notifyItemInserted(messages.size - 1)
            recyclerView.scrollToPosition(messages.size - 1)
        }

        selectImageButton.setOnClickListener {
            // selectImageLauncher.launch("image/*")
        }


        id?.let { uniqueId.setText(it) }
        connectButton.setOnClickListener {
            viewModel.connectWebSocket("wss://be40-2401-4900-1c17-3c48-60a5-ef31-6398-4e3f.ngrok-free.app/ws/test/$id")
        }

        disconnectButton.setOnClickListener {
            viewModel.disconnectWebSocket()
        }

        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString()
            if (messageText.isNotBlank()) {
                val textMessage = TextMessage(messageText, isSent = true)
                messages.add(textMessage)
                messageAdapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)
                viewModel.sendMessage(messageText)
                messageInput.text.clear()
            }
        }
    }

    private fun getFilePathFromUri(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        }
        return null
    }
}
