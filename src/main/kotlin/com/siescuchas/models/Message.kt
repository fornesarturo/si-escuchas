package com.siescuchas.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

// db.createCollection("messages", { capped: true, size: 5000000, max: 10000 });
@Document(collection = "messages")
class Message (
        @Id var id: String?,
        val message: String,
        val trackUri: String?,
        val track: SpotifyTrack?,
        val sender: String,
        val channelId: String,
        val createdAt: Date
) {
    override fun toString(): String {
        return "Message[id=$id, message=$message, trackUri=$trackUri, track=$track, sender=$sender, channelId=$channelId, createdAt=$createdAt]"
    }
}