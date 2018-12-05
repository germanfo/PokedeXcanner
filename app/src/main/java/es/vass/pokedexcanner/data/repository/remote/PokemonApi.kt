package es.vass.pokedexcanner.data.repository.remote

import es.vass.pokedexcanner.data.repository.remote.responses.PokemonExtendedInfoResponse
import es.vass.pokedexcanner.data.repository.remote.responses.PokemonFormResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PokemonApi {

    @GET("pokemon-form/{id}")
    fun getPokemonInfo(@Path("id") idPokemon: Long):Call<PokemonFormResponse>

    @GET("pokemon/{name}")
    fun getPokemonExtendedInfo(@Path("name") pokemonName: String):Call<PokemonExtendedInfoResponse>

}