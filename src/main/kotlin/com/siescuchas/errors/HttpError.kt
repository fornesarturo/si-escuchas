package com.siescuchas.errors

import com.fasterxml.jackson.annotation.JsonProperty

class HttpError (
        var status: Int?,
        override var message: String?
): Error(message) {
    @JsonProperty("error")
    private fun unpackError(errorMap: Map<String, Any>) {
        this.status = errorMap.getOrDefault("status", 1) as? Int ?: 500
        this.message = errorMap.getOrDefault("message", "") as? String ?: ""
    }

}