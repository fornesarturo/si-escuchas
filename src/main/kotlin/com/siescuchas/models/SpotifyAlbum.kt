package com.siescuchas.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.ArrayList

class SpotifyAlbum(
        val id: String,
        val name: String,
        val uri: String,
        @JsonProperty("release_date") val releaseDate: String,
        var externalUrl: String = "",
        var cover: String = "",
        var artists: ArrayList<SpotifyArtist>
) {
    @JsonProperty("external_urls")
    private fun unpackExternalUrls(externalUrls: Map<String, String>) {
        this.externalUrl = externalUrls.getOrDefault("spotify", "")
    }

    @JsonProperty("images")
    private fun unpackImage(images: ArrayList<Map<String, String?>>) {
        val imageMap = images.get(0)
        this.cover = imageMap.getOrDefault("url", "")!!
    }

    override fun toString(): String {
        return "SpotifyAlbum[id=$id, name=$name, uri=$uri, releaseDate=$releaseDate, externalUrl=$externalUrl, cover=$cover]"
    }
}