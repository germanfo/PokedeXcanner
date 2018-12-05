package es.vass.pokedexcanner.data.repository.remote

import android.arch.lifecycle.LiveData
import es.vass.pokedexcanner.data.repository.remote.responses.PokemonExtendedInfoResponse
import es.vass.pokedexcanner.data.repository.remote.responses.PokemonFormResponse

interface IRemoteDataSource {

    fun downloadPokemonViewedData(id: Long): LiveData<PokemonFormResponse>
    fun downloadPokemonCatchedData(name: String): LiveData<PokemonExtendedInfoResponse>
}