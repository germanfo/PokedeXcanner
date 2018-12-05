package es.vass.pokedexcanner.data.repository.remote.responses

import com.google.gson.annotations.SerializedName

data class PokemonFormResponse(
    @SerializedName("form_name")
    val formName: String,
    @SerializedName("form_names")
    val formNames: List<Any>,
    @SerializedName("form_order")
    val formOrder: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_battle_only")
    val isBattleOnly: Boolean,
    @SerializedName("is_default")
    val isDefault: Boolean,
    @SerializedName("is_mega")
    val isMega: Boolean,
    @SerializedName("name")
    val name: String,
    @SerializedName("names")
    val names: List<Any>,
    @SerializedName("order")
    val order: Int,
    @SerializedName("pokemon")
    val pokemon: PokemonUrl,
    @SerializedName("sprites")
    val sprites: Sprites,
    @SerializedName("version_group")
    val versionGroup: VersionGroup
)