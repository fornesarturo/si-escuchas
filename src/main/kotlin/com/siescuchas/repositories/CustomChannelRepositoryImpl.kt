package com.siescuchas.repositories

import com.siescuchas.models.Channel
import com.siescuchas.models.SpotifyTrack
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.BooleanOperators.Not.not
import org.springframework.data.mongodb.core.findAndModify
import org.springframework.data.mongodb.core.query.*
import reactor.core.publisher.Mono

class CustomChannelRepositoryImpl @Autowired constructor(
        val reactiveMongoTemplate: ReactiveMongoTemplate
): CustomChannelRepository {
    override fun connectUser(channelId: String, user: String): Mono<Channel> {
        val query = Query(Criteria
                .where("id")
                .isEqualTo(channelId)
                .and("connected")
                .nin(user)
        )

        val update = Update().push("connected", user)
        val findAndModifyOptions = FindAndModifyOptions().returnNew(true)
        return reactiveMongoTemplate.findAndModify(query, update, findAndModifyOptions, Channel::class.java)
    }

    override fun disconnectUser(channelId: String, user: String): Mono<Channel> {
        val query = Query(Criteria
                .where("id")
                .isEqualTo(channelId)
                .and("connected").all(user)
        )
        val update = Update().pull("connected", user)
        val findAndModifyOptions = FindAndModifyOptions().returnNew(true)
        return reactiveMongoTemplate.findAndModify(query, update, findAndModifyOptions, Channel::class.java)
    }

    override fun addTrackToQueue(channelId: String, track: SpotifyTrack): Mono<Channel> {
        val query = Query(Criteria.where("id").isEqualTo(channelId))
        val update = Update().push("queue", track)
        val findAndModifyOptions = FindAndModifyOptions().returnNew(true)
        return reactiveMongoTemplate.findAndModify(query, update, findAndModifyOptions, Channel::class.java)
    }

    override fun removeTrackFromQueue(channelId: String, track: SpotifyTrack): Mono<Channel> {
        val query = Query(Criteria
                .where("id")
                .isEqualTo(channelId)
                .and("queue")
                .elemMatch(Criteria
                        .where("id")
                        .isEqualTo(track.id)
                )
        )
        val update = Update().pull("queue", Query(Criteria.where("id").isEqualTo(track.id)))
        val findAndModifyOptions = FindAndModifyOptions().returnNew(true)
        return reactiveMongoTemplate.findAndModify(query, update, findAndModifyOptions, Channel::class.java)
    }
}