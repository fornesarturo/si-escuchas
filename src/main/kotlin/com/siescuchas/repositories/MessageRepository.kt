package com.siescuchas.repositories

import com.siescuchas.models.Message
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.mongodb.repository.Tailable
import reactor.core.publisher.Flux

interface MessageRepository: ReactiveMongoRepository<Message, String> {
    @Tailable
    fun findWithTailableCursorByChannelId(channelId: String): Flux<Message>;
}