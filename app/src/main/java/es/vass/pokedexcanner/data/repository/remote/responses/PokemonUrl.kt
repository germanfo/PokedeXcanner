package es.vass.pokedexcanner.data.repository.remote.responses

import com.google.gson.annotations.SerializedName

data class PokemonUrl(
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String
)