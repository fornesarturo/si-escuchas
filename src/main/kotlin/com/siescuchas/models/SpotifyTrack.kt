package com.siescuchas.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.ArrayList

class SpotifyTrack (
        val id: String,
        val name: String,
        val uri: String,
        var duration: String = "",
        var externalUrl: String = "",
        val artists: ArrayList<SpotifyArtist>,
        val album: SpotifyAlbum
) {
    @JsonProperty("duration_ms")
    private fun parseMSToMinutes(duration_ms: Int) {
        val totalSeconds: Int = duration_ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        this.duration = "$minutes:${seconds.toString().padStart(2, '0')}"
    }

    @JsonProperty("external_urls")
    private fun unpackExternalUrls(externalUrls: Map<String, String>) {
        this.externalUrl = externalUrls.getOrDefault("spotify", "")
    }

    override fun toString(): String {
        return "SpotifyTrack[id=$id, name=$name, uri=$uri, duration=$duration, externalUrl=$externalUrl, album=$album, artists=$artists]"
    }
}