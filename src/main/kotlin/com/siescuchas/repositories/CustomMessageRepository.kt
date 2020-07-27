package com.siescuchas.repositories

import com.siescuchas.models.Message
import reactor.core.publisher.Flux

interface CustomMessageRepository {
    fun findRecentMessagesByChannelId(channelId: String): Flux<Message>
}