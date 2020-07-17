package com.siescuchas.models

import com.fasterxml.jackson.annotation.JsonProperty

data class SpotifyToken(
        @JsonProperty("access_token") val accessToken: String,
        @JsonProperty("refresh_token") val refreshToken: String?
)