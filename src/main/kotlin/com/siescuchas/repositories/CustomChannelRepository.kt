package com.siescuchas.repositories

import com.siescuchas.models.Channel
import com.siescuchas.models.SpotifyTrack
import reactor.core.publisher.Mono

interface CustomChannelRepository {
    fun connectUser(channelId: String, user: String): Mono<Channel>
    fun disconnectUser(channelId: String, user: String): Mono<Channel>
    fun addTrackToQueue(channelId: String, track: SpotifyTrack): Mono<Channel>
    fun removeTrackFromQueue(channelId: String, track: SpotifyTrack): Mono<Channel>
}