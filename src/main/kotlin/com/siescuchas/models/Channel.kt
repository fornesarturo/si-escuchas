package com.siescuchas.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "channel")
class Channel (
        @Id var id: String?,
        val owner: String
        )