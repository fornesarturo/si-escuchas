package com.siescuchas.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.ArrayList

class SpotifyMe(
        val id: String,
        val country: String,
        @JsonProperty("display_name") val displayName: String,
        val email: String,
        val href: String,
        var externalUrl: String = "",
        var profilePicture: String = ""
) {
    @JsonProperty("external_urls")
    private fun unpackExternalUrls(externalUrls: Map<String, String>) {
        this.externalUrl = externalUrls.getOrDefault("spotify", "")
    }

    @JsonProperty("images")
    private fun unpackImage(images: ArrayList<Map<String, String?>>) {
        val imageMap = images.get(0)
        this.profilePicture = imageMap.getOrDefault("url", "")!!
    }

    override fun toString(): String {
        return "InfoResponse[id=$id, country=$country, displayName=$displayName, email=$email, href=$href, externalUrl=$externalUrl, profilePicture=$profilePicture]"
    }
}