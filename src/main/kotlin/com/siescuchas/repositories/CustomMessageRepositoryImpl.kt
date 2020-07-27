package com.siescuchas.repositories

import com.siescuchas.models.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import reactor.core.publisher.Flux
import java.util.Date

class CustomMessageRepositoryImpl @Autowired constructor(
        val reactiveMongoTemplate: ReactiveMongoTemplate
) : CustomMessageRepository {
    override fun findRecentMessagesByChannelId(channelId: String): Flux<Message> {
        val SECONDS_IN_MILLISECONDS = 1000
        val SECONDS = 5
        val nSecondsAgo = Date().time - (SECONDS * SECONDS_IN_MILLISECONDS)
        val nSecondsAgoDate = Date(nSecondsAgo)
        val query = Query(Criteria
                .where("createdAt").gte(nSecondsAgoDate)
                .and("channelId").isEqualTo(channelId))
        return reactiveMongoTemplate.tail(query, Message::class.java)
    }
}