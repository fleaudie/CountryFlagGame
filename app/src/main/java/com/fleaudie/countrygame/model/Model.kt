package com.fleaudie.countrygame.model

import com.google.gson.annotations.SerializedName

data class Country(
    @SerializedName("translations")
    val translations: Map<String, Map<String, String>>?,
    @SerializedName("flags")
    val flags: Map<String, String>
)

