package com.siescuchas.repositories

import com.siescuchas.models.Channel
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface ChannelRepository: ReactiveMongoRepository<Channel, String>