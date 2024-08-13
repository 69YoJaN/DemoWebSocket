package com.example.kekaclockinclockout.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.kekaclockinclockout.R
import com.example.kekaclockinclockout.model.TextMessage

class MessageAdapter(private val messages: List<TextMessage>, private val isSender: (Int) -> Boolean) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderCard: CardView = itemView.findViewById(R.id.senderCard)
        val receiverCard: CardView = itemView.findViewById(R.id.receiverCard)
        val senderTextView: TextView = itemView.findViewById(R.id.sender)
        val receiverTextView: TextView = itemView.findViewById(R.id.receiver)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_theme, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        
        if (isSender(position)) {
            holder.senderCard.visibility = View.VISIBLE
            holder.receiverCard.visibility = View.GONE
            holder.senderTextView.text = message.text_data
        } else {
            holder.senderCard.visibility = View.GONE
            holder.receiverCard.visibility = View.VISIBLE
            holder.receiverTextView.text = message.text_data
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}
