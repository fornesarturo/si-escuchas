package com.siescuchas.models

import com.fasterxml.jackson.annotation.JsonProperty

class SpotifySearch (
        val tracks: SpotifySearchTracks
)

class SpotifySearchTracks(
        val items: ArrayList<SpotifyTrack>,
        val href: String,
        val previous: String?,
        val next: String?,
        val total: Int,
        val limit: Int,
        val offset: Int
)