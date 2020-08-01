package com.siescuchas.controllers

import com.siescuchas.models.Channel
import com.siescuchas.models.SpotifyTrack
import com.siescuchas.repositories.ChannelRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
// https://si-escuchas.netlify.app
@RestController
@RequestMapping("/channel")
class ChannelController @Autowired constructor(
        val channelRepository: ChannelRepository
) {
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    fun createChannel(@Validated @RequestBody channel: Channel): Mono<Channel> {
        return channelRepository.save(channel)
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getChannel(@PathVariable id: String): Mono<Channel> {
        return channelRepository.findById(id)
    }

    @PostMapping("/{id}/connectUser")
    @ResponseStatus(HttpStatus.OK)
    fun connectUser(
            @PathVariable id: String,
            @RequestParam user: String
    ): Mono<Channel> {
        return channelRepository.connectUser(id, user)
    }

    @PostMapping("/{id}/disconnectUser")
    @ResponseStatus(HttpStatus.OK)
    fun disconnectUser(
            @PathVariable id: String,
            @RequestParam user: String
    ): Mono<Channel> {
        return channelRepository.disconnectUser(id, user)
    }

    @PostMapping("/{id}/enqueueTrack")
    @ResponseStatus(HttpStatus.OK)
    fun enqueueTrack(
            @PathVariable id: String,
            @Validated @RequestBody track: SpotifyTrack
    ): Mono<Channel> {
        return channelRepository.addTrackToQueue(id, track)
    }

    @PostMapping("/{id}/dequeueTrack")
    @ResponseStatus(HttpStatus.OK)
    fun dequeueTrack(
            @PathVariable id: String,
            @Validated @RequestBody track: SpotifyTrack
    ): Mono<Channel> {
        return channelRepository.removeTrackFromQueue(id, track)
    }

}