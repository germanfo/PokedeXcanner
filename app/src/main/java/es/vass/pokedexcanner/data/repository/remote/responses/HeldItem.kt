package es.vass.pokedexcanner.data.repository.remote.responses

import com.google.gson.annotations.SerializedName

data class HeldItem(
    @SerializedName("item")
    val item: Item,
    @SerializedName("version_details")
    val versionDetails: List<VersionDetail>
)