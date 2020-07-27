package com.siescuchas.models

import com.fasterxml.jackson.annotation.JsonProperty

class SpotifyArtist(
        val id: String,
        val name: String,
        val uri: String,
        val href: String,
        var externalUrl: String = ""
) {
    @JsonProperty("external_urls")
    private fun unpackExternalUrls(externalUrls: Map<String, String>) {
        this.externalUrl = externalUrls.getOrDefault("spotify", "")
    }

    override fun toString(): String {
        return "SpotifyArtist=[id=$id, name=$name, uri=$uri, href=$href, externalUrl=$externalUrl]"
    }
}